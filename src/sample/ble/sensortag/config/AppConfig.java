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

    /** Device name. */
    public static final String BLE_DEVICE_NAME = "SensorTag";
}
