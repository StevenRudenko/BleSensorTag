package sample.ble.sensortag.ble;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

/**
 * Implements callback methods for GATT events that the app cares about.
 * For example, connection change and services discovered.
 */
public interface BleExecutorListener {

    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState);

    public void onServicesDiscovered(BluetoothGatt gatt, int status);

    public void onCharacteristicRead(BluetoothGatt gatt,
                                     BluetoothGattCharacteristic characteristic,
                                     int status);

    public void onCharacteristicChanged(BluetoothGatt gatt,
                                        BluetoothGattCharacteristic characteristic);
}
