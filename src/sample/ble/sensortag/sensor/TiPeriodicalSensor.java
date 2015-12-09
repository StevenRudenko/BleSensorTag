package sample.ble.sensortag.sensor;

/** Periodical sensor. */
public interface TiPeriodicalSensor {

    String getPeriodUUID();

    int getMinPeriod();

    int getMaxPeriod();

    void setPeriod(int period);

    int getPeriod();
}
