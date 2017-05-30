package sample.ble.sensortag.sensor.metawear;

import android.annotation.SuppressLint;

/** Sensor utils. */
public class MetawearUtils {

    private MetawearUtils() {
    }

    @SuppressLint("DefaultLocale")
    public static String coordinatesToString(float[] coordinates) {
        return String.format("x=%+.6f\ny=%+.6f\nz=%+.6f",
                coordinates[0], coordinates[1], coordinates[2]);
    }
}
