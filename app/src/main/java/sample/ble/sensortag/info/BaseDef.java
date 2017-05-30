package sample.ble.sensortag.info;

import com.chimeraiot.android.ble.sensor.DeviceDef;
import com.chimeraiot.android.ble.sensor.Sensor;

import java.util.ArrayList;
import java.util.List;

/** BLE base sensor group. */
public class BaseDef<M> extends DeviceDef<M> {
    /** Collection of sensors. */
    private final List<Sensor<M>> sensors = new ArrayList<>();

    /**
     * Constructor.
     * @param model - sensor model.
     */
    public BaseDef(M model) {
        super(model);
        sensors.add(new GapService<>(model));
        sensors.add(new DeviceInfoService<>(model));
        sensors.add(new GattService<>(model));
    }

    @Override
    public Sensor<M> getSensor(String uuid) {
        for (Sensor<M> sensor : sensors) {
            if (sensor.getServiceUUID().equals(uuid)) {
                return sensor;
            }
        }
        return null;
    }

}
