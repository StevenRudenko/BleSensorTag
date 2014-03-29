package sample.ble.sensortag.fusion;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import rajawali.Object3D;
import sample.ble.sensortag.R;
import sample.ble.sensortag.config.AppConfig;
import sample.ble.sensortag.fusion.engine.SensorFusionHelper;
import sample.ble.sensortag.fusion.sensors.BleSensorManager;
import sample.ble.sensortag.fusion.sensors.ISensor;
import sample.ble.sensortag.fusion.sensors.ISensorManager;
import sample.ble.sensortag.gl.GlActivity;

/**
 * Created by steven on 10/18/13.
 */
public class DemoSensorFusionActivity extends GlActivity implements ISensorManager.SensorEventListener {
    @SuppressWarnings("UnusedDeclaration")
    private final static String TAG = DemoSensorFusionActivity.class.getSimpleName();

    public static final String EXTRAS_DEVICE_ADDRESS = TAG+":DEVICE_ADDRESS";

    private ISensorManager sensorManager;
    private TextView viewFused;

    private final SensorFusionHelper sensorFusion = new SensorFusionHelper() {
        @Override
        public void onOrientationChanged(float[] orientation) {
            final Object3D model = getModel();
            if (model == null)
                return;

            model.setRotation(
                    orientation[0] * 180 / Math.PI,
                    orientation[1] * 180 / Math.PI,
                    orientation[2] * 180 / Math.PI);
            viewFused.setText("" + orientation[0]+"\n" + orientation[1]+"\n" + orientation[2]);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setTitle(R.string.title_demo_sensor_fusion);

        final Intent intent = getIntent();
        final String deviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);
        sensorManager = new BleSensorManager(this, deviceAddress);
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

        sensorManager.disable();
        sensorFusion.stop();
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
