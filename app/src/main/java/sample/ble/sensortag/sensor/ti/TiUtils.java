package sample.ble.sensortag.sensor.ti;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothGattCharacteristic;

/** Sensor utils. */
public class TiUtils {

    private TiUtils() {
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
        byte[] value = c.getValue();
        Integer lowerByte = (int) value[offset] & 0xFF;
        Integer upperByte = (int) value[offset + 1]; // Note: interpret MSB as signed.
        return (upperByte << 8) + lowerByte;
    }

    public static Integer shortUnsignedAtOffset(BluetoothGattCharacteristic c, int offset) {
        byte[] value = c.getValue();
        Integer lowerByte = (int) value[offset] & 0xFF;
        Integer upperByte = (int) value[offset + 1] & 0xFF; // Note: interpret MSB as unsigned.
        return (upperByte << 8) + lowerByte;
    }

    public static Integer twentyFourBitUnsignedAtOffset(BluetoothGattCharacteristic c, int offset) {
        byte[] value = c.getValue();
        Integer lowerByte = (int) value[offset] & 0xFF;
        Integer mediumByte = (int) value[offset + 1] & 0xFF;
        Integer upperByte = (int) value[offset + 2] & 0xFF;
        return (upperByte << 16) + (mediumByte << 8) + lowerByte;

    }

    @SuppressLint("DefaultLocale")
    public static String coordinatesToString(float[] coordinates) {
        return String.format("x=%+.6f\ny=%+.6f\nz=%+.6f",
                coordinates[0], coordinates[1], coordinates[2]);
    }
}
