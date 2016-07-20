package sample.ble.sensortag.sensor.ti;

/** TI key sensor V2. */
public class TiKeysSensor2 extends TiKeysSensor {

    private static final String UUID_SERVICE = "0000ffe0-0000-1000-8000-00805f9b34fb";
    private static final String UUID_DATA = "0000ffe1-0000-1000-8000-00805f9b34fb";
    private static final String UUID_CONFIG = null;

    TiKeysSensor2(TiSensorTag model) {
        super(model);
    }

    @Override
    public String getName() {
        return "Simple Keys";
    }

    @Override
    public String getServiceUUID() {
        return UUID_SERVICE;
    }

    @Override
    public String getDataUUID() {
        return UUID_DATA;
    }

    @Override
    public String getConfigUUID() {
        return UUID_CONFIG;
    }

}
