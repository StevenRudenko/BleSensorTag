package sample.ble.sensortag.ble;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothProfile;

import java.util.LinkedList;

import sample.ble.sensortag.sensor.TiSensor;

/**
 * Created by steven on 9/3/13.
 */
public class BleGattExecutor extends BluetoothGattCallback {

    public static abstract class ServiceAction {
        public enum ActionType {
            NONE,
            READ,
            NOTIFY,
            WRITE
        }

        public static final ServiceAction NULL = new ServiceAction(ActionType.NONE) {
            @Override
            public boolean execute(BluetoothGatt bluetoothGatt) {
                // it is null action. do nothing.
                return true;
            }
        };

        private final ActionType type;

        public ServiceAction(ActionType type) {
            this.type = type;
        }

        public ActionType getType() {
            return type;
        }

        /***
         * Executes action.
         * @param bluetoothGatt
         * @return true - if action was executed instantly. false if action is waiting for
         *         feedback.
         */
        public abstract boolean execute(BluetoothGatt bluetoothGatt);
    }

    private final LinkedList<BleGattExecutor.ServiceAction> queue = new LinkedList<ServiceAction>();
    private volatile ServiceAction currentAction;

    public void update(final TiSensor sensor) {
        queue.add(sensor.update());
    }

    public void enable(TiSensor sensor, boolean enable) {
        final ServiceAction[] actions = sensor.enable(enable);
        for ( ServiceAction action : actions ) {
            this.queue.add(action);
        }
    }

    public void execute(BluetoothGatt gatt) {
        if (currentAction != null)
            return;

        boolean next = !queue.isEmpty();
        while (next) {
            final BleGattExecutor.ServiceAction action = queue.pop();
            currentAction = action;
            if (!action.execute(gatt))
                break;

            currentAction = null;
            next = !queue.isEmpty();
        }
    }

    @Override
    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        super.onDescriptorWrite(gatt, descriptor, status);

        // wait for onCharacteristicWrite for write action before execution of any other actions
        if (currentAction != null && currentAction.getType() == ServiceAction.ActionType.WRITE)
            return;

        currentAction = null;
        execute(gatt);
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        super.onCharacteristicWrite(gatt, characteristic, status);

        currentAction = null;
        execute(gatt);
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt,
                                     BluetoothGattCharacteristic characteristic,
                                     int status) {
        // wait for onCharacteristicWrite for write action before execution of any other actions
        if (currentAction != null && currentAction.getType() == ServiceAction.ActionType.WRITE)
            return;

        currentAction = null;
        execute(gatt);
    }

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        if (newState == BluetoothProfile.STATE_DISCONNECTED) {
            queue.clear();
        }
    }
}
