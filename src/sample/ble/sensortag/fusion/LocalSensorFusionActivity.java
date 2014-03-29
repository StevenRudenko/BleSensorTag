package sample.ble.sensortag.fusion;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import rajawali.Object3D;
import sample.ble.sensortag.R;
import sample.ble.sensortag.config.AppConfig;
import sample.ble.sensortag.fusion.engine.SensorFusionHelper;
import sample.ble.sensortag.fusion.sensors.AndroidSensorManager;
import sample.ble.sensortag.fusion.sensors.ISensor;
import sample.ble.sensortag.fusion.sensors.ISensorManager;
import sample.ble.sensortag.gl.GlActivity;

public class LocalSensorFusionActivity extends GlActivity implements ISensorManager.SensorEventListener {
    private ISensorManager sensorManager;

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

        sensorManager = new AndroidSensorManager(getBaseContext());
        sensorManager.setListener(this);

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
    protected void onStart() {
        super.onStart();

        sensorManager.enable();
        if (AppConfig.SENSOR_FUSION_USE_MAGNET_SENSOR)
            sensorManager.registerSensor(ISensor.TYPE_MAGNETIC_FIELD);
        sensorManager.registerSensor(ISensor.TYPE_ACCELEROMETER);
        sensorManager.registerSensor(ISensor.TYPE_GYROSCOPE);
        sensorFusion.start();
    }

    @Override
    protected void onStop() {
        super.onStop();

        sensorFusion.stop();
        sensorManager.disable();
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

    @Override
    public void onSensorChanged(int sensorType, float[] values) {
        switch(sensorType) {
            case ISensor.TYPE_ACCELEROMETER:
                sensorFusion.onAccDataUpdate(values);
                break;

            case ISensor.TYPE_GYROSCOPE:
                sensorFusion.onGyroDataUpdate(values);
                break;

            case ISensor.TYPE_MAGNETIC_FIELD:
                sensorFusion.onMagDataUpdate(values);
                break;
        }
    }
}
