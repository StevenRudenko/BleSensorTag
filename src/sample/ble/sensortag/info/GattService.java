package sample.ble.sensortag.info;

import java.util.HashMap;

/** BLE GATT service. */
public class GattService<T> extends InfoService<T> {
    /** Service UUID. */
    private static final String UUID_SERVICE = "00001801-0000-1000-8000-00805f9b34fb";
    /** Device name UUID. */
    private static final String UUID_DEVICE_NAME = "00002a05-0000-1000-8000-00805f9b34fb";

    /** Characteristics. */
    private static final HashMap<String, String> CHARACTERISTIC_MAP = new HashMap<>();

    static {
        CHARACTERISTIC_MAP.put(UUID_DEVICE_NAME, "Service Changed");
    }

    protected GattService(T model) {
        super(model);
    }

    @Override
    public String getServiceUUID() {
        return UUID_SERVICE;
    }

    @Override
    public String getName() {
        return "GATT Service";
    }

    @Override
    public String getCharacteristicName(String uuid) {
        if (!CHARACTERISTIC_MAP.containsKey(uuid)) {
            return "Unknown";
        }
        return CHARACTERISTIC_MAP.get(uuid);
    }

}
