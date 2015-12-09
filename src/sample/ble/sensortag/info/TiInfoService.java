package sample.ble.sensortag.info;

import com.chimeraiot.android.ble.sensor.Sensor;

import android.bluetooth.BluetoothGattCharacteristic;

/**
 * BLE info service.
 * @param <M> data model.
 */
public abstract class TiInfoService<M> extends Sensor<M> {

    /** Data value. */
    private String value;

    protected TiInfoService(M model) {
        super(model);
    }

    public String getValue() {
        return value;
    }

    @Override
    protected boolean apply(final BluetoothGattCharacteristic c, final M data) {
        value = c.getStringValue(0);
        // always set it to true to notify update
        return true;
    }
}
