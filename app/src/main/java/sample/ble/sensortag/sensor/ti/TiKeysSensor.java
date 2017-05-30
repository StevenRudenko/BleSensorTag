package sample.ble.sensortag.sensor.ti;

import android.bluetooth.BluetoothGattCharacteristic;

import sample.ble.sensortag.sensor.BaseSensor;

import static android.bluetooth.BluetoothGattCharacteristic.FORMAT_UINT8;

/** TI key sensor. */
public class TiKeysSensor extends BaseSensor<TiSensorTag> {

    private static final String UUID_SERVICE = "0000ffe0-0451-4000-b000-000000000000";
    private static final String UUID_DATA = "0000ffe1-0451-4000-b000-000000000000";
    private static final String UUID_CONFIG = null;

    TiKeysSensor(TiSensorTag model) {
        super(model);
    }

    @Override
    protected boolean apply(BluetoothGattCharacteristic c, TiSensorTag data) {
        /*
         * The key state is encoded into 1 unsigned byte.
         * bit 0 designates the right key.
         * bit 1 designates the left key.
         * bit 2 designates the side key.
         *
         * Weird, in the userguide left and right are opposite.
         */
        final int encodedInteger = c.getIntValue(FORMAT_UINT8, 0);
        data.setStatus(TiSensorTag.KeysStatus.valueAt(encodedInteger % 4));
        return true;
    }

    @Override
    public String getName() {
        return "Simple Keys";
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
        return getData().getKeyStatus().name();
    }

}
