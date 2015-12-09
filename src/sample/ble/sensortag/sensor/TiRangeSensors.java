package sample.ble.sensortag.sensor;

public abstract class TiRangeSensors<T, R> extends TiSensor<T> implements TiPeriodicalSensor {
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
