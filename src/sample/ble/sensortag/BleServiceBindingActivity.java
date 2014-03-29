package sample.ble.sensortag;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import sample.ble.sensortag.config.AppConfig;
import sample.ble.sensortag.ble.BleActionsReceiver;
import sample.ble.sensortag.ble.BleServiceListener;

public class BleServiceBindingActivity extends Activity
                                       implements BleServiceListener,
                                                  ServiceConnection {
    private final static String TAG = BleServiceBindingActivity.class.getSimpleName();

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    private String deviceName;
    private String deviceAddress;
    private BleService bleService;
    @SuppressWarnings("ConstantConditions")
    private BroadcastReceiver bleActionsReceiver =
            AppConfig.REMOTE_BLE_SERVICE ? new BleActionsReceiver(this) : null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Intent intent = getIntent();
        deviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        deviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (AppConfig.REMOTE_BLE_SERVICE)
            registerReceiver(bleActionsReceiver, BleActionsReceiver.createIntentFilter());
        final Intent gattServiceIntent = new Intent(this, BleService.class);
        bindService(gattServiceIntent, this, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (bleService != null)
            bleService.getBleManager().disconnect();
        if (AppConfig.REMOTE_BLE_SERVICE)
            unregisterReceiver(bleActionsReceiver);
        unbindService(this);
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getDeviceAddress() {
        return deviceAddress;
    }

    public BleService getBleService() {
        return bleService;
    }

    @Override
    public void onConnected() {
    }

    @Override
    public void onDisconnected() {
    }

    @Override
    public void onServiceDiscovered() {
    }

    @Override
    public void onDataAvailable(String serviceUuid, String characteristicUUid, String text, byte[] data) {
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        bleService = ((BleService.LocalBinder) service).getService();
        //noinspection PointlessBooleanExpression,ConstantConditions
        if (!AppConfig.REMOTE_BLE_SERVICE)
            bleService.setServiceListener(this);
        if (!bleService.getBleManager().initialize(getBaseContext())) {
            Log.e(TAG, "Unable to initialize Bluetooth");
            finish();
            return;
        }

        // Automatically connects to the device upon successful start-up initialization.
        bleService.getBleManager().connect(getBaseContext(), deviceAddress);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        bleService = null;
        //TODO: show toast
    }
}
