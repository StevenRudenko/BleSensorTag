package sample.ble.sensortag.sensor.metawear;

import android.bluetooth.BluetoothGattCharacteristic;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import sample.ble.sensortag.sensor.BaseSensor;

/** Metawear Sensor. */
public abstract class MetawearSensor extends BaseSensor<Metawear> {

    /** Service UUID. */
    public static final String UUID_SERVICE = "326a9000-85cb-9195-d9dd-464cfbbae75a";
    /** Data UUID. */
    protected static final String UUID_DATA = "326a9006-85cb-9195-d9dd-464cfbbae75a";
    /** Configuration UUID. */
    protected static final String UUID_CONFIG = "326a9001-85cb-9195-d9dd-464cfbbae75a";

    /**
     * Constructor.
     * @param data - instance of data.
     */
    protected MetawearSensor(Metawear data) {
        super(data);
    }

    @Override
    public String getName() {
        return "Metawear Data Service";
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

    /** Service ID. */
    protected abstract byte getServiceId();

    /**
     * Processes data
     * @param op operation.
     * @param data data buffer.
     * @return true if data has been processed and consumed.
     */
    protected abstract boolean processData(byte op, ByteBuffer data, Metawear metawear);

    @Override
    protected boolean apply(BluetoothGattCharacteristic c, Metawear metawear) {
        final String uuid = c.getUuid().toString();
        switch (uuid) {
            case UUID_DATA:
                final byte[] data = c.getValue();
                final ByteBuffer buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
                final byte id = buffer.get();
                if (id != getServiceId()) {
                    return false;
                }
                final byte op = buffer.get();
                return processData(op, buffer, metawear);
            default:
                return false;
        }
    }

}
