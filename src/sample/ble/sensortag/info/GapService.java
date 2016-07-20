package sample.ble.sensortag.info;

import java.util.HashMap;

/** BLE GAP service. */
public class GapService<T> extends InfoService<T> {
    /** Service UUID. */
    private static final String UUID_SERVICE = "00001800-0000-1000-8000-00805f9b34fb";
    /** Device name UUID. */
    private static final String UUID_DEVICE_NAME = "00002a00-0000-1000-8000-00805f9b34fb";
    /** Appearance UUID. */
    private static final String UUID_APPEARANCE = "00002a01-0000-1000-8000-00805f9b34fb";
    /** PPF UUID. */
    private static final String UUID_PPF = "00002a02-0000-1000-8000-00805f9b34fb";
    /** Re-connection address UUID. */
    private static final String UUID_RECCONECTION_ADDRESS = "00002a03-0000-1000-8000-00805f9b34fb";
    /** PPCP UUID. */
    private static final String UUID_PPCP = "00002a04-0000-1000-8000-00805f9b34fb";

    /** Characteristics. */
    private static final HashMap<String, String> CHARACTERISTIC_MAP = new HashMap<>();

    static {
        CHARACTERISTIC_MAP.put(UUID_DEVICE_NAME, "Device name");
        CHARACTERISTIC_MAP.put(UUID_APPEARANCE, "Appearance");
        CHARACTERISTIC_MAP.put(UUID_PPF, "Peripheral Privacy Flag");
        CHARACTERISTIC_MAP.put(UUID_RECCONECTION_ADDRESS, "Reconnection address");
        CHARACTERISTIC_MAP.put(UUID_PPCP, "Peripheral Preferred Connection Parameters");
    }

    protected GapService(T model) {
        super(model);
    }


    @Override
    public String getServiceUUID() {
        return UUID_SERVICE;
    }

    @Override
    public String getName() {
        return "GAP Service";
    }

    @Override
    public String getCharacteristicName(String uuid) {
        if (!CHARACTERISTIC_MAP.containsKey(uuid)) {
            return "Unknown";
        }
        return CHARACTERISTIC_MAP.get(uuid);
    }

}
