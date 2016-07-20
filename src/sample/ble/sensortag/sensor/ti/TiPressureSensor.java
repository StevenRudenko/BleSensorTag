package sample.ble.sensortag.sensor.ti;

import com.chimeraiot.android.ble.BleGattExecutor;

import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Bundle;

import sample.ble.sensortag.sensor.BaseSensor;

import static java.lang.Math.pow;

/** TI pressure sensor. */
public class TiPressureSensor extends BaseSensor<TiSensorTag> {
    /** Service UUID. */
    private static final String UUID_SERVICE = "f000aa40-0451-4000-b000-000000000000";
    /** Data UUID. */
    private static final String UUID_DATA = "f000aa41-0451-4000-b000-000000000000";
    /** Configuration UUID. */
    private static final String UUID_CONFIG = "f000aa42-0451-4000-b000-000000000000";
    /** Calibration UUID. */
    private static final String UUID_CALIBRATION = "f000aa43-0451-4000-b000-000000000000";

    /** Calibration constant. */
    private static final byte[] CALIBRATION_DATA = new byte[]{2};

    /** Calibration value. */
    private final int[] calibration = new int[8];

    TiPressureSensor(TiSensorTag model) {
        super(model);
    }

    @Override
    public String getName() {
        return "Pressure";
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
    public String getCharacteristicName(String uuid) {
        switch (uuid) {
            case UUID_CALIBRATION:
                return "Calibration";
            default:
                return super.getCharacteristicName(uuid);
        }
    }

    @Override
    public String getDataString() {
        final double data = getData().getPressure();
        return "" + data;
    }

    @Override
    public BleGattExecutor.ServiceAction[] update(final String uuid, final Bundle data) {
        switch (uuid) {
            case UUID_CONFIG:
                return new BleGattExecutor.ServiceAction[]{
                        write(UUID_CONFIG, CALIBRATION_DATA),
                        read(UUID_CALIBRATION),
                        write(getConfigUUID(), new byte[] {
                                (byte)(isEnabled() ? 1 : 0)
                        })
                };
            default:
                return super.update(uuid, data);
        }
    }

    @Override
    protected boolean apply(final BluetoothGattCharacteristic c, final TiSensorTag data) {
        final String uuid = c.getUuid().toString();
        switch (uuid) {
            case UUID_CALIBRATION:
                for (int i = 0; i < 4; ++i) {
                    calibration[i] = TiUtils.shortUnsignedAtOffset(c, i * 2);
                    calibration[i + 4] = TiUtils.shortSignedAtOffset(c, 8 + i * 2);
                }
                return true;
            case UUID_DATA:
                // c holds the calibration coefficients
                final Integer t_r;    // Temperature raw value from sensor
                final Integer p_r;    // Pressure raw value from sensor
                final Double t_a;    // Temperature actual value in unit centi degrees celsius
                final Double S;    // Interim value in calculation
                final Double O;    // Interim value in calculation
                final Double p_a;    // Pressure actual value in unit Pascal.

                t_r = TiUtils.shortSignedAtOffset(c, 0);
                p_r = TiUtils.shortUnsignedAtOffset(c, 2);

                t_a = (100 * (calibration[0] * t_r / pow(2, 8) + calibration[1] * pow(2, 6))) / pow(2, 16);
                S = calibration[2] + calibration[3] * t_r / pow(2, 17)
                        + ((calibration[4] * t_r / pow(2, 15)) * t_r) / pow(2, 19);
                O = calibration[5] * pow(2, 14) + calibration[6] * t_r / pow(2, 3)
                        + ((calibration[7] * t_r / pow(2, 15)) * t_r) / pow(2, 4);
                p_a = (S * p_r + O) / pow(2, 14);

                data.setPressure(p_a);
                return true;
            default:
                return false;
        }
    }

}
