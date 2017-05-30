package sample.ble.sensortag.fusion.sensors;

import android.hardware.Sensor;

public interface ISensor {

    /**
     * A constant describing an accelerometer sensor type. See
     * {@link android.hardware.SensorEvent#values SensorEvent.values} for more
     * details.
     */
    public static final int TYPE_ACCELEROMETER = Sensor.TYPE_ACCELEROMETER;

    /**
     * A constant describing a magnetic field sensor type. See
     * {@link android.hardware.SensorEvent#values SensorEvent.values} for more
     * details.
     */
    public static final int TYPE_MAGNETIC_FIELD = Sensor.TYPE_MAGNETIC_FIELD;

    /** A constant describing a gyroscope sensor type */
    public static final int TYPE_GYROSCOPE = Sensor.TYPE_GYROSCOPE;

    public float getMaxRange();

    public int getType();
}
