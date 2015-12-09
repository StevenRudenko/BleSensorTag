package sample.ble.sensortag.fusion;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import sample.ble.sensortag.R;

/** Sensor fusion activity. */
public class SensorFusionActivity extends AppCompatActivity {
    /** Log tag. */
    @SuppressWarnings("UnusedDeclaration")
    private final static String TAG = SensorFusionActivity.class.getSimpleName();
    /** String. BLE device address. */
    public static final String EXTRA_DEVICE_ADDRESS = SensorFusionFragment.ARG_DEVICE_ADDRESS;

    /** Fusion fragment. */
    public SensorFusionFragment sensorFusionFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.title_demo_sensor_fusion);
        actionBar.setDisplayHomeAsUpEnabled(true);

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
        if (!sensorFusionFragment.isLocalSensorsModeEnabled()) {
            return false;
        }

        getMenuInflater().inflate(R.menu.sensor_fusion, menu);

        final MenuItem lockOrientationItem = menu.findItem(R.id.menu_lock_orientation);
        lockOrientation(lockOrientationItem);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!sensorFusionFragment.isLocalSensorsModeEnabled()) {
            return super.onOptionsItemSelected(item);
        }

        final int itemId = item.getItemId();
        switch (itemId) {
            case R.id.menu_lock_orientation:
                item.setChecked(!item.isChecked());
                lockOrientation(item);
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void lockOrientation(MenuItem item) {
        if (item.isChecked()) {
            item.setIcon(R.drawable.ic_action_lock_orientation_off);
            item.setTitle(R.string.menu_lock_orientation);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        } else {
            item.setIcon(R.drawable.ic_action_lock_orientation_on);
            item.setTitle(R.string.menu_unlock_orientation);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        }
    }
}
