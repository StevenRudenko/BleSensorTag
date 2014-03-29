package sample.ble.sensortag;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import sample.ble.sensortag.ble.BleDevicesScanner;
import sample.ble.sensortag.ble.BleUtils;
import sample.ble.sensortag.config.AppConfig;
import sample.ble.sensortag.sensor.TiAccelerometerSensor;
import sample.ble.sensortag.sensor.TiSensor;
import sample.ble.sensortag.sensor.TiSensors;

public class BleSensorsRecordService extends BleService {
    private static final String TAG = BleSensorsRecordService.class.getSimpleName();

    private static final String RECORD_DEVICE_NAME = "SensorTag";

    private final TiSensor<?> sensorToRead = TiSensors.getSensor(TiAccelerometerSensor.UUID_SERVICE);
    private BleDevicesScanner scanner;

    @Override
    public void onCreate() {
        super.onCreate();

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

        if (!getBleManager().initialize(getBaseContext())) {
            stopSelf();
            return;
        }

        // initialize scanner
        final BluetoothAdapter bluetoothAdapter = BleUtils.getBluetoothAdapter(getBaseContext());
        scanner = new BleDevicesScanner(bluetoothAdapter, new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                Log.d(TAG, "Device discovered: " + device.getName());
                if (RECORD_DEVICE_NAME.equals(RECORD_DEVICE_NAME)) {
                    scanner.stop();
                    getBleManager().connect(getBaseContext(), device.getAddress());
                }
            }
        });

        setServiceListener(this);
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
    public void onConnected() {
        Log.d(TAG, "Connected");
    }

    @Override
    public void onDisconnected() {
        Log.d(TAG, "Disconnected");
        scanner.start();
    }

    @Override
    public void onServiceDiscovered() {
        Log.d(TAG, "Service discovered");
        enableSensor(sensorToRead, true);
    }

    @Override
    public void onDataAvailable(String serviceUuid, String characteristicUUid, String text, byte[] data) {
        Log.d(TAG, "Data='" + text + "'");
        //TODO: put your record code here. Please note that it is not main thread.
    }
}
