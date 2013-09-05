package sample.ble.sensortag.sensor;

import static android.bluetooth.BluetoothGattCharacteristic.FORMAT_UINT8;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

/**
 * Created by steven on 9/3/13.
 */
public class TiKeysSensor extends TiSensor<TiKeysSensor.SimpleKeysStatus> {

    public static final TiKeysSensor INSTANCE = new TiKeysSensor();

    public enum SimpleKeysStatus {
        // Warning: The order in which these are defined matters.
        OFF_OFF, OFF_ON, ON_OFF, ON_ON;
    }

    private TiKeysSensor() {
        super();
    }

    @Override
    public String getName() {
        return "Simple Keys";
    }

    @Override
    public String getServiceUUID() {
        return "0000ffe0-0000-1000-8000-00805f9b34fb";
    }

    @Override
    public String getDataUUID() {
        return "0000ffe1-0000-1000-8000-00805f9b34fb";
    }

    @Override
    public String getConfigUUID() {
        return null;
    }

    public SimpleKeysStatus onCharacteristicChanged(BluetoothGattCharacteristic c) {
    /*
     * The key state is encoded into 1 unsigned byte.
     * bit 0 designates the right key.
     * bit 1 designates the left key.
     * bit 2 designates the side key.
     *
     * Weird, in the userguide left and right are opposite.
     */
        int encodedInteger = c.getIntValue(FORMAT_UINT8, 0);
        return SimpleKeysStatus.values()[encodedInteger % 4];
    }

    @Override
    public boolean isTurnable() {
        return false;
    }

    @Override
    protected void enable(BluetoothGatt bluetoothGatt, boolean enable) {
    }

    @Override
    public String toString(BluetoothGattCharacteristic c) {
        final SimpleKeysStatus data = onCharacteristicChanged(c);
        return data.name();
    }

}
