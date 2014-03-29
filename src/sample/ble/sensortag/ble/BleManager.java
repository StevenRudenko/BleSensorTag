package sample.ble.sensortag.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import sample.ble.sensortag.config.AppConfig;
import sample.ble.sensortag.sensor.TiSensor;
import sample.ble.sensortag.sensor.TiSensors;

import java.util.List;

public class BleManager implements BleExecutorListener {
    private final static String TAG = BleManager.class.getSimpleName();

    public static final int STATE_DISCONNECTED = 0;
    public static final int STATE_CONNECTING = 1;
    public static final int STATE_CONNECTED = 2;

    private final BleGattExecutor executor = BleUtils.createExecutor(this);
    private BluetoothAdapter adapter;
    private BluetoothGatt gatt;

    private String deviceAddress;
    private int connectionState = STATE_DISCONNECTED;

    private BleServiceListener serviceListener;

    public int getState() {
        return connectionState;
    }

    public String getConnectedDeviceAddress() {
        return deviceAddress;
    }

    public void setServiceListener(BleServiceListener listener) {
        serviceListener = listener;
    }

    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize(Context context) {
        if (adapter == null) {
            adapter = BleUtils.getBluetoothAdapter(context);
        }
        if (adapter == null || !adapter.isEnabled()) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }

        return true;
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param address The device address of the destination device.
     *
     * @return Return true if the connection is initiated successfully. The connection result
     *         is reported asynchronously through the
     *         {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     *         callback.
     */
    public boolean connect(Context context, String address) {
        if (adapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        // Previously connected device.  Try to reconnect.
        if (deviceAddress != null && address.equals(deviceAddress) && gatt != null) {
            Log.d(TAG, "Trying to use an existing BluetoothGatt for connection.");
            if (gatt.connect()) {
                connectionState = STATE_CONNECTING;
                return true;
            } else {
                return false;
            }
        }

        final BluetoothDevice device = adapter.getRemoteDevice(address);
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        gatt = device.connectGatt(context, false, executor);
        Log.d(TAG, "Trying to create a new connection.");
        deviceAddress = address;
        connectionState = STATE_CONNECTING;
        return true;
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public void disconnect() {
        if (adapter == null || gatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        gatt.disconnect();
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close() {
        if (gatt == null) {
            return;
        }
        gatt.close();
        gatt = null;
    }

    public void updateSensor(TiSensor<?> sensor) {
        if (sensor == null)
            return;

        if (adapter == null || gatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }

        executor.update(sensor);
        executor.execute(gatt);
    }

    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param sensor sensor to be enabled/disabled
     * @param enabled If true, enable notification.  False otherwise.
     */
    public void enableSensor(TiSensor<?> sensor, boolean enabled) {
        if (sensor == null)
            return;

        if (adapter == null || gatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }

        executor.enable(sensor, enabled);
        executor.execute(gatt);
    }

    /**
     * Retrieves a list of supported GATT services on the connected device. This should be
     * invoked only after {@code BluetoothGatt#discoverServices()} completes successfully.
     *
     * @return A {@code List} of supported services.
     */
    public List<BluetoothGattService> getSupportedGattServices() {
        if (gatt == null)
            return null;

        return gatt.getServices();
    }

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        if (newState == BluetoothProfile.STATE_CONNECTED) {
            connectionState = STATE_CONNECTED;
            Log.i(TAG, "Connected to GATT server.");
            // Attempts to discover services after successful connection.
            Log.i(TAG, "Attempting to start service discovery:" +
                    gatt.discoverServices());

            if (serviceListener != null)
                serviceListener.onConnected();
        } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
            connectionState = STATE_DISCONNECTED;
            Log.i(TAG, "Disconnected from GATT server.");

            if (serviceListener != null)
                serviceListener.onDisconnected();
        }
    }

    /**
     * Request a read on a given {@code BluetoothGattCharacteristic}. The read result is reported
     * asynchronously through the {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
     * callback.
     *
     * @param characteristic The characteristic to read from.
     */
    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (adapter == null || gatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        gatt.readCharacteristic(characteristic);
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        if (status == BluetoothGatt.GATT_SUCCESS) {
            if (serviceListener != null)
                serviceListener.onServiceDiscovered();
        } else {
            Log.w(TAG, "onServicesDiscovered received: " + status);
        }
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt,
                                     BluetoothGattCharacteristic characteristic,
                                     int status) {
        if (status != BluetoothGatt.GATT_SUCCESS)
            return;

        final TiSensor<?> sensor = TiSensors.getSensor(characteristic.getService().getUuid().toString());
        if (sensor != null) {
            if (sensor.onCharacteristicRead(characteristic)) {
                return;
            }
        }

        broadcastUpdate(characteristic);
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt,
                                        BluetoothGattCharacteristic characteristic) {
        broadcastUpdate(characteristic);
    }

    private void broadcastUpdate(BluetoothGattCharacteristic characteristic) {
        final String serviceUuid = characteristic.getService().getUuid().toString();
        final String characteristicUuid = characteristic.getUuid().toString();
        final byte[] data;
        final String text;

        final TiSensor<?> sensor = TiSensors.getSensor(serviceUuid);
        if (sensor != null) {
            sensor.onCharacteristicChanged(characteristic);
            text = sensor.getDataString();
            data = null;
        } else {
            data = characteristic.getValue();
            // For all other profiles, writes the data formatted in HEX.
            if (data != null && data.length > 0) {
                final StringBuilder stringBuilder = new StringBuilder(data.length);
                for (byte byteChar : data)
                    stringBuilder.append(String.format("%02X ", byteChar));
                text = new String(data) + "\n" + stringBuilder.toString();
            } else {
                text = null;
            }
        }

        if (serviceListener != null)
            serviceListener.onDataAvailable(serviceUuid, characteristicUuid, text, data);
    }
}
