package ua.lpnu.kzp;

/**
 * Штормове погодне спостереження.
 */
public final class StormReading extends WeatherReading {

    /**
     * Створює штормове погодне спостереження.
     *
     * @param date дата спостереження
     * @param temperature температура
     * @param humidity вологість
     * @param pressure атмосферний тиск
     * @param wind швидкість вітру
     */
    public StormReading(
            String date,
            double temperature,
            double humidity,
            double pressure,
            double wind) {

        super(
            validateState(
                    date,
                    temperature,
                    humidity,
                    pressure,
                    wind),
            WeatherKind.STORM);

        if (wind < STORM_WIND_THRESHOLD) {
            throw new IllegalArgumentException(
                    "Для StormReading швидкість вітру має бути не меншою за 15.0");
        }
    }

    /**
     * Обчислює індекс небезпеки штормового спостереження.
     *
     * @return індекс небезпеки
     */
    @Override
    public double dangerIndex() {
        return getWind() * 2.0 + getHumidity() / 10.0;
    }
}