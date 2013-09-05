package sample.ble.sensortag.sensor;

import static android.bluetooth.BluetoothGattCharacteristic.FORMAT_SINT8;

import android.bluetooth.BluetoothGattCharacteristic;

/**
 * Created by steven on 9/3/13.
 */
public class TiAccelerometerSensor extends TiSensor<float[]> {

    public static final TiAccelerometerSensor INSTANCE = new TiAccelerometerSensor();

    private TiAccelerometerSensor() {
        super();
    }

    @Override
    public String getName() {
        return "Accelerometer";
    }

    @Override
    public String getServiceUUID() {
        return "f000aa10-0451-4000-b000-000000000000";
    }

    @Override
    public String getDataUUID() {
        return "f000aa11-0451-4000-b000-000000000000";
    }

    @Override
    public String getConfigUUID() {
        return "f000aa12-0451-4000-b000-000000000000";
    }
    //TODO: there is period service


    @Override
    public String toString(BluetoothGattCharacteristic c) {
        final float[] data = onCharacteristicChanged(c);
        return "x="+data[0]+"\ny="+data[1]+"\nz="+data[2];
    }

    public float[] onCharacteristicChanged(final BluetoothGattCharacteristic c) {
    /*
     * The accelerometer has the range [-2g, 2g] with unit (1/64)g.
     *
     * To convert from unit (1/64)g to unit g we divide by 64.
     *
     * (g = 9.81 m/s^2)
     *
     * The z value is multiplied with -1 to coincide
     * with how we have arbitrarily defined the positive y direction.
     * (illustrated by the apps accelerometer image)
     * */

        Integer x = c.getIntValue(FORMAT_SINT8, 0);
        Integer y = c.getIntValue(FORMAT_SINT8, 1);
        Integer z = c.getIntValue(FORMAT_SINT8, 2) * -1;

        double scaledX = x / 64.0;
        double scaledY = y / 64.0;
        double scaledZ = z / 64.0;

        return new float[]{(float)scaledX, (float)scaledY, (float)scaledZ};
    }
}
