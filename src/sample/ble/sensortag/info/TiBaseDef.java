package sample.ble.sensortag.info;

import com.chimeraiot.android.ble.sensor.DeviceDef;
import com.chimeraiot.android.ble.sensor.Sensor;

import java.util.ArrayList;
import java.util.List;

/** BLE base sensor group. */
public class TiBaseDef<M> extends DeviceDef<M> {
    /** Collection of sensors. */
    private final List<Sensor<M>> sensors = new ArrayList<>();

    /**
     * Constructor.
     * @param model - sensor model.
     */
    public TiBaseDef(M model) {
        super(model);
        sensors.add(new TiGapService<>(model));
        sensors.add(new TiDeviceInfoService<>(model));
        sensors.add(new TiGattService<>(model));
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
