package sample.ble.sensortag.demo;

import android.os.Bundle;
import android.widget.TextView;
import rajawali.Object3D;
import sample.ble.sensortag.R;
import sample.ble.sensortag.fusion.SensorFusionHelper;
import sample.ble.sensortag.sensor.TiAccelerometerSensor;
import sample.ble.sensortag.sensor.TiGyroscopeSensor;
import sample.ble.sensortag.sensor.TiMagnetometerSensor;
import sample.ble.sensortag.sensor.TiSensor;
import sample.ble.sensortag.sensor.TiSensors;

/**
 * Created by steven on 10/18/13.
 */
public class DemoSensorFusionActivity extends DemoSensorActivity {
    @SuppressWarnings("UnusedDeclaration")
    private final static String TAG = DemoSensorFusionActivity.class.getSimpleName();

    private TextView viewAcc;
    private TextView viewMag;
    private TextView viewGyro;
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

        viewAcc = (TextView) findViewById(R.id.acc);
        viewMag = (TextView) findViewById(R.id.mag);
        viewGyro = (TextView) findViewById(R.id.gyro);
        viewFused = (TextView) findViewById(R.id.fused);
    }

    @Override
    public int getContentViewId() {
        return R.layout.demo_sensor_fusion;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sensorFusion.stop();
    }

    @Override
    protected void onServicesDiscovered() {
        super.onServicesDiscovered();

        final TiAccelerometerSensor accSensor = (TiAccelerometerSensor) TiSensors.getSensor(TiAccelerometerSensor.UUID_SERVICE);
        accSensor.setPeriod(accSensor.getMinPeriod());

        final TiMagnetometerSensor magSensor = (TiMagnetometerSensor) TiSensors.getSensor(TiMagnetometerSensor.UUID_SERVICE);
        magSensor.setPeriod(magSensor.getMinPeriod());

        bleService.updateSensor(accSensor);
        bleService.updateSensor(magSensor);

        sensorFusion.start();
    }

    @Override
    public void onDataRecieved(TiSensor<?> sensor, String text) {
        if (sensor instanceof TiAccelerometerSensor) {
            final TiAccelerometerSensor accSensor = (TiAccelerometerSensor) sensor;
            float[] values = accSensor.getData();

            if (values == null)
                return;

            sensorFusion.onAccDataUpdate(values);
            viewAcc.setText(text);
        } else if (sensor instanceof TiMagnetometerSensor) {
            final TiMagnetometerSensor magSensor = (TiMagnetometerSensor) sensor;
            float[] values = magSensor.getData();

            if (values == null)
                return;

            sensorFusion.onMagDataUpdate(values);
            viewMag.setText(text);
        } else if (sensor instanceof TiGyroscopeSensor) {
            final TiGyroscopeSensor gyroSensor = (TiGyroscopeSensor) sensor;
            float[] values = gyroSensor.getData();

            if (values == null)
                return;

            sensorFusion.onGyroDataUpdate(values);
            viewGyro.setText(text);
        }
    }
}
