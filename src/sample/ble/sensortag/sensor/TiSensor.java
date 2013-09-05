package sample.ble.sensortag.sensor;

import static android.bluetooth.BluetoothGattCharacteristic.FORMAT_UINT8;
import static android.bluetooth.BluetoothGattCharacteristic.FORMAT_SINT8;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.util.Log;

import java.util.UUID;

/**
 * Created by steven on 9/3/13.
 */
public abstract class TiSensor<T> {
    private final static String TAG = TiSensor.class.getSimpleName();

    private static String CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";

    public enum ExecuteAction {
        TURN_ON,
        TUNN_OFF,
        BEGIN_NOTIFY,
        END_NOTIFY
    }

    protected TiSensor() {
    }

    public abstract String getName();
    public String getCharacteristicName(String uuid) {
        if (getDataUUID().equals(uuid))
            return getName() + " Data";
        else if (getConfigUUID().equals(uuid))
            return getName() + " Config";
        return "Unknown";
    }

    public abstract String getServiceUUID();
    public abstract String getDataUUID();
    public abstract String getConfigUUID();

    protected byte[] getConfigValues(boolean enable) {
        return new byte[] { (byte)(enable ? 1 : 0) };
    }

    public T onCharacteristicChanged(byte[] value) {
        final BluetoothGattCharacteristic dumb = new BluetoothGattCharacteristic(
                UUID.randomUUID(),
                BluetoothGattCharacteristic.PROPERTY_READ,
                BluetoothGattCharacteristic.PERMISSION_READ);
        dumb.setValue(value);

        return onCharacteristicChanged(dumb);
    }

    public abstract T onCharacteristicChanged(BluetoothGattCharacteristic c);

    public abstract String toString(BluetoothGattCharacteristic c);

    public boolean isTurnable() {
        return true;
    }

    public void execute(BluetoothGatt bluetoothGatt, ExecuteAction action) {
        switch (action) {
            case TURN_ON:
                enable(bluetoothGatt, true);
                break;
            case TUNN_OFF:
                enable(bluetoothGatt, false);
                break;
            case BEGIN_NOTIFY:
                notify(bluetoothGatt, true);
                break;
            case END_NOTIFY:
                notify(bluetoothGatt, false);
                break;
        }
    }

    /**
     * NB: the config value is different for the Gyroscope
     * @param bluetoothGatt
     * @param enable
     */
    protected void enable(BluetoothGatt bluetoothGatt, boolean enable) {
        final UUID serviceUuid = UUID.fromString(getServiceUUID());
        final UUID configUuid = UUID.fromString(getConfigUUID());

        final BluetoothGattService service = bluetoothGatt.getService(serviceUuid);
        final BluetoothGattCharacteristic config = service.getCharacteristic(configUuid);
        config.setValue(getConfigValues(enable));
        bluetoothGatt.writeCharacteristic(config);
    }

    protected void notify(BluetoothGatt bluetoothGatt, boolean start) {
        final UUID serviceUuid = UUID.fromString(getServiceUUID());
        final UUID dataUuid = UUID.fromString(getDataUUID());
        final UUID CCC = UUID.fromString(CHARACTERISTIC_CONFIG);

        // enable/disable locally
        final BluetoothGattService service = bluetoothGatt.getService(serviceUuid);
        final BluetoothGattCharacteristic dataCharacteristic = service.getCharacteristic(dataUuid);
        bluetoothGatt.setCharacteristicNotification(dataCharacteristic, start);

        // enable/disable remotely
        final BluetoothGattDescriptor config = dataCharacteristic.getDescriptor(CCC);
        config.setValue(start ? BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE : BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
        bluetoothGatt.writeDescriptor(config);
    }

    /**
     * Gyroscope, Magnetometer, Barometer, IR temperature
     * all store 16 bit two's complement values in the awkward format
     * LSB MSB, which cannot be directly parsed as getIntValue(FORMAT_SINT16, offset)
     * because the bytes are stored in the "wrong" direction.
     *
     * This function extracts these 16 bit two's complement values.
     * */
    protected static Integer shortSignedAtOffset(BluetoothGattCharacteristic c, int offset) {
        Integer lowerByte = c.getIntValue(FORMAT_UINT8, offset);
        Integer upperByte = c.getIntValue(FORMAT_SINT8, offset + 1); // Note: interpret MSB as signed.

        return (upperByte << 8) + lowerByte;
    }

    protected static Integer shortUnsignedAtOffset(BluetoothGattCharacteristic c, int offset) {
        Integer lowerByte = c.getIntValue(FORMAT_UINT8, offset);
        Integer upperByte = c.getIntValue(FORMAT_UINT8, offset + 1); // Note: interpret MSB as unsigned.

        return (upperByte << 8) + lowerByte;
    }


}
