package sample.ble.sensortag.sensor.metawear;

/** Metawear data model. */
public class Metawear {
    /** Device address. */
    private final String address;

    /** Accelerometer value. */
    private final float[] accel = new float[3];

    public Metawear(String address) {
        this.address = address;
    }

    public float[] getAccel() {
        return accel;
    }

}
