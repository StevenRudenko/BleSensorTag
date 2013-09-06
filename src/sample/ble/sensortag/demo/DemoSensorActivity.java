package sample.ble.sensortag.demo;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.MenuItem;

import sample.ble.sensortag.R;
import sample.ble.sensortag.sensor.TiSensor;
import sample.ble.sensortag.sensor.TiSensors;
import sample.ble.sensortag.service.BluetoothLeService;

/**
 * Created by steven on 9/5/13.
 */
public abstract class DemoSensorActivity extends Activity {
    private final static String TAG = DemoSensorActivity.class.getSimpleName();

    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    public static final String EXTRAS_SENSOR_UUID = "SERVICE_UUID";

    private BluetoothLeService bluetoothLeService;
    private String serviceUuid;
    private String deviceAddress;

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private final BroadcastReceiver gattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                //TODO: show toast
                finish();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                final TiSensor<?> sensor = TiSensors.getSensor(serviceUuid);
                bluetoothLeService.setCharacteristicNotification(sensor, true);
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                final byte[] data = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
                if (data == null || data.length == 0)
                    return;

                final TiSensor<?> sensor = TiSensors.getSensor(serviceUuid);
                final String text = intent.getStringExtra(BluetoothLeService.EXTRA_TEXT);
                onDataRecieved(sensor, data, text);
            }
        }
    };

    // Code to manage Service lifecycle.
    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            bluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!bluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            bluetoothLeService.connect(deviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            bluetoothLeService = null;
            //TODO: show toast
            finish();
        }
    };

    public abstract void onDataRecieved(TiSensor<?> sensor, byte[] data, String text);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Intent intent = getIntent();
        deviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);
        serviceUuid = intent.getStringExtra(EXTRAS_SENSOR_UUID);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        final Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, serviceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        registerReceiver(gattUpdateReceiver, makeGattUpdateIntentFilter());
        if (bluetoothLeService != null) {
            final boolean result = bluetoothLeService.connect(deviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(gattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
        bluetoothLeService = null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }
}
