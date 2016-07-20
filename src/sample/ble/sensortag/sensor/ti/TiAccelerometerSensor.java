package sample.ble.sensortag.sensor.ti;

import com.chimeraiot.android.ble.BleGattExecutor;

import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Bundle;

import static android.bluetooth.BluetoothGattCharacteristic.FORMAT_SINT8;

/** TI accelerometer sensor. */
public class TiAccelerometerSensor extends TiRangeSensors<TiSensorTag, Float> {
    /** Service UUID. */
    public static final String UUID_SERVICE = "f000aa10-0451-4000-b000-000000000000";
    /** Data UUID. */
    private static final String UUID_DATA = "f000aa11-0451-4000-b000-000000000000";
    /** Configuration UUID. */
    private static final String UUID_CONFIG = "f000aa12-0451-4000-b000-000000000000";
    /** Period UUID. */
    private static final String UUID_PERIOD = "f000aa13-0451-4000-b000-000000000000";

    /** Min period value. */
    public static final int PERIOD_MIN = 1;
    /** Max period value. */
    public static final int PERIOD_MAX = 255;

    /** Period. */
    private int period = 100;

    TiAccelerometerSensor(TiSensorTag model) {
        super(model);
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
            case UUID_PERIOD:
                return "Period";
            default:
                return super.getCharacteristicName(uuid);
        }
    }

    @Override
    public String getDataString() {
        final float[] data = getData().getAccel();
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
    public BleGattExecutor.ServiceAction[] update(String uuid, Bundle data) {
        switch (uuid) {
            case UUID_PERIOD:
                return new BleGattExecutor.ServiceAction[]{
                        write(uuid, new byte[]{(byte) period})
                };
            case UUID_CONFIG:
                return new BleGattExecutor.ServiceAction[]{
                        write(uuid, new byte[] {
                                (byte)(isEnabled() ? 1 : 0)
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
                 */
                Integer x = c.getIntValue(FORMAT_SINT8, 0);
                Integer y = c.getIntValue(FORMAT_SINT8, 1);
                Integer z = -1 * c.getIntValue(FORMAT_SINT8, 2);

                double scaledX = x / 64.0;
                double scaledY = y / 64.0;
                double scaledZ = z / 64.0;
                final float[] values = data.getAccel();
                values[0] = (float) scaledX;
                values[1] = (float) scaledY;
                values[2] = (float) scaledZ;
                return true;
            default:
                return false;
        }
    }

}
