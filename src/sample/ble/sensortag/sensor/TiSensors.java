package sample.ble.sensortag.sensor;

import java.util.HashMap;

/**
 * Created by steven on 9/4/13.
 */
public class TiSensors {

    private static HashMap<String, TiSensor<?>> SENSORS = new HashMap<String, TiSensor<?>>();

    static {
        SENSORS.put(TiAccelerometerSensor.INSTANCE.getServiceUUID(), TiAccelerometerSensor.INSTANCE);
        SENSORS.put(TiGyroscopeSensor.INSTANCE.getServiceUUID(), TiGyroscopeSensor.INSTANCE);
        SENSORS.put(TiHumiditySensor.INSTANCE.getServiceUUID(), TiHumiditySensor.INSTANCE);
        SENSORS.put(TiKeysSensor.INSTANCE.getServiceUUID(), TiKeysSensor.INSTANCE);
        SENSORS.put(TiMagnetometerSensor.INSTANCE.getServiceUUID(), TiMagnetometerSensor.INSTANCE);
        SENSORS.put(TiPressureSensor.INSTANCE.getServiceUUID(), TiPressureSensor.INSTANCE);
        SENSORS.put(TiTemperatureSensor.INSTANCE.getServiceUUID(), TiTemperatureSensor.INSTANCE);
        SENSORS.put(TiTestSensor.INSTANCE.getServiceUUID(), TiTestSensor.INSTANCE);
    }

    public static TiSensor<?> getSensor(String uuid) {
        return SENSORS.get(uuid);
    }
}
