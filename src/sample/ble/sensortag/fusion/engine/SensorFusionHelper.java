package sample.ble.sensortag.fusion.engine;

import java.util.Timer;

public abstract class SensorFusionHelper {

    private static final int TIMER_RATE = 30;

    private Timer fuseTimer = null;
    private SensorFusionEngine fusionEngine = null;

    private boolean isRunning = false;

    public void start() {
        fuseTimer = new Timer();
        fusionEngine = new SensorFusionEngine();
    }

    public void stop() {
        if (fuseTimer != null) {
            fuseTimer.cancel();
            fuseTimer = null;
        }

        if (fusionEngine != null) {
            fusionEngine.cancel();
            fusionEngine = null;
        }
    }

    private synchronized void startSensorFusion() {
        if (isRunning)
            return;

        fuseTimer.scheduleAtFixedRate(fusionEngine, 0, TIMER_RATE);
        isRunning = true;
    }

    public void onAccDataUpdate(float[] acc) {
        if (fusionEngine == null)
            return;

        if (fusionEngine.scheduledExecutionTime() == 0)
            startSensorFusion();

        fusionEngine.onAccDataUpdate(acc);
        onOrientationChanged(fusionEngine.getFusedOrientation());
    }

    public void onMagDataUpdate(float[] magnet) {
        if (fusionEngine == null)
            return;

        if (fusionEngine.scheduledExecutionTime() == 0)
            startSensorFusion();

        fusionEngine.onMagDataUpdate(magnet);
        onOrientationChanged(fusionEngine.getFusedOrientation());
    }

    public void onGyroDataUpdate(float[] gyro) {
        if (fusionEngine == null)
            return;

        if (fusionEngine.scheduledExecutionTime() == 0)
            startSensorFusion();

        fusionEngine.onGyroDataUpdate(gyro);
        onOrientationChanged(fusionEngine.getFusedOrientation());
    }

    public abstract void onOrientationChanged(float[] orientation);
}
