package sample.ble.sensortag.config;

public class AppConfig {
    /** Debug flag to enable local sensors fustion. */
    public static final boolean LOCAL_SENSOR_FUSION = false;
    /**
     * Indicates whether {@link sample.ble.sensortag.BleSensorsRecordService} would be enabled.
     */
    public static final boolean ENABLE_RECORD_SERVICE = false;
    /**
     * Enables magnet sensor to be used while sensor fusion calculation.
     */
    public static final boolean SENSOR_FUSION_USE_MAGNET_SENSOR = false;

    /** Sensor Tag Device name. */
    public static final String SENSOR_TAG_DEVICE_NAME = "SensorTag";
    /** Metawear Device name. */
    public static final String METAWEAR_DEVICE_NAME = "MetaWear";
}
