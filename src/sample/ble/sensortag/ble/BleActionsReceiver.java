package sample.ble.sensortag.ble;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import sample.ble.sensortag.BleService;

public class BleActionsReceiver extends BroadcastReceiver {

    private final BleServiceListener bleServiceListener;

    public BleActionsReceiver(BleServiceListener listener) {
        bleServiceListener = listener;
    }

    /**
     * Handles various events fired by the Service.
     * ACTION_GATT_CONNECTED: connected to a GATT server.
     * ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
     * ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
     * ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
     *                        or notification operations.
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (BleService.ACTION_GATT_CONNECTED.equals(action)) {
            bleServiceListener.onConnected();
        } else if (BleService.ACTION_GATT_DISCONNECTED.equals(action)) {
            bleServiceListener.onDisconnected();
        } else if (BleService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
            bleServiceListener.onServiceDiscovered();
        } else if (BleService.ACTION_DATA_AVAILABLE.equals(action)) {
            final String serviceUuid = intent.getStringExtra(BleService.EXTRA_SERVICE_UUID);
            final String characteristicUUid = intent.getStringExtra(BleService.EXTRA_CHARACTERISTIC_UUID);
            final String text = intent.getStringExtra(BleService.EXTRA_TEXT);
            final byte[] data = intent.getExtras().getByteArray(BleService.EXTRA_DATA);
            bleServiceListener.onDataAvailable(serviceUuid, characteristicUUid, text, data);
        }
    }

    public static IntentFilter createIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BleService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BleService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BleService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BleService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

}
