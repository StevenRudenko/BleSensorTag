package sample.ble.sensortag.sensor.ti;

import sample.ble.sensortag.sensor.BaseSensor;

public abstract class TiRangeSensors<T, R> extends BaseSensor<T> implements TiPeriodicalSensor {
    /**
     * Constructor.
     *
     * @param data - instance of data.
     */
    protected TiRangeSensors(T data) {
        super(data);
    }

    public abstract R getMaxRange();
}
