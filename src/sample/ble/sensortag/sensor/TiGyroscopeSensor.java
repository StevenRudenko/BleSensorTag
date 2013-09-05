package sample.ble.sensortag.sensor;

import android.bluetooth.BluetoothGattCharacteristic;

/**
 * Created by steven on 9/3/13.
 */
public class TiGyroscopeSensor extends TiSensor<float[]> {

    public static final TiGyroscopeSensor INSTANCE = new TiGyroscopeSensor();

    private TiGyroscopeSensor() {
        super();
    }

    @Override
    public String getName() {
        return "Gyroscope";
    }

    @Override
    public String getServiceUUID() {
        return "f000aa50-0451-4000-b000-000000000000";
    }

    @Override
    public String getDataUUID() {
        return "f000aa51-0451-4000-b000-000000000000";
    }

    @Override
    public String getConfigUUID() {
        return "f000aa52-0451-4000-b000-000000000000";
    }
    //TODO: there is period service

    @Override
    protected byte[] getConfigValues(boolean enable) {
        return new byte[] { (byte)(enable ? 7 : 0) };
    }

    @Override
    public String toString(BluetoothGattCharacteristic c) {
        final float[] data = onCharacteristicChanged(c);
        return "x="+data[0]+"\ny="+data[1]+"\nz="+data[2];
    }

    public float[] onCharacteristicChanged(BluetoothGattCharacteristic c) {
        // NB: x,y,z has a weird order.
        float y = shortSignedAtOffset(c, 0) * (500f / 65536f) * -1;
        float x = shortSignedAtOffset(c, 2) * (500f / 65536f);
        float z = shortSignedAtOffset(c, 4) * (500f / 65536f);

        return new float[]{x, y, z};
    }
}
