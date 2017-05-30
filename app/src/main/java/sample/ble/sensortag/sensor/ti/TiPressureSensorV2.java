package sample.ble.sensortag.sensor.ti;

import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Bundle;

import com.chimeraiot.android.ble.BleGattExecutor;

import static java.lang.Math.pow;

/** TI key sensor V2. */
public class TiPressureSensorV2 extends TiPressureSensor {
    /** Period UUID. */
    private static final String UUID_PERIOD = "f000aa44-0451-4000-b000-000000000000";

    private static final double PA_PER_METER = 12.0;

    private static double heightCalibration;

    private boolean isHeightCalibrated;

    TiPressureSensorV2(TiSensorTag model) {
        super(model);
    }

    @Override
    public String getCharacteristicName(String uuid) {
        if (UUID_PERIOD.equals(uuid)) {
            return "Period";
        }
        return super.getCharacteristicName(uuid);
    }

    @Override
    public BleGattExecutor.ServiceAction[] update(final String uuid, final Bundle data) {
        switch (uuid) {
            case UUID_CONFIG:
                return new BleGattExecutor.ServiceAction[] {
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
            case UUID_DATA:
                double output;
                if (c.getValue().length > 4) {
                    output = TiUtils.twentyFourBitUnsignedAtOffset(c, 2);
                } else {
                    Integer value = TiUtils.shortUnsignedAtOffset(c, 2);
                    int mantissa = value & 0x0FFF;
                    int exponent = (value >> 12) & 0xFF;
                    double magnitude = pow(2.0f, exponent);
                    output = (mantissa * magnitude);
                }
                data.setPressure(output / 10000.0f);

                if (!isHeightCalibrated) {
                    heightCalibration = output;
                    isHeightCalibrated = true;
                }

                double height = (output - heightCalibration) / PA_PER_METER;
                height = (double) Math.round(-height * 10.0) / 10.0;
                return true;
            default:
                return super.apply(c, data);
        }
    }
}
