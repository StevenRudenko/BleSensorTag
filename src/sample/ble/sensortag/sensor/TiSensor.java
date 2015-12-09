package sample.ble.sensortag.sensor;

import com.chimeraiot.android.ble.sensor.Sensor;

/**
 * Base TI sensor implementation.
 * @param <T> - data type.
 */
public abstract class TiSensor<T> extends Sensor<T> {
    /** Enabled service. */
    private boolean enabled = false;

    /**
     * Constructor.
     * @param data - instance of data.
     */
    protected TiSensor(T data) {
        super(data);
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isConfigUUID(String uuid) {
        return false;
    }

    public abstract String getDataUUID();

    public abstract String getConfigUUID();

    public abstract String getDataString();

    @Override
    public String getCharacteristicName(String uuid) {
        if (uuid.equals(getConfigUUID())) {
            return "Config";
        } else if (uuid.equals(getDataUUID())) {
            return "Data";
        }
        return super.getCharacteristicName(uuid);
    }

}
