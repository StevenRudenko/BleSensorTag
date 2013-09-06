package sample.ble.sensortag.sensor;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

/**
 * Created by steven on 9/3/13.
 */
public class TiTestSensor extends TiSensor<Void> {

    public static final TiTestSensor INSTANCE = new TiTestSensor();

    private TiTestSensor() {
        super();
    }

    @Override
    public String getName() {
        return "Test";
    }

    @Override
    public String getServiceUUID() {
        return "f000aa60-0451-4000-b000-000000000000";
    }

    @Override
    public String getDataUUID() {
        return "f000aa61-0451-4000-b000-000000000000";
    }

    @Override
    public String getConfigUUID() {
        return "f000aa62-0451-4000-b000-000000000000";
    }

    @Override
    public String toString(BluetoothGattCharacteristic c) {
        return "";
    }

    @Override
    public boolean isAccessable() {
        return false;
    }

    @Override
    public boolean isTurnable() {
        return false;
    }

    @Override
    public void execute(BluetoothGatt bluetoothGatt, ExecuteAction action) {
        //TODO: implement this service firstly
    }

    @Override
    protected void notify(BluetoothGatt bluetoothGatt, boolean start) {
        //TODO: implement this service firstly
    }

    @Override
    public Void onCharacteristicChanged(BluetoothGattCharacteristic c) {
        //TODO: implement method
        return null;
    }
}
