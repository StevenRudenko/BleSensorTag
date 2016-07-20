package sample.ble.sensortag.ui;

import com.chimeraiot.android.ble.BleService;
import com.chimeraiot.android.ble.BleServiceBindingActivity;
import com.chimeraiot.android.ble.sensor.DeviceDef;
import com.chimeraiot.android.ble.sensor.Sensor;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.List;

import sample.ble.sensortag.R;
import sample.ble.sensortag.BleSensorService;
import sample.ble.sensortag.sensor.BaseSensor;
import sample.ble.sensortag.ui.adapters.BleServicesAdapter;
import sample.ble.sensortag.config.AppConfig;
import sample.ble.sensortag.fusion.SensorFusionActivity;
import sample.ble.sensortag.info.InfoService;
import sample.ble.sensortag.sensor.ti.TiPeriodicalSensor;

/**
 * For a given BLE device, this Activity provides the user interface to connect, display data,
 * and display GATT services and characteristics supported by the device.  The Activity
 * communicates with {@code BleService}, which in turn interacts with the
 * Bluetooth LE API.
 */
public class DeviceServicesActivity extends BleServiceBindingActivity
        implements ExpandableListView.OnChildClickListener,
        BleServicesAdapter.OnServiceItemClickListener {
    /** Log tag. */
    @SuppressWarnings("UnusedDeclaration")
    private final static String TAG = DeviceServicesActivity.class.getSimpleName();

    /** Data output field for active sensor. */
    private TextView dataCharacteristic;
    /** Data output field for active sensor. */
    private TextView dataValue;
    /** Services list. */
    private ExpandableListView gattServicesList;
    /** Services adapter. */
    private BleServicesAdapter gattServiceAdapter;

    /** Active sensor. */
    private BaseSensor<?> activeSensor;

    @Override
    public Class<? extends BleService> getServiceClass() {
        return BleSensorService.class;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_services_activity);

        gattServicesList = (ExpandableListView) findViewById(R.id.gatt_services_list);
        gattServicesList.setOnChildClickListener(this);
        final View emptyView = findViewById(R.id.empty_view);
        gattServicesList.setEmptyView(emptyView);

        dataCharacteristic = (TextView) findViewById(R.id.data_characteristic_uuid);
        dataValue = (TextView) findViewById(R.id.data_characteristic_value);

        //noinspection ConstantConditions
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar actionBar = getSupportActionBar();
        final String deviceName = getDeviceName();
        if (TextUtils.isEmpty(deviceName)) {
            //noinspection ConstantConditions
            actionBar.setTitle(getDeviceAddress());
        } else {
            //noinspection ConstantConditions
            actionBar.setTitle(deviceName);
            actionBar.setSubtitle(getDeviceAddress());
        }
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final String deviceName = getDeviceName();
        if (deviceName != null) {
            getMenuInflater().inflate(R.menu.gatt_services, menu);
            // enable demo for SensorTag device only
            menu.findItem(R.id.menu_demo).setEnabled(
                    deviceName.startsWith(AppConfig.SENSOR_TAG_DEVICE_NAME));
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_demo:
                final Intent demoIntent = new Intent();
                demoIntent.setClass(DeviceServicesActivity.this, SensorFusionActivity.class);
                demoIntent.putExtra(SensorFusionActivity.EXTRA_DEVICE_ADDRESS, getDeviceAddress());
                startActivity(demoIntent);
                break;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDisconnected(final String name, final String address) {
        super.onDisconnected(name, address);
        finish();
    }

    @Override
    public void onServiceDiscovered(final String name, final String address) {
        super.onServiceDiscovered(name, address);
        displayGattServices(getBleManager().getSupportedGattServices(address));
    }

    @Override
    public void onCharacteristicChanged(final String name, final String address,
            final String serviceUuid,
            final String characteristicUuid) {
        super.onCharacteristicChanged(name, address, serviceUuid, characteristicUuid);
        final Sensor<?> sensor = (Sensor<?>) getBleManager().getDeviceDefCollection()
                .get(name, address).getSensor(serviceUuid);
        if (sensor != null) {
            String uuid = sensor.getName();
            if (TextUtils.isEmpty(uuid)) {
                uuid = characteristicUuid;
            }
            dataCharacteristic.setText(uuid);

            if (sensor instanceof BaseSensor) {
                final BaseSensor<?> tiSensor = (BaseSensor<?>) sensor;
                dataValue.setText(tiSensor.getDataString());
            } else if (sensor instanceof InfoService) {
                final InfoService<?> infoSensor = (InfoService<?>) sensor;
                dataValue.setText(infoSensor.getValue());
            } else {
                final Object data = sensor.getData();
                dataValue.setText(String.valueOf(data));
            }
        }
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
            int childPosition, long id) {
        if (gattServiceAdapter == null) {
            return false;
        }

        final BluetoothGattCharacteristic characteristic = gattServiceAdapter.getChild(
                groupPosition, childPosition);
        final DeviceDef def = getBleManager().getDeviceDefCollection().get(
                getDeviceName(), getDeviceAddress());
        if (def == null) {
            return false;
        }

        final Sensor<?> sensor = (Sensor<?>) def.getSensor(
                characteristic.getService().getUuid().toString());
        if (sensor == null) {
            return true;
        }
        if (sensor == activeSensor) {
            return true;
        }

        final String address = getDeviceAddress();
        if (sensor instanceof BaseSensor) {
            final BaseSensor<?> baseSensor = (BaseSensor<?>) sensor;
            if (activeSensor != null) {
                activeSensor.setEnabled(false);
                getBleManager().update(address, activeSensor, activeSensor.getConfigUUID(), null);
            }

            activeSensor = baseSensor;
            baseSensor.setEnabled(true);
            getBleManager().update(address, sensor, baseSensor.getConfigUUID(), null);
            getBleManager().listen(address, sensor, baseSensor.getDataUUID());
        } else {
            getBleManager().read(address, sensor, characteristic.getUuid().toString());
        }
        return true;
    }

    @Override
    public void onServiceUpdated(BluetoothGattService service) {
        final BaseSensor<?> sensor = (BaseSensor<?>) getBleManager().getDeviceDefCollection()
                .get(getDeviceName(), getDeviceAddress())
                .getSensor(service.getUuid().toString());
        if (sensor != null && sensor instanceof TiPeriodicalSensor) {
            getBleManager().update(getDeviceAddress(), sensor,
                    ((TiPeriodicalSensor) sensor).getPeriodUUID(), null);
        }
    }

    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) {
            return;
        }
        final DeviceDef def = getBleManager().getDeviceDefCollection().get(
                getDeviceName(), getDeviceAddress());
        gattServiceAdapter = new BleServicesAdapter(this, gattServices, def);
        gattServiceAdapter.setServiceListener(this);
        gattServicesList.setAdapter(gattServiceAdapter);
    }
}
