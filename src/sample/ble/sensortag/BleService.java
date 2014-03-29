package sample.ble.sensortag;

import android.app.Service;
import android.bluetooth.BluetoothGattService;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import sample.ble.sensortag.ble.BleManager;
import sample.ble.sensortag.ble.BleServiceListener;
import sample.ble.sensortag.config.AppConfig;
import sample.ble.sensortag.sensor.TiSensor;

import java.util.List;

/**
 * Service for managing connection and data communication with a GATT server hosted on a
 * given Bluetooth LE device.
 */
public class BleService extends Service implements BleServiceListener {
    @SuppressWarnings("UnusedDeclaration")
    private final static String TAG = BleService.class.getSimpleName();

    private final static String INTENT_PREFIX = BleService.class.getPackage().getName();
    public final static String ACTION_GATT_CONNECTED = INTENT_PREFIX+".ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED = INTENT_PREFIX+".ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED = INTENT_PREFIX+".ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE = INTENT_PREFIX+".ACTION_DATA_AVAILABLE";
    public final static String EXTRA_SERVICE_UUID = INTENT_PREFIX+".EXTRA_SERVICE_UUID";
    public final static String EXTRA_CHARACTERISTIC_UUID = INTENT_PREFIX+".EXTRA_CHARACTERISTIC_UUI";
    public final static String EXTRA_DATA = INTENT_PREFIX+".EXTRA_DATA";
    public final static String EXTRA_TEXT = INTENT_PREFIX+".EXTRA_TEXT";

    public class LocalBinder extends Binder {
        public BleService getService() {
            return BleService.this;
        }
    }

    private final IBinder binder = new LocalBinder();

    private final Handler uiThreadHandler = new Handler(Looper.getMainLooper());
    private final BleManager bleManager = new BleManager();
    private BleServiceListener serviceListener;

    @Override
    public void onCreate() {
        super.onCreate();

        bleManager.setServiceListener(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.
        bleManager.close();
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        bleManager.disconnect();
        bleManager.close();
    }

    public void updateSensor(TiSensor<?> sensor) {
        bleManager.updateSensor(sensor);
    }

    public BleManager getBleManager() {
        return bleManager;
    }

    public void setServiceListener(BleServiceListener listener) {
        serviceListener = listener;
    }

    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param sensor sensor to be enabled/disabled
     * @param enabled If true, enable notification.  False otherwise.
     */
    public void enableSensor(TiSensor<?> sensor, boolean enabled) {
        bleManager.enableSensor(sensor, enabled);
    }

    /**
     * Retrieves a list of supported GATT services on the connected device. This should be
     * invoked only after {@code BluetoothGatt#discoverServices()} completes successfully.
     *
     * @return A {@code List} of supported services.
     */
    public List<BluetoothGattService> getSupportedGattServices() {
        return bleManager.getSupportedGattServices();
    }

    @Override
    public void onConnected() {
        broadcastUpdate(ACTION_GATT_CONNECTED);
        uiThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (serviceListener != null)
                    serviceListener.onConnected();
            }
        });
    }

    @Override
    public void onDisconnected() {
        broadcastUpdate(ACTION_GATT_DISCONNECTED);
        uiThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (serviceListener != null)
                    serviceListener.onDisconnected();
            }
        });
    }

    @Override
    public void onServiceDiscovered() {
        broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);

        uiThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (serviceListener != null)
                    serviceListener.onServiceDiscovered();
            }
        });
    }

    @Override
    public void onDataAvailable(final String serviceUuid, final String characteristicUuid,
                                final String text, final byte[] data) {
        final Intent intent = new Intent(ACTION_DATA_AVAILABLE);
        intent.putExtra(EXTRA_SERVICE_UUID, serviceUuid);
        intent.putExtra(EXTRA_CHARACTERISTIC_UUID, characteristicUuid);
        intent.putExtra(EXTRA_TEXT, text);
        intent.putExtra(EXTRA_DATA, data);

        if (AppConfig.REMOTE_BLE_SERVICE)
            sendBroadcast(intent);
        uiThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (serviceListener != null)
                    serviceListener.onDataAvailable(serviceUuid, characteristicUuid, text, data);
            }
        });
    }

    private void broadcastUpdate(final String action) {
        if (!AppConfig.REMOTE_BLE_SERVICE)
            return;

        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }
}
