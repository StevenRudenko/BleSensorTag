package sample.ble.sensortag.sensor;

import android.bluetooth.BluetoothGattCharacteristic;

import sample.ble.sensortag.ble.BleGattExecutor;

/**
 * Created by steven on 9/3/13.
 */
public class TiMagnetometerSensor extends TiRangeSensors<float[], Float> {

    public static final String UUID_SERVICE = "f000aa30-0451-4000-b000-000000000000";
    private static final String UUID_DATA = "f000aa31-0451-4000-b000-000000000000";
    private static final String UUID_CONFIG = "f000aa32-0451-4000-b000-000000000000";
    private static final String UUID_PERIOD = "f000aa33-0451-4000-b000-000000000000";

    private static final int PERIOD_MIN = 10;
    private static final int PERIOD_MAX = 255;

    private int period = 200;

    TiMagnetometerSensor() {
        super();
    }

    @Override
    public String getName() {
        return "Magnetometer";
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
    public boolean isConfigUUID(String uuid) {
        if (uuid.equals(UUID_PERIOD))
            return true;
        return super.isConfigUUID(uuid);
    }

    @Override
    public String getCharacteristicName(String uuid) {
        if (UUID_PERIOD.equals(uuid))
            return getName() + " Period";
        return super.getCharacteristicName(uuid);
    }

    @Override
    public String getDataString() {
        final float[] data = getData();
        return TiSensorUtils.coordinatesToString(data);
    }

    @Override
    public int getMinPeriod() {
        return PERIOD_MIN;
    }

    @Override
    public int getMaxPeriod() {
        return PERIOD_MAX;
    }

    @Override
    public void setPeriod(int period) {
        this.period = period;
    }

    @Override
    public int getPeriod() {
        return period;
    }

    @Override
    public Float getMaxRange() {
        return 1.f;
    }

    @Override
    public BleGattExecutor.ServiceAction update() {
        return write(UUID_PERIOD, new byte[]{(byte) period});
    }

    @Override
    public float[] parse(BluetoothGattCharacteristic c) {
        // Multiply x and y with -1 so that the values correspond with our pretty pictures in the app.
        float x = TiSensorUtils.shortSignedAtOffset(c, 0) * (2000f / 65536f) * -1;
        float y = TiSensorUtils.shortSignedAtOffset(c, 2) * (2000f / 65536f) * -1;
        float z = TiSensorUtils.shortSignedAtOffset(c, 4) * (2000f / 65536f);

        return new float[]{x, y, z};
    }

}
