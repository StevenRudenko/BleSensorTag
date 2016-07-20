package sample.ble.sensortag.sensor.ti;

import android.bluetooth.BluetoothGattCharacteristic;

import sample.ble.sensortag.sensor.BaseSensor;

import static java.lang.Math.pow;

/** TI temperature sensor. */
public class TiTemperatureSensor extends BaseSensor<TiSensorTag> {
    /** Service UUID. */
    private static final String UUID_SERVICE = "f000aa00-0451-4000-b000-000000000000";
    /** Data UUID. */
    private static final String UUID_DATA = "f000aa01-0451-4000-b000-000000000000";
    /** Configuration UUID. */
    private static final String UUID_CONFIG = "f000aa02-0451-4000-b000-000000000000";

    TiTemperatureSensor(TiSensorTag model) {
        super(model);
    }

    @Override
    public String getName() {
        return "Temperature";
    }

    @Override
    public String getServiceUUID() {
        return UUID_SERVICE;
    }

    @Override
    public String getDataUUID() {
        return UUID_DATA;
    }

    @Override
    public String getConfigUUID() {
        return UUID_CONFIG;
    }

    @Override
    public String getDataString() {
        final float[] data = getData().getTemp();
        return "ambient=" + data[0] + "\ntarget=" + data[1];
    }

    @Override
    protected boolean apply(final BluetoothGattCharacteristic c, final TiSensorTag data) {
        /* The IR Temperature sensor produces two measurements;
         * Object ( AKA target or IR) Temperature,
         * and Ambient ( AKA die ) temperature.
         *
         * Both need some conversion, and Object temperature is dependent on Ambient temperature.
         *
         * They are stored as [ObjLSB, ObjMSB, AmbLSB, AmbMSB] (4 bytes)
         * Which means we need to shift the bytes around to get the correct values.
         */

        double ambient = extractAmbientTemperature(c);
        double target = extractTargetTemperature(c, ambient);

        final float[] values = getData().getTemp();
        values[0] = (float) ambient;
        values[1] = (float) target;
        return true;
    }

    private static double extractAmbientTemperature(BluetoothGattCharacteristic c) {
        int offset = 2;
        return TiUtils.shortUnsignedAtOffset(c, offset) / 128.0;
    }

    private static double extractTargetTemperature(BluetoothGattCharacteristic c, double ambient) {
        Integer twoByteValue = TiUtils.shortSignedAtOffset(c, 0);

        double Vobj2 = twoByteValue.doubleValue();
        Vobj2 *= 0.00000015625;

        double Tdie = ambient + 273.15;

        double S0 = 5.593E-14;	// Calibration factor
        double a1 = 1.75E-3;
        double a2 = -1.678E-5;
        double b0 = -2.94E-5;
        double b1 = -5.7E-7;
        double b2 = 4.63E-9;
        double c2 = 13.4;
        double Tref = 298.15;
        double S = S0*(1+a1*(Tdie - Tref)+a2*pow((Tdie - Tref),2));
        double Vos = b0 + b1*(Tdie - Tref) + b2*pow((Tdie - Tref),2);
        double fObj = (Vobj2 - Vos) + c2*pow((Vobj2 - Vos),2);
        double tObj = pow(pow(Tdie,4) + (fObj/S),.25);

        return tObj - 273.15;
    }
}
