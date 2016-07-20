package sample.ble.sensortag;

import com.chimeraiot.android.ble.BleManager;
import com.chimeraiot.android.ble.BleScanner;
import com.chimeraiot.android.ble.BleService;
import com.chimeraiot.android.ble.BleUtils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import sample.ble.sensortag.config.AppConfig;
import sample.ble.sensortag.sensor.BaseSensor;
import sample.ble.sensortag.sensor.ti.TiAccelerometerSensor;

/** Record sensor data exmaple service. */
public class BleSensorsRecordService extends BleService {
    /** Log tag. */
    private static final String TAG = BleSensorsRecordService.class.getSimpleName();

    /** Record device name. */
    private static final String RECORD_DEVICE_NAME = "SensorTag";
    /** Sensor UUID. Used to listen for updates. */
    private static final String SENSOR_TO_READ = TiAccelerometerSensor.UUID_SERVICE;

    /** BLE device scanner. */
    private BleScanner scanner;

    @Override
    public void onCreate() {
        super.onCreate();
        //noinspection PointlessBooleanExpression
        if (!AppConfig.ENABLE_RECORD_SERVICE) {
            stopSelf();
            return;
        }

        final int bleStatus = BleUtils.getBleStatus(getBaseContext());
        switch (bleStatus) {
            case BleUtils.STATUS_BLE_NOT_AVAILABLE:
                Toast.makeText(getApplicationContext(), R.string.dialog_error_no_ble, Toast.LENGTH_SHORT).show();
                stopSelf();
                return;
            case BleUtils.STATUS_BLUETOOTH_NOT_AVAILABLE:
                Toast.makeText(getApplicationContext(), R.string.dialog_error_no_bluetooth, Toast.LENGTH_SHORT).show();
                stopSelf();
                return;
            default:
                break;
        }

        // initialize scanner
        final BluetoothAdapter bluetoothAdapter = BleUtils.getBluetoothAdapter(getBaseContext());
        scanner = new BleScanner(bluetoothAdapter, new BleScanner.BleScannerListener() {
            @Override
            public void onScanStarted() {
            }

            @Override
            public void onScanRepeat() {
            }

            @Override
            public void onScanStopped() {
            }

            @Override
            public void onLeScan(BluetoothDevice device, int i, byte[] bytes) {
                Log.d(TAG, "Device discovered: " + device.getName());
                if (RECORD_DEVICE_NAME.equals(device.getName())) {
                    scanner.stop();
                    getBleManager().connect(getBaseContext(), device.getAddress());
                }
            }
        });

        setServiceListener(this);
    }

    @Override
    protected BleManager createBleManager() {
        return new BleManager(App.DEVICE_DEF_COLLECTION);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (scanner == null)
            return super.onStartCommand(intent, flags, startId);
        Log.d(TAG, "Service started");
        scanner.start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Service stopped");
        setServiceListener(null);
        if (scanner != null)
            scanner.stop();
    }

    @Override
    public void onConnected(final String name, final String address) {
        super.onConnected(name, address);
        Log.d(TAG, "Connected");
    }

    @Override
    public void onDisconnected(final String name, final String address) {
        super.onDisconnected(name, address);
        Log.d(TAG, "Disconnected");
        scanner.start();
    }

    @Override
    public void onServiceDiscovered(final String name, final String address) {
        super.onServiceDiscovered(name, address);
        Log.d(TAG, "Service discovered");

        final BaseSensor<?> sensor = (BaseSensor<?>) getBleManager().getDeviceDefCollection()
                .get(name, address).getSensor(SENSOR_TO_READ);
        if (sensor != null) {
            sensor.setEnabled(true);
            getBleManager().update(address, sensor, sensor.getConfigUUID(), null);
            getBleManager().listen(address, sensor, sensor.getDataUUID());
        }
    }

    @Override
    public void onCharacteristicChanged(final String name, final String address,
            final String serviceUuid,
            final String characteristicUuid) {
        super.onCharacteristicChanged(name, address, serviceUuid, characteristicUuid);
        Log.d(TAG, "Service='" + serviceUuid + " characteristic=" + characteristicUuid);
    }

}
