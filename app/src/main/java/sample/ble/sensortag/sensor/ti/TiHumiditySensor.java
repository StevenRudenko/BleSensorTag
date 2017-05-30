package sample.ble.sensortag.sensor.ti;

import android.bluetooth.BluetoothGattCharacteristic;

import sample.ble.sensortag.sensor.BaseSensor;

/** TI humidity sensor. */
public class TiHumiditySensor extends BaseSensor<TiSensorTag> {
    /** Service UUID. */
    private static final String UUID_SERVICE = "f000aa20-0451-4000-b000-000000000000";
    /** Data UUID. */
    private static final String UUID_DATA = "f000aa21-0451-4000-b000-000000000000";
    /** Configuration UUID. */
    private static final String UUID_CONFIG = "f000aa22-0451-4000-b000-000000000000";

    TiHumiditySensor(TiSensorTag model) {
        super(model);
    }

    @Override
    public String getName() {
        return "Humidity";
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
        final float data = getData().getHumidity();
        return ""+data;
    }

    @Override
    protected boolean apply(final BluetoothGattCharacteristic c, final TiSensorTag data) {
        int a = TiUtils.shortUnsignedAtOffset(c, 2);
        // bits [1..0] are status bits and need to be cleared according
        // to the userguide, but the iOS code doesn't bother. It should
        // have minimal impact.
        a = a - (a % 4);

        data.setHumidity(-6f + 125f * (a / 65535f));
        return true;
    }

}
