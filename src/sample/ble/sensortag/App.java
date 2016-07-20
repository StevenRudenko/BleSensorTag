package sample.ble.sensortag;

import com.chimeraiot.android.ble.BleConfig;
import com.chimeraiot.android.ble.sensor.DeviceDef;
import com.chimeraiot.android.ble.sensor.DeviceDefCollection;

import android.app.Application;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import sample.ble.sensortag.config.AppConfig;
import sample.ble.sensortag.info.BaseDef;
import sample.ble.sensortag.sensor.metawear.MetawearDef;
import sample.ble.sensortag.sensor.ti.TiSensorTagDef;

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
                    return new BaseDef<>((Void)null);
                }
                switch (name) {
                    case AppConfig.SENSOR_TAG_DEVICE_NAME:
                        return new TiSensorTagDef(address);
                    case AppConfig.METAWEAR_DEVICE_NAME:
                        return new MetawearDef(address);
                    default:
                        return new BaseDef<>((Void)null);
                }
            }
        };
        DEVICE_DEF_COLLECTION.register(AppConfig.SENSOR_TAG_DEVICE_NAME);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        BleConfig.setDebugEnabled(BuildConfig.DEBUG);
    }
}
