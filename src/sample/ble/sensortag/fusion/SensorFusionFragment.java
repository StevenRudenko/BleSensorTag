package sample.ble.sensortag.fusion;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import rajawali.Object3D;
import sample.ble.sensortag.R;
import sample.ble.sensortag.config.AppConfig;
import sample.ble.sensortag.fusion.engine.SensorFusionHelper;
import sample.ble.sensortag.fusion.sensors.AndroidSensorManager;
import sample.ble.sensortag.fusion.sensors.BleSensorManager;
import sample.ble.sensortag.fusion.sensors.ISensor;
import sample.ble.sensortag.fusion.sensors.ISensorManager;
import sample.ble.sensortag.gl.GlFragment;

import java.util.Formatter;

public class SensorFusionFragment extends GlFragment implements ISensorManager.SensorEventListener {
    public final static String TAG = SensorFusionFragment.class.getSimpleName();

    public static final String EXTRA_DEVICE_ADDRESS = TAG+":DEVICE_ADDRESS";

    private ISensorManager sensorManager;
    private TextView viewFused;

    private final SensorFusionHelper sensorFusion = new SensorFusionHelper() {
        @Override
        public void onOrientationChanged(final float[] orientation) {
            final Object3D model = getModel();
            if (model == null)
                return;

            final double[] patchedOrientation = sensorManager.patchSensorFusion(orientation);
            model.setRotation(patchedOrientation[0], patchedOrientation[1], patchedOrientation[2]);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    viewFused.setText(String.format("%+.6f\n%+.6f\n%+.6f",
                                      orientation[0], orientation[1], orientation[2]));
                }
            });
        }
    };

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Bundle args = getArguments();
        if (args == null || !args.containsKey(EXTRA_DEVICE_ADDRESS)) {
            sensorManager = new AndroidSensorManager(getActivity());
        } else {
            final String deviceAddress = args.getString(EXTRA_DEVICE_ADDRESS);
            sensorManager = new BleSensorManager(getActivity(), deviceAddress);
        }
        sensorManager.setListener(this);

        viewFused = (TextView) view.findViewById(R.id.fused);
    }

    @Override
    public int getContentViewId() {
        return R.layout.sensor_fusion_fragment;
    }

    @Override
    public void onStart() {
        super.onStart();

        sensorManager.enable();
        if (AppConfig.SENSOR_FUSION_USE_MAGNET_SENSOR)
            sensorManager.registerSensor(ISensor.TYPE_MAGNETIC_FIELD);
        sensorManager.registerSensor(ISensor.TYPE_ACCELEROMETER);
        sensorManager.registerSensor(ISensor.TYPE_GYROSCOPE);
        sensorFusion.start();
    }

    @Override
    public void onStop() {
        super.onStop();

        sensorFusion.stop();
        sensorManager.disable();
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

    public boolean isLocalSensorsModeEnabled() {
        return sensorManager instanceof AndroidSensorManager;
    }
}
