package sample.ble.sensortag.sensor;

public abstract class TiRangeSensors<T, R> extends TiSensor<T> implements TiPeriodicalSensor {

    public abstract R getMaxRange();
}
