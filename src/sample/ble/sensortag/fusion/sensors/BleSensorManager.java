package sample.ble.sensortag.fusion.sensors;

import com.chimeraiot.android.ble.BleManager;
import com.chimeraiot.android.ble.BleServiceListener;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;

import sample.ble.sensortag.App;
import sample.ble.sensortag.config.AppConfig;
import sample.ble.sensortag.sensor.BaseSensor;
import sample.ble.sensortag.sensor.ti.TiRangeSensors;
import sample.ble.sensortag.sensor.ti.TiSensorTag;

/** BLE fusion sensor manager. */
public class BleSensorManager extends ISensorManager implements BleServiceListener {
    /** Log tag. */
    private static final String TAG = BleSensorManager.class.getSimpleName();

    // experimentally selected value
    private static final double SENSOR_CALIBRATION = 75f;

    private static final double SENSOR_FUSION_COEFF = -180f / Math.PI;

    private final double[] fusedOrientation = new double[3];

    private final Context context;

    private final BleManager bleManager;

    private final String deviceAddress;

    private final SparseArray<BleSensor> sensors = new SparseArray<>();

    private boolean isConnected = false;

    public BleSensorManager(Context context, String deviceAddress) {
        this.context = context;
        bleManager = new BleManager(App.DEVICE_DEF_COLLECTION);
        this.deviceAddress = deviceAddress;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ISensor getSensor(int sensorType) {
        if (sensors.get(sensorType) != null) {
            return sensors.get(sensorType);
        }

        final BaseSensor<?> sensor = (BaseSensor<?>) App.DEVICE_DEF_COLLECTION
                .get(AppConfig.SENSOR_TAG_DEVICE_NAME, deviceAddress)
                .getSensor(BleSensor.getSensorUuid(sensorType));
        if (sensor instanceof TiRangeSensors<?, ?>) {
            return new BleSensor((TiRangeSensors<float[], Float>) sensor);
        }
        throw new IllegalStateException();
    }

    @Override
    public void enable() {
        Log.d(TAG, "enable");
        bleManager.registerListener(this);
        if (!bleManager.initialize(context)) {
            Log.e(TAG, "Unable to initialize Bluetooth");
            throw new SensorManagerException();
        }
        // Automatically connects to the device upon successful start-up initialization.
        Log.d(TAG, "connecting: " + deviceAddress);
        bleManager.connect(context, deviceAddress);
    }

    @Override
    public void disable() {
        Log.d(TAG, "disable");
        isConnected = false;
        bleManager.unregisterListener(this);
        bleManager.disconnect();
        bleManager.close();
    }

    @Override
    public void registerSensor(int sensorType) {
        final BleSensor sensor = (BleSensor) getSensor(sensorType);
        sensors.put(sensorType, sensor);
        enableTiSensor(sensorType, true);
    }

    @Override
    public void unregisterSensor(int sensorType) {
        enableTiSensor(sensorType, false);
        sensors.remove(sensorType);
    }

    @Override
    public double[] patchSensorFusion(float[] values) {
        fusedOrientation[0] = values[1] * SENSOR_FUSION_COEFF;
        fusedOrientation[1] = values[2] * SENSOR_FUSION_COEFF;
        fusedOrientation[2] = values[0] * SENSOR_FUSION_COEFF;
        return fusedOrientation;
    }

    private void enableTiSensor(int sensorType, boolean enable) {
        if (!bleManager.isReady()) {
            return;
        }
        if (!isConnected) {
            return;
        }

        final BleSensor sensor = (BleSensor) getSensor(sensorType);
        final TiRangeSensors<?, ?> tiSensors = sensor.getTiSensor();
        tiSensors.setPeriod(tiSensors.getMinPeriod());
        tiSensors.setEnabled(enable);
        bleManager.update(deviceAddress, tiSensors, tiSensors.getConfigUUID(), null);
        if (enable) {
            bleManager.listen(deviceAddress, tiSensors, tiSensors.getDataUUID());
        }
        Log.d(TAG, (enable ? "enable" : "disable") + " sensor: " + tiSensors.getName());
    }

    @Override
    public void onConnected(final String name, final String address) {
        Log.d(TAG, "connected: " + name);
    }

    @Override
    public void onConnectionFailed(String name, String address, int status, int state) {
        Log.d(TAG, "connection failed: " + name + " status=" + status + " state=" + state);
    }

    @Override
    public void onDisconnected(final String name, final String address) {
        Log.d(TAG, "disconnected: " + name);
    }

    @Override
    public void onServiceDiscovered(final String name, final String address) {
        isConnected = true;
        Log.d(TAG, "services discovered: " + name);
        final int count = sensors.size();
        for (int i = 0; i < count; ++i) {
            final BleSensor sensor = sensors.valueAt(i);
            enableTiSensor(sensor.getType(), true);
        }
    }

    @Override
    public void onCharacteristicChanged(final String name, final String address,
            final String serviceUuid,
            final String characteristicUuid) {
        if (listener == null) {
            return;
        }

        @SuppressWarnings("unchecked")
        final TiRangeSensors<TiSensorTag, Float> sensor =
                (TiRangeSensors<TiSensorTag, Float>) bleManager.getDeviceDefCollection()
                        .get(name, address).getSensor(serviceUuid);
        final int sensorType = BleSensor.getSensorType(serviceUuid);
        final TiSensorTag sensorTag = sensor.getData();
        final float[] values = new float[3];
        switch (sensorType) {
            case ISensor.TYPE_ACCELEROMETER:
                System.arraycopy(sensorTag.getAccel(), 0, values, 0, 3);
                break;
            case ISensor.TYPE_MAGNETIC_FIELD:
                System.arraycopy(sensorTag.getMagnet(), 0, values, 0, 3);
                break;
            case ISensor.TYPE_GYROSCOPE:
                System.arraycopy(sensorTag.getGyro(), 0, values, 0, 3);
                break;
            default:
                return;
        }
        calibrate(values);
        listener.onSensorChanged(sensorType, values);
    }

    private static void calibrate(float[] values) {
        values[0] /= SENSOR_CALIBRATION;
        values[1] /= SENSOR_CALIBRATION;
        values[2] /= SENSOR_CALIBRATION;
    }
}
