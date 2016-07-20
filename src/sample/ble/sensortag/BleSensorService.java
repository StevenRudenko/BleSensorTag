package sample.ble.sensortag;

import com.chimeraiot.android.ble.BleManager;

/** BLE service. */
public class BleSensorService extends com.chimeraiot.android.ble.BleService {

    @Override
    protected BleManager createBleManager() {
        return new BleManager(App.DEVICE_DEF_COLLECTION);
    }
}
