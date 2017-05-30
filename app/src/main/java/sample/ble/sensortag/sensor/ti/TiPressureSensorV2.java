package sample.ble.sensortag.sensor.ti;

/** TI key sensor V2. */
public class TiPressureSensorV2 extends TiPressureSensor{
    /** Calibration UUID. */
    private static final String UUID_CALIBRATION = "f000aa44-0451-4000-b000-000000000000";

    TiPressureSensorV2(TiSensorTag model) {
        super(model);
    }

    @Override
    protected String getCalibratoionUUID() {
        return UUID_CALIBRATION;
    }
}
