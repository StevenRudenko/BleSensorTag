package sample.ble.sensortag.config;

import java.util.HashSet;
import java.util.Set;

public class AppConfig {
    /** Debug flag to enable local sensors fustion. */
    public static final boolean LOCAL_SENSOR_FUSION = false;
    /** Indicates whether {@link sample.ble.sensortag.BleSensorsRecordService} would be enabled. */
    public static final boolean ENABLE_RECORD_SERVICE = false;
    /** Enables magnet sensor to be used while sensor fusion calculation. */
    public static final boolean SENSOR_FUSION_USE_MAGNET_SENSOR = false;

    /** Sensor Tag Device name. */
    public static final String SENSOR_TAG_DEVICE_NAME = "SensorTag";
    /** Sensor Tag Device name. */
    public static final String SENSOR_TAG_V2_DEVICE_NAME = "CC2650 SensorTag";
    /** Metawear Device name. */
    public static final String METAWEAR_DEVICE_NAME = "MetaWear";

    /** Supported devices. */
    public static final Set<String> SUPPORTED_DEVICES = new HashSet<>();
    public static final Set<String> SENSOR_FUSION_DEVICES = new HashSet<>();
    {
        // supported devices
        SUPPORTED_DEVICES.add(SENSOR_TAG_DEVICE_NAME);
        SUPPORTED_DEVICES.add(SENSOR_TAG_V2_DEVICE_NAME);
        SUPPORTED_DEVICES.add(METAWEAR_DEVICE_NAME);
        // sensor fusion devices
        SENSOR_FUSION_DEVICES.add(SENSOR_TAG_DEVICE_NAME);
    }
}
