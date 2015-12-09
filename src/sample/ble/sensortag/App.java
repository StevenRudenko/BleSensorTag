package sample.ble.sensortag;

import com.chimeraiot.android.ble.sensor.DeviceDef;
import com.chimeraiot.android.ble.sensor.DeviceDefCollection;

import android.app.Application;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import sample.ble.sensortag.config.AppConfig;
import sample.ble.sensortag.info.TiBaseDef;
import sample.ble.sensortag.sensor.TiSensorTagDef;

/** Application class. */
public class App extends Application {
    /** BLE device definitions collection. */
    public static final DeviceDefCollection DEVICE_DEF_COLLECTION;

    static {
        DEVICE_DEF_COLLECTION = new DeviceDefCollection() {
            @Nullable
            @Override
            public DeviceDef create(String name, String address) {
                if (TextUtils.isEmpty(name)) {
                    return new TiBaseDef<>((Void)null);
                }
                switch (name) {
                    case AppConfig.BLE_DEVICE_NAME:
                        return new TiSensorTagDef(address);
                    default:
                        return new TiBaseDef<>((Void)null);
                }
            }
        };
        DEVICE_DEF_COLLECTION.register(AppConfig.BLE_DEVICE_NAME);
    }

}
