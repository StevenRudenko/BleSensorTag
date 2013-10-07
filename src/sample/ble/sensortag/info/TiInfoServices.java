package sample.ble.sensortag.info;

import java.util.HashMap;

/**
 * Created by steven on 10/7/13.
 */
public class TiInfoServices {

    private static HashMap<String, TiInfoService> SERVICES = new HashMap<String, TiInfoService>();

    static {
        final TiGattSerivce gapSerivce = new TiGattSerivce();
        final TiGapSerivce gattSerivce = new TiGapSerivce();
        final TiDeviceInfoSerivce deviceInfoSerivce = new TiDeviceInfoSerivce();

        SERVICES.put(gapSerivce.getUUID(), gapSerivce);
        SERVICES.put(gattSerivce.getUUID(), gattSerivce);
        SERVICES.put(deviceInfoSerivce.getUUID(), deviceInfoSerivce);
    }

    public static TiInfoService getService(String uuid) {
        return SERVICES.get(uuid);
    }
}
