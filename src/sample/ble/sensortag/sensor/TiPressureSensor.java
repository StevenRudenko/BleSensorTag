package sample.ble.sensortag.sensor;

import static java.lang.Math.pow;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

import java.util.UUID;

/**
 * Created by steven on 9/3/13.
 */
public class TiPressureSensor extends TiSensor<Double> {

    public static final TiPressureSensor INSTANCE = new TiPressureSensor();

    private int[] calibration;

    private TiPressureSensor() {
        super();
    }

    @Override
    public String getName() {
        return "Pressure";
    }

    @Override
    public String getServiceUUID() {
        return "f000aa40-0451-4000-b000-000000000000";
    }

    @Override
    public String getDataUUID() {
        return "f000aa41-0451-4000-b000-000000000000";
    }

    @Override
    public String getConfigUUID() {
        return "f000aa42-0451-4000-b000-000000000000";
    }
    //TODO: there is period service


    @Override
    protected void enable(BluetoothGatt bluetoothGatt, boolean enable) {
        super.enable(bluetoothGatt, enable);

        final UUID serviceUuid = UUID.fromString(getServiceUUID());
        final UUID calibrationUuid = UUID.fromString("f000aa43-0451-4000-b000-000000000000");

        final BluetoothGattService magnetService = bluetoothGatt.getService(serviceUuid);
        final BluetoothGattCharacteristic config = magnetService.getCharacteristic(calibrationUuid);
        bluetoothGatt.readCharacteristic(config);
    }

    @Override
    public String toString(BluetoothGattCharacteristic c) {
        final double data = onCharacteristicChanged(c);
        return ""+data;
    }

    @Override
    public Double onCharacteristicChanged(BluetoothGattCharacteristic characteristic){
        // c holds the calibration coefficients

        final Integer t_r;	// Temperature raw value from sensor
        final Integer p_r;	// Pressure raw value from sensor
        final Double t_a; 	// Temperature actual value in unit centi degrees celsius
        final Double S;	// Interim value in calculation
        final Double O;	// Interim value in calculation
        final Double p_a; 	// Pressure actual value in unit Pascal.

        t_r = shortSignedAtOffset(characteristic, 0);
        p_r = shortUnsignedAtOffset(characteristic, 2);

        t_a = (100 * (calibration[0] * t_r / pow(2,8) + calibration[1] * pow(2,6))) / pow(2,16);
        S = calibration[2] + calibration[3] * t_r / pow(2,17) + ((calibration[4] * t_r / pow(2,15)) * t_r) / pow(2,19);
        O = calibration[5] * pow(2,14) + calibration[6] * t_r / pow(2,3) + ((calibration[7] * t_r / pow(2,15)) * t_r) / pow(2,4);
        p_a = (S * p_r + O) / pow(2,14);

        return p_a;
    }

}
