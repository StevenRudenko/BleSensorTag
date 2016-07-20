package sample.ble.sensortag.sensor.metawear;

import com.chimeraiot.android.ble.sensor.Sensor;

import java.util.ArrayList;
import java.util.List;

import sample.ble.sensortag.info.BaseDef;

/** TI SensorTag sensor group. */
public class MetawearDef extends BaseDef<Metawear> {
    /** Collection of sensors. */
    private final List<Sensor<Metawear>> sensors = new ArrayList<>();

    /**
     * Constructor.
     * @param address - sensor address.
     */
    public MetawearDef(String address) {
        super(new Metawear(address));
        final Metawear model = getModel();
        sensors.add(new MetawearAccelerometerSensor(model));
    }

    @Override
    public Sensor<Metawear> getSensor(String uuid) {
        for (Sensor<Metawear> sensor : sensors) {
            if (sensor.getServiceUUID().equals(uuid)) {
                return sensor;
            }
        }
        return super.getSensor(uuid);
    }

}
