package sample.ble.sensortag.sensor;

import android.bluetooth.BluetoothGattCharacteristic;

/**
 * Created by steven on 9/3/13.
 */
public class TiHumiditySensor extends TiSensor<Float> {

    private static final String UUID_SERVICE = "f000aa20-0451-4000-b000-000000000000";
    private static final String UUID_DATA = "f000aa21-0451-4000-b000-000000000000";
    private static final String UUID_CONFIG = "f000aa22-0451-4000-b000-000000000000";

    TiHumiditySensor() {
        super();
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
        final float data = getData();
        return ""+data;
    }

    @Override
    public Float parse(BluetoothGattCharacteristic c) {
        int a = TiSensorUtils.shortUnsignedAtOffset(c, 2);
        // bits [1..0] are status bits and need to be cleared according
        // to the userguide, but the iOS code doesn't bother. It should
        // have minimal impact.
        a = a - (a % 4);

        return (-6f) + 125f * (a / 65535f);
    }

}
