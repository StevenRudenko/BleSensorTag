package sample.ble.sensortag.sensor;

import android.bluetooth.BluetoothGattCharacteristic;

import static android.bluetooth.BluetoothGattCharacteristic.FORMAT_SINT8;
import static android.bluetooth.BluetoothGattCharacteristic.FORMAT_UINT8;

/**
 * Created by steven on 9/23/13.
 */
public class TiSensorUtils {

    private TiSensorUtils() {
    }

    /**
     * Gyroscope, Magnetometer, Barometer, IR temperature
     * all store 16 bit two's complement values in the awkward format
     * LSB MSB, which cannot be directly parsed as getIntValue(FORMAT_SINT16, offset)
     * because the bytes are stored in the "wrong" direction.
     *
     * This function extracts these 16 bit two's complement values.
     * */
    public static Integer shortSignedAtOffset(BluetoothGattCharacteristic c, int offset) {
        Integer lowerByte = c.getIntValue(FORMAT_UINT8, offset);
        if (lowerByte == null)
            return 0;
        Integer upperByte = c.getIntValue(FORMAT_SINT8, offset + 1); // Note: interpret MSB as signed.
        if (upperByte == null)
            return 0;

        return (upperByte << 8) + lowerByte;
    }

    public static Integer shortUnsignedAtOffset(BluetoothGattCharacteristic c, int offset) {
        Integer lowerByte = c.getIntValue(FORMAT_UINT8, offset);
        if (lowerByte == null)
            return 0;
        Integer upperByte = c.getIntValue(FORMAT_UINT8, offset + 1); // Note: interpret MSB as unsigned.
        if (upperByte == null)
            return 0;

        return (upperByte << 8) + lowerByte;
    }

    public static String coordinatesToString(float[] coordinates) {
        return String.format("x=%+.6f\ny=%+.6f\nz=%+.6f", coordinates[0], coordinates[1], coordinates[2]);
    }
}
