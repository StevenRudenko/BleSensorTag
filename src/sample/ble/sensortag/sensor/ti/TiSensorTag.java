package sample.ble.sensortag.sensor.ti;

import java.util.Arrays;

/** TI sensor tag model. */
public class TiSensorTag {

    /** Keys state. */
    public enum KeysStatus {
        // Warning: The order in which these are defined matters.
        /** First button OFF. Second button OFF. */
        OFF_OFF,
        /** First button ON. Second button OFF. */
        ON_OFF,
        /** First button OFF. Second button ON. */
        OFF_ON,
        /** First button ON. Second button ON. */
        ON_ON;

        public static KeysStatus valueAt(int pos) {
            return values()[pos % values().length];
        }

        public boolean isFirstOn() {
            return this == ON_OFF || this == ON_ON;
        }

        public boolean isSecondOn() {
            return this == OFF_ON || this == ON_ON;
        }
    }

    /** Address of device. */
    private String address;

    /** Last key status. */
    private KeysStatus status = KeysStatus.OFF_OFF;
    /** Magnetometer value. */
    private final float[] magnet = new float[3];
    /** Accelerometer value. */
    private final float[] accel = new float[3];
    /** Gyroscope value. */
    private final float[] gyro = new float[3];
    /** Temperature value. */
    private final float[] temp = new float[2];
    /** Pressure value. */
    private double pressure = 0f;
    /** Humidity value. */
    private float humidity = 0f;
    /** Luxometer value. */
    private float lux = 0;

    public TiSensorTag(String address) {
        this.address = address;
    }

    public KeysStatus getKeyStatus() {
        return status;
    }

    public void setStatus(KeysStatus status) {
        this.status = status;
    }

    public float[] getMagnet() {
        return magnet;
    }

    public float getLux() {
        return lux;
    }

    public void setLux(final float lux) {
        this.lux = lux;
    }

    public float[] getAccel() {
        return accel;
    }

    public float[] getGyro() {
        return gyro;
    }

    public float[] getTemp() {
        return temp;
    }

    public double getPressure() {
        return pressure;
    }

    public void setPressure(final double pressure) {
        this.pressure = pressure;
    }

    public float getHumidity() {
        return humidity;
    }

    public void setHumidity(final float humidity) {
        this.humidity = humidity;
    }

    @Override
    public String toString() {
        return "SensorTag:\n"
                + "\tMagnet: " + Arrays.toString(magnet)
                + "\tLux: " + lux
                + "\tKeys: " + status.name();
    }
}
