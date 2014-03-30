package sample.ble.sensortag.sensor;

import android.bluetooth.BluetoothGattCharacteristic;
import sample.ble.sensortag.ble.BleGattExecutor;

/**
 * Created by steven on 9/3/13.
 */
public class TiGyroscopeSensor extends TiRangeSensors<float[], Float> {

    public static final String UUID_SERVICE = "f000aa50-0451-4000-b000-000000000000";
    private static final String UUID_DATA = "f000aa51-0451-4000-b000-000000000000";
    private static final String UUID_CONFIG = "f000aa52-0451-4000-b000-000000000000";
    private static final String UUID_PERIOD = "f000aa53-0451-4000-b000-000000000000";

    private static final int PERIOD_MIN = 10;
    private static final int PERIOD_MAX = 255;

    private int period = 100;

    TiGyroscopeSensor() {
        super();
    }

    @Override
    public String getName() {
        return "Gyroscope";
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
    protected byte[] getConfigValues(boolean enable) {
        // NB: Gyroscope is special as it has a different "enable-code" from the other sensors.
        // Gyroscope is unique in that you can enable any combination of the the 3 axes when you
        // write to the configuration characteristic.
        // Write 0 to turn off gyroscope,
        //       1 to enable X axis only,
        //       2 to enable Y axis only,
        //       3 = X and Y,
        //       4 = Z only,
        //       5 = X and Z,
        //       6 = Y and Z,
        //       7 = X, Y and Z
        return new byte[] { (byte)(enable ? 7 : 0) };
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
    public float[] parse(BluetoothGattCharacteristic c) {
        // NB: x,y,z has a weird order.
        float y = TiSensorUtils.shortSignedAtOffset(c, 0) * (500f / 65536f) * -1;
        float x = TiSensorUtils.shortSignedAtOffset(c, 2) * (500f / 65536f);
        float z = TiSensorUtils.shortSignedAtOffset(c, 4) * (500f / 65536f);

        return new float[]{x, y, z};
    }
}
