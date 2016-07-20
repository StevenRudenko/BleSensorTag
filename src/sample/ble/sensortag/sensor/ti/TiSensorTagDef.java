package sample.ble.sensortag.sensor.ti;

import com.chimeraiot.android.ble.sensor.Sensor;

import java.util.ArrayList;
import java.util.List;

import sample.ble.sensortag.info.BaseDef;

/** TI SensorTag sensor group. */
public class TiSensorTagDef extends BaseDef<TiSensorTag> {
    /** Collection of sensors. */
    private final List<Sensor<TiSensorTag>> sensors = new ArrayList<>();

    /**
     * Constructor.
     * @param address - sensor address.
     */
    public TiSensorTagDef(String address) {
        super(new TiSensorTag(address));
        final TiSensorTag model = getModel();
        sensors.add(new TiAccelerometerSensor(model));
        sensors.add(new TiGyroscopeSensor(model));
        sensors.add(new TiHumiditySensor(model));
        sensors.add(new TiKeysSensor(model));
        sensors.add(new TiKeysSensor2(model));
        sensors.add(new TiMagnetometerSensor(model));
        sensors.add(new TiPressureSensor(model));
        sensors.add(new TiTemperatureSensor(model));
        sensors.add(new TiTestSensor(model));
    }

    @Override
    public Sensor<TiSensorTag> getSensor(String uuid) {
        for (Sensor<TiSensorTag> sensor : sensors) {
            if (sensor.getServiceUUID().equals(uuid)) {
                return sensor;
            }
        }
        return super.getSensor(uuid);
    }

}
