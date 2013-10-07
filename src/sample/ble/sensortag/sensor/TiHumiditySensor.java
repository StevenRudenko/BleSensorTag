package sample.ble.sensortag.sensor;

import android.bluetooth.BluetoothGattCharacteristic;

/**
 * Created by steven on 9/3/13.
 */
public class TiHumiditySensor extends TiSensor<Float> {

    TiHumiditySensor() {
        super();
    }

    @Override
    public String getName() {
        return "Humidity";
    }

    @Override
    public String getServiceUUID() {
        return "f000aa20-0451-4000-b000-000000000000";
    }

    @Override
    public String getDataUUID() {
        return "f000aa21-0451-4000-b000-000000000000";
    }

    @Override
    public String getConfigUUID() {
        return "f000aa22-0451-4000-b000-000000000000";
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
