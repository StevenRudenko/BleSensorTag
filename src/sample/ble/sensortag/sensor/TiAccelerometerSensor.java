package sample.ble.sensortag.sensor;

import android.bluetooth.BluetoothGattCharacteristic;
import sample.ble.sensortag.ble.BleGattExecutor;

import static android.bluetooth.BluetoothGattCharacteristic.FORMAT_SINT8;

/**
 * Created by steven on 9/3/13.
 */
public class TiAccelerometerSensor extends TiRangeSensors<float[], Float> {

    public static final String UUID_SERVICE = "f000aa10-0451-4000-b000-000000000000";
    private static final String UUID_DATA = "f000aa11-0451-4000-b000-000000000000";
    private static final String UUID_CONFIG = "f000aa12-0451-4000-b000-000000000000";
    private static final String UUID_PERIOD = "f000aa13-0451-4000-b000-000000000000";

    private static final int PERIOD_MIN = 10;
    private static final int PERIOD_MAX = 255;

    private int period = 100;

    TiAccelerometerSensor() {
        super();
    }

    @Override
    public String getName() {
        return "Accelerometer";
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
    public boolean isConfigUUID(String uuid) {
        if (uuid.equals(UUID_PERIOD))
            return true;
        return super.isConfigUUID(uuid);
    }

    @Override
    public String getCharacteristicName(String uuid) {
        if (UUID_PERIOD.equals(uuid))
            return getName() + " Period";
        return super.getCharacteristicName(uuid);
    }

    @Override
    public String getDataString() {
        final float[] data = getData();
        return TiSensorUtils.coordinatesToString(data);
    }

    @Override
    public int getMinPeriod() {
        return PERIOD_MIN;
    }

    @Override
    public int getMaxPeriod() {
        return PERIOD_MAX;
    }

    @Override
    public void setPeriod(int period) {
        this.period = period;
    }

    @Override
    public int getPeriod() {
        return period;
    }

    @Override
    public Float getMaxRange() {
        return 1.f;
    }

    @Override
    public BleGattExecutor.ServiceAction update() {
        return write(UUID_PERIOD, new byte[]{(byte) period});
    }

    @Override
    public boolean onCharacteristicRead(BluetoothGattCharacteristic c) {
        super.onCharacteristicRead(c);

        if ( !c.getUuid().toString().equals(UUID_PERIOD) )
            return false;

        period = TiSensorUtils.shortUnsignedAtOffset(c, 0);
        return true;
    }

    @Override
    public float[] parse(final BluetoothGattCharacteristic c) {
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
        Integer z = -1 * c.getIntValue(FORMAT_SINT8, 2);

        double scaledX = x / 64.0;
        double scaledY = y / 64.0;
        double scaledZ = z / 64.0;

        return new float[]{(float)scaledX, (float)scaledY, (float)scaledZ};
    }

}
