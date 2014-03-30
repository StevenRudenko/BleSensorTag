package sample.ble.sensortag.fusion;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import sample.ble.sensortag.R;

/**
 * Created by steven on 10/18/13.
 */
public class SensorFusionActivity extends Activity {
    @SuppressWarnings("UnusedDeclaration")
    private final static String TAG = SensorFusionActivity.class.getSimpleName();

    public static final String EXTRA_DEVICE_ADDRESS =
            SensorFusionFragment.EXTRA_DEVICE_ADDRESS;

    public SensorFusionFragment sensorFusionFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setTitle(R.string.title_demo_sensor_fusion);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.sensor_fusion_activity);

        sensorFusionFragment = (SensorFusionFragment) getFragmentManager().findFragmentByTag(
                SensorFusionFragment.TAG);
        if (sensorFusionFragment == null) {
            sensorFusionFragment = new SensorFusionFragment();
            sensorFusionFragment.setArguments(getIntent().getExtras());
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content, sensorFusionFragment, SensorFusionFragment.TAG)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!sensorFusionFragment.isLocalSensorsModeEnabled())
            return false;

        getMenuInflater().inflate(R.menu.sensor_fusion, menu);

        final MenuItem lockOrientationItem = menu.findItem(R.id.menu_lock_orientaion);
        lockOrientation(lockOrientationItem);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!sensorFusionFragment.isLocalSensorsModeEnabled())
            return false;

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
            item.setTitle(R.string.menu_lock_orientation);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        } else {
            item.setIcon(R.drawable.ic_action_lock_orientaion_on);
            item.setTitle(R.string.menu_unlock_orientation);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        }
    }
}
