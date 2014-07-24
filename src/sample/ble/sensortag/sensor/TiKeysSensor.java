package sample.ble.sensortag.sensor;

import static android.bluetooth.BluetoothGattCharacteristic.FORMAT_UINT8;

import android.bluetooth.BluetoothGattCharacteristic;

import sample.ble.sensortag.ble.BleGattExecutor;

/**
 * Created by steven on 9/3/13.
 */
public class TiKeysSensor extends TiSensor<TiKeysSensor.SimpleKeysStatus> {

    private static final String UUID_SERVICE = "0000ffe0-0451-4000-b000-000000000000";
    private static final String UUID_DATA = "0000ffe1-0451-4000-b000-000000000000";
    private static final String UUID_CONFIG = null;

    public enum SimpleKeysStatus {
        // Warning: The order in which these are defined matters.
        OFF_OFF, OFF_ON, ON_OFF, ON_ON;
    }

    TiKeysSensor() {
        super();
    }

    @Override
    public String getName() {
        return "Simple Keys";
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
    public BleGattExecutor.ServiceAction[] enable(boolean enable) {
        return new BleGattExecutor.ServiceAction[] {
                notify(enable)
        };
    }

    @Override
    public String getDataString() {
        final SimpleKeysStatus data = getData();
        return data.name();
    }

    @Override
    public SimpleKeysStatus parse(BluetoothGattCharacteristic c) {
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
}
