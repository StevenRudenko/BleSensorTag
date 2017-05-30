package sample.ble.sensortag.sensor.ti;

import android.bluetooth.BluetoothGattCharacteristic;

import sample.ble.sensortag.sensor.BaseSensor;

/** TI test sensor. */
public class TiTestSensor extends BaseSensor<TiSensorTag> {
    /** Service UUID. */
    private static final String UUID_SERVICE = "f000aa60-0451-4000-b000-000000000000";
    /** Data UUID. */
    private static final String UUID_DATA = "f000aa61-0451-4000-b000-000000000000";
    /** Config UUID. */
    private static final String UUID_CONFIG = "f000aa62-0451-4000-b000-000000000000";

    TiTestSensor(TiSensorTag model) {
        super(model);
    }

    @Override
    public String getName() {
        return "Test";
    }

    @Override
    public String getServiceUUID() {
        return UUID_SERVICE;
    }

    @Override
    public String getDataUUID() {
        return UUID_DATA;
    }

    @Override
    public String getConfigUUID() {
        return UUID_CONFIG;
    }

    @Override
    public String getDataString() {
        return "";
    }

    @Override
    protected boolean apply(final BluetoothGattCharacteristic c, final TiSensorTag data) {
        return false;
    }

}
