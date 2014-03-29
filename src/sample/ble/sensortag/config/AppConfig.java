package sample.ble.sensortag.config;

public interface AppConfig {
    /**
     * Indicates whether BleService is used as remote service.<br/>
     * It would send broadcast messages in this case.
     */
    public static final boolean REMOTE_BLE_SERVICE = false;
    /**
     * Indicates whether {@link sample.ble.sensortag.BleSensorsRecordService} would be enabled.
     */
    public static final boolean ENABLE_RECORD_SERVICE = false;
    /**
     * Enables magnet sensor to be used while sensor fusion calculation.
     */
    public static final boolean SENSOR_FUSION_USE_MAGNET_SENSOR = false;

}
