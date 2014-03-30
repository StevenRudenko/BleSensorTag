package sample.ble.sensortag.fusion.sensors;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;
import sample.ble.sensortag.ble.BleManager;
import sample.ble.sensortag.ble.BleServiceListener;
import sample.ble.sensortag.sensor.TiRangeSensors;
import sample.ble.sensortag.sensor.TiSensor;
import sample.ble.sensortag.sensor.TiSensors;

public class BleSensorManager extends ISensorManager implements BleServiceListener {
    private static final String TAG = BleSensorManager.class.getSimpleName();

    // experimentally selected value
    private static final double SENSOR_CALIBRATION = 75f;
    private static final double SENSOR_FUSION_COEFF = -180f / Math.PI;
    private final double[] fusedOrientation = new double[3];

    private final Context context;
    private final BleManager bleManager = new BleManager();

    private final String deviceAddress;
    private final SparseArray<BleSensor> sensors = new SparseArray<BleSensor>();

    private boolean isConnected = false;

    public BleSensorManager(Context context, String deviceAddress) {
        this.context = context;
        this.deviceAddress = deviceAddress;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ISensor getSensor(int sensorType) {
        if (sensors.get(sensorType) != null)
            return sensors.get(sensorType);

        final TiSensor<?> sensor = TiSensors.getSensor(BleSensor.getSensorUuid(sensorType));
        if (sensor instanceof TiRangeSensors<?, ?>) {
            final BleSensor result = new BleSensor((TiRangeSensors<float[], Float>) sensor);
            return result;
        }
        throw new IllegalStateException();
    }

    @Override
    public void enable() {
        Log.d(TAG, "enable");
        bleManager.setServiceListener(BleSensorManager.this);
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

    @Override
    public void onConnected() {
        Log.d(TAG, "connected");
    }

    @Override
    public void onDisconnected() {
        Log.d(TAG, "disconnected");
    }

    @Override
    public void onServiceDiscovered() {
        isConnected = true;
        Log.d(TAG, "services discovered");
        final int count = sensors.size();
        for (int i=0; i<count; ++i) {
            final BleSensor sensor = sensors.valueAt(i);
            enableTiSensor(sensor.getType(), true);
        }
    }

    @Override
    public void onDataAvailable(String serviceUuid, String characteristicUUid, String text, byte[] data) {
        if (listener == null)
            return;

        @SuppressWarnings("unchecked")
        final TiRangeSensors<float[], Float> sensor =
                (TiRangeSensors<float[], Float>) TiSensors.getSensor(serviceUuid);

        final int sensorType = BleSensor.getSensorType(serviceUuid);
        final float[] values = sensor.getData();
        calibrate(values);
        listener.onSensorChanged(sensorType, values);
    }

    private void enableTiSensor(int sensorType, boolean enable) {
        if (bleManager.getState() != BleManager.STATE_CONNECTED)
            return;
        if (!isConnected)
            return;

        final BleSensor sensor = (BleSensor) getSensor(sensorType);
        final TiRangeSensors<?, ?> tiSensors = sensor.getTiSensor();
        tiSensors.setPeriod(tiSensors.getMinPeriod());
        bleManager.enableSensor(tiSensors, enable);
        if (enable)
            bleManager.updateSensor(tiSensors);
        Log.d(TAG, (enable ? "enable" : "disable") + " sensor: " + tiSensors.getName());
    }

    private static void calibrate(float[] values) {
        values[0] /= SENSOR_CALIBRATION;
        values[1] /= SENSOR_CALIBRATION;
        values[2] /= SENSOR_CALIBRATION;
    }
}
