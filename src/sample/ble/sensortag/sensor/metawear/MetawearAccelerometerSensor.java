package sample.ble.sensortag.sensor.metawear;

import com.chimeraiot.android.ble.BleGattExecutor;

import android.os.Bundle;

import java.nio.ByteBuffer;

/** Metawear accelerometer sensor. */
public class MetawearAccelerometerSensor extends MetawearSensor {
    /** Service ID. */
    private static final byte SERVICE_ID = 3;

    /** Data scale. */
    private int scale = 8192;

    /**
     * Constructor.
     * @param data - instance of data.
     */
    protected MetawearAccelerometerSensor(Metawear data) {
        super(data);
    }

    @Override
    public String getDataString() {
        final Metawear metawear = getData();
        return MetawearUtils.coordinatesToString(metawear.getAccel());
    }

    @Override
    protected byte getServiceId() {
        return SERVICE_ID;
    }

    @Override
    public BleGattExecutor.ServiceAction[] update(String uuid, Bundle data) {
        switch (uuid) {
            case UUID_CONFIG:
                if (isEnabled()) {
                    return new BleGattExecutor.ServiceAction[] {
                            write(uuid, new byte[]{
                                    0x0b, (byte) (0x84 & 0xff)
                            }),
                            write(uuid, new byte[]{
                                    getServiceId(), 0x04, 0x01
                            }),
                            write(uuid, new byte[]{
                                    getServiceId(), 0x02, 0x01, 0x00
                            }),
                            write(uuid, new byte[]{
                                    getServiceId(), 0x03, 0x27, 0x03
                            }),
                            write(uuid, new byte[]{
                                    getServiceId(), 0x01, 0x01
                            }),
                    };
                } else {
                    return new BleGattExecutor.ServiceAction[]{
                            write(uuid, new byte[]{
                                    getServiceId(), 0x01, 0x00
                            }),
                            write(uuid, new byte[]{
                                    getServiceId(), 0x02, 0x00, 0x01
                            }),
                            write(uuid, new byte[]{
                                    getServiceId(), 0x04, 0x00
                            }),
                    };
                }
            default:
                return super.update(uuid, data);
        }
    }

    @Override
    protected boolean processData(byte op, ByteBuffer buffer, Metawear metawear) {
        switch (op) {
            case 4:
                final short x = buffer.getShort();
                final short y = buffer.getShort();
                final short z = buffer.getShort();
                final float[] values = metawear.getAccel();
                values[0] = (float) x / scale;
                values[1] = (float) y / scale;
                values[2] = (float) z / scale;
                return true;
            default:
                return false;
        }
    }

}
