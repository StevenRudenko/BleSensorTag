package sample.ble.sensortag.sensor.ti;

import com.chimeraiot.android.ble.BleGattExecutor;

import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Bundle;

/** TI gyroscope sensor. */
public class TiGyroscopeSensor extends TiRangeSensors<TiSensorTag, Float> {
    /** Service UUID. */
    public static final String UUID_SERVICE = "f000aa50-0451-4000-b000-000000000000";
    /** Data UUID. */
    private static final String UUID_DATA = "f000aa51-0451-4000-b000-000000000000";
    /** Configuration UUID. */
    private static final String UUID_CONFIG = "f000aa52-0451-4000-b000-000000000000";
    /** Period UUID. */
    private static final String UUID_PERIOD = "f000aa53-0451-4000-b000-000000000000";

    /** Min period value. */
    public static final int PERIOD_MIN = 1;
    /** Max period value. */
    public static final int PERIOD_MAX = 255;

    /** Period. */
    private int period = 100;

    TiGyroscopeSensor(TiSensorTag model) {
        super(model);
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
    public String getPeriodUUID() {
        return UUID_PERIOD;
    }

    @Override
    public boolean isConfigUUID(String uuid) {
        //noinspection SimplifiableIfStatement
        if (uuid.equals(UUID_PERIOD)) {
            return true;
        }
        return super.isConfigUUID(uuid);
    }

    @Override
    public String getCharacteristicName(String uuid) {
        switch (uuid) {
            case UUID_DATA:
                return "Data";
            case UUID_PERIOD:
                return "Period";
            default:
                return super.getCharacteristicName(uuid);
        }
    }

    @Override
    public String getDataString() {
        final float[] data = getData().getGyro();
        return TiUtils.coordinatesToString(data);
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
    public BleGattExecutor.ServiceAction[] update(final String uuid, final Bundle data) {
        switch (uuid) {
            case UUID_PERIOD:
                return new BleGattExecutor.ServiceAction[]{
                        write(uuid, new byte[]{(byte) period})
                };
            case UUID_CONFIG:
                return new BleGattExecutor.ServiceAction[]{
                        write(uuid, new byte[] {
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
                                (byte)(isEnabled() ? 7 : 0)
                        })
                };
            default:
                return super.update(uuid, data);
        }
    }

    @Override
    protected boolean apply(BluetoothGattCharacteristic c, TiSensorTag data) {
        final String uuid = c.getUuid().toString();
        switch (uuid) {
            case UUID_PERIOD:
                period = TiUtils.shortUnsignedAtOffset(c, 0);
                return true;
            case UUID_DATA:
                // NB: x,y,z has a weird order.
                float y = TiUtils.shortSignedAtOffset(c, 0) * (500f / 65536f) * -1;
                float x = TiUtils.shortSignedAtOffset(c, 2) * (500f / 65536f);
                float z = TiUtils.shortSignedAtOffset(c, 4) * (500f / 65536f);

                final float[] values = data.getGyro();
                values[0] = x;
                values[1] = y;
                values[2] = z;
                return true;
            default:
                return false;
        }
    }

}
