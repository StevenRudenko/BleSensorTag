package sample.ble.sensortag.service;

import java.util.LinkedList;

import sample.ble.sensortag.sensor.TiSensor;

/**
 * Created by steven on 9/3/13.
 */
public class BluetoothGattQuery extends LinkedList<BluetoothGattQuery.SensorAction> {

    public static class SensorAction {
        public final TiSensor sensor;
        public final TiSensor.ExecuteAction action;

        private SensorAction(TiSensor sensor, TiSensor.ExecuteAction action) {
            this.sensor = sensor;
            this.action = action;
        }
    }

    public void push(TiSensor sensor, TiSensor.ExecuteAction action) {
        super.push(new SensorAction(sensor, action));
    }
}
