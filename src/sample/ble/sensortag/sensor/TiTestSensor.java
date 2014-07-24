package sample.ble.sensortag.sensor;

import android.bluetooth.BluetoothGattCharacteristic;

import sample.ble.sensortag.ble.BleGattExecutor;

/**
 * Created by steven on 9/3/13.
 */
public class TiTestSensor extends TiSensor<Void> {

    private static final String UUID_SERVICE = "f000aa60-0451-4000-b000-000000000000";
    private static final String UUID_DATA = "f000aa61-0451-4000-b000-000000000000";
    private static final String UUID_CONFIG = "f000aa62-0451-4000-b000-000000000000";

    TiTestSensor() {
        super();
    }

    @Override
    public String getName() {
        return "Test";
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
    public String getDataString() {
        return "";
    }

    @Override
    public BleGattExecutor.ServiceAction[] enable(boolean enable) {
        return new BleGattExecutor.ServiceAction[0];
    }

    @Override
    public BleGattExecutor.ServiceAction notify(boolean start) {
        return BleGattExecutor.ServiceAction.NULL;
    }

    @Override
    public Void parse(BluetoothGattCharacteristic c) {
        //TODO: implement method
        return null;
    }
}
