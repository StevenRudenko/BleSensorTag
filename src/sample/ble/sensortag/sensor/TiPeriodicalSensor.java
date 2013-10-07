package sample.ble.sensortag.sensor;

/**
 * Created by steven on 10/7/13.
 */
public interface TiPeriodicalSensor {

    public int getMinPeriod();

    public int getMaxPeriod();

    public void setPeriod(int period);

    public int getPeriod();
}
