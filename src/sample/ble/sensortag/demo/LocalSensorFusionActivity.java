package sample.ble.sensortag.demo;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import rajawali.Object3D;
import sample.ble.sensortag.R;
import sample.ble.sensortag.fusion.SensorFusionHelper;
import sample.ble.sensortag.gl.GlActivity;

public class LocalSensorFusionActivity extends GlActivity implements SensorEventListener {
    private final static String TAG = LocalSensorFusionActivity.class.getSimpleName();

    private SensorManager sensorManager;
    private Handler processor = null;

    private TextView viewFused;

    private final SensorFusionHelper sensorFusion = new SensorFusionHelper() {
        @Override
        public void onOrientationChanged(final float[] orienation) {
            final Object3D model = getModel();
            if (model == null)
                return;

            model.setRotation(
                    -orienation[1] * 180 / Math.PI,
                    -orienation[2] * 180 / Math.PI,
                    -orienation[0] * 180 / Math.PI);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    viewFused.setText("" + orienation[0] + "\n" + orienation[1] + "\n" + orienation[2]);
                }
            });
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActionBar().setTitle(R.string.title_demo_sensor_fusion);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        findViewById(R.id.acc).setVisibility(View.GONE);
        findViewById(R.id.mag).setVisibility(View.GONE);
        findViewById(R.id.gyro).setVisibility(View.GONE);
        viewFused = (TextView) findViewById(R.id.fused);
    }

    @Override
    public int getContentViewId() {
        return R.layout.demo_sensor_fusion;
    }

    @Override
    protected void onResume() {
        super.onResume();
        start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sensor_fusion, menu);

        final MenuItem lockOrientationItem = menu.findItem(R.id.menu_lock_orientaion);
        lockOrientation(lockOrientationItem);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int itemId = item.getItemId();
        switch (itemId) {
            case R.id.menu_lock_orientaion:
                item.setChecked(!item.isChecked());
                lockOrientation(item);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void lockOrientation(MenuItem item) {
        if (item.isChecked()) {
            item.setIcon(R.drawable.ic_action_lock_orientaion_off);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        } else {
            item.setIcon(R.drawable.ic_action_lock_orientaion_on);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        }
    }

    private void start() {
        final Thread processorThread = new Thread(TAG) {
            public void run() {
                Looper.prepare();
                processor = new Handler();
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

                sensorManager.registerListener(LocalSensorFusionActivity.this,
                        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                        SensorManager.SENSOR_DELAY_FASTEST, processor);

                sensorManager.registerListener(LocalSensorFusionActivity.this,
                        sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                        SensorManager.SENSOR_DELAY_FASTEST, processor);

                sensorManager.registerListener(LocalSensorFusionActivity.this,
                        sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                        SensorManager.SENSOR_DELAY_FASTEST, processor);

                Looper.loop();
            }
        };
        processorThread.start();

        sensorFusion.start();
    }

    private void stop() {
        sensorFusion.stop();
        sensorManager.unregisterListener(this);

        if (processor != null) {
            processor.getLooper().quit();
            processor = null;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch(event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                sensorFusion.onAccDataUpdate(event.values);
                break;

            case Sensor.TYPE_GYROSCOPE:
                sensorFusion.onGyroDataUpdate(event.values);
                break;

            case Sensor.TYPE_MAGNETIC_FIELD:
                sensorFusion.onMagDataUpdate(event.values);
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
          // do nothing
    }
}
