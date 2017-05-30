package sample.ble.sensortag.fusion.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Looper;

public class AndroidSensorManager extends ISensorManager implements SensorEventListener {
    private static final String TAG = AndroidSensorManager.class.getSimpleName();

    private static final double SENSOR_FUSION_COEFF = -180f / Math.PI;
    private final double[] fusedOrientation = new double[3];

    private final SensorManager sensorManager;
    private Handler processor = null;

    public AndroidSensorManager(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    }

    @Override
    public ISensor getSensor(int sensorType) {
        final AndroidSensor result = new AndroidSensor(
                sensorManager.getDefaultSensor(sensorType));
        return result;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (listener == null)
            return;
        listener.onSensorChanged(event.sensor.getType(), event.values);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void enable() {
        if (processor != null)
            return;

        final Thread processorThread = new Thread(TAG) {
            public void run() {
                Looper.prepare();
                processor = new Handler();
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

                Looper.loop();
            }
        };
        processorThread.start();
    }

    @Override
    public void disable() {
        sensorManager.unregisterListener(this);
        if (processor != null) {
            processor.getLooper().quit();
            processor = null;
        }
    }

    @Override
    public void registerSensor(int sensorType) {
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(sensorType),
                SensorManager.SENSOR_DELAY_FASTEST, processor);
    }

    @Override
    public void unregisterSensor(int sensorType) {
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(sensorType),
                SensorManager.SENSOR_DELAY_FASTEST, processor);
    }

    @Override
    public double[] patchSensorFusion(float[] values) {
        fusedOrientation[0] = values[1] * SENSOR_FUSION_COEFF;
        fusedOrientation[1] = values[2] * SENSOR_FUSION_COEFF;
        fusedOrientation[2] = values[0] * SENSOR_FUSION_COEFF;
        return fusedOrientation;
    }
}
