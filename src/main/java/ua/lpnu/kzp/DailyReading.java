package ua.lpnu.kzp;

/**
 * Звичайне добове погодне спостереження.
 */
public final class DailyReading extends WeatherReading {

    /**
     * Створює звичайне погодне спостереження.
     *
     * @param date дата спостереження
     * @param temperature температура
     * @param humidity вологість
     * @param pressure атмосферний тиск
     * @param wind швидкість вітру
     */
    public DailyReading(
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
            WeatherKind.DAILY);

        if (wind >= STORM_WIND_THRESHOLD) {
            throw new IllegalArgumentException(
                    "Для DailyReading швидкість вітру має бути меншою за 15.0");
        }
    }

    /**
     * Обчислює індекс небезпеки звичайного спостереження.
     *
     * @return індекс небезпеки
     */
    @Override
    public double dangerIndex() {
        return getWind() + getHumidity() / 20.0;
    }
}