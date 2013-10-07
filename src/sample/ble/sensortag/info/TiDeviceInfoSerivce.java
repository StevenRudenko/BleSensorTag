package sample.ble.sensortag.info;

import java.util.HashMap;

/**
 * Created by steven on 10/7/13.
 */
public class TiDeviceInfoSerivce extends TiInfoService {

    private static final String UUID_SERVICE = "0000180a-0000-1000-8000-00805f9b34fb";

    private static final String UUID_SYSTEM_ID = "00002a23-0000-1000-8000-00805f9b34fb";
    private static final String UUID_MODEL_NUMBER = "00002a24-0000-1000-8000-00805f9b34fb";
    private static final String UUID_SERIAL_NUMBER = "00002a25-0000-1000-8000-00805f9b34fb";
    private static final String UUID_FIRMWARE_REV = "00002a26-0000-1000-8000-00805f9b34fb";
    private static final String UUID_HARDWARE_REV = "00002a27-0000-1000-8000-00805f9b34fb";
    private static final String UUID_SOFTWARE_REV = "00002a28-0000-1000-8000-00805f9b34fb";
    private static final String UUID_MANUFACTURER_NAME = "00002a29-0000-1000-8000-00805f9b34fb";
    private static final String UUID_CERT = "00002a2a-0000-1000-8000-00805f9b34fb";
    private static final String UUID_PNP_ID = "00002a50-0000-1000-8000-00805f9b34fb";


    private static final HashMap<String, String> CHARACTERISTIC_MAP = new HashMap<String, String>();

    static {
        CHARACTERISTIC_MAP.put(UUID_SYSTEM_ID, "System ID");
        CHARACTERISTIC_MAP.put(UUID_MODEL_NUMBER, "Model Number");
        CHARACTERISTIC_MAP.put(UUID_SERIAL_NUMBER, "Serial Number");
        CHARACTERISTIC_MAP.put(UUID_FIRMWARE_REV, "Firmware Revision");
        CHARACTERISTIC_MAP.put(UUID_HARDWARE_REV, "Hardware Revision");
        CHARACTERISTIC_MAP.put(UUID_SOFTWARE_REV, "Software Revision");
        CHARACTERISTIC_MAP.put(UUID_MANUFACTURER_NAME, "Manufacturer Name");
        CHARACTERISTIC_MAP.put(UUID_CERT, "IEEE 11073-20601 Regulatory Certification Data List\n");
        CHARACTERISTIC_MAP.put(UUID_PNP_ID, "PnP ID");
    }

    @Override
    public String getUUID() {
        return UUID_SERVICE;
    }

    @Override
    public String getName() {
        return "Device Information";
    }

    @Override
    public String getCharacteristicName(String uuid) {
        if (!CHARACTERISTIC_MAP.containsKey(uuid))
            return "Unknown";
        return CHARACTERISTIC_MAP.get(uuid);
    }
}
