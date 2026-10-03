package ua.lpnu.kzp;

/**
 * Зберігає пару значень температури та вологості.
 *
 * @param temperature температура у градусах Цельсія
 * @param humidity відносна вологість у відсотках
 */
public record TemperatureHumidity(
        double temperature,
        double humidity) {

    /**
     * Перевіряє допустимість значень.
     */
    public TemperatureHumidity {
        if (!Double.isFinite(temperature) || temperature < -273.15) {
            throw new IllegalArgumentException(
                    "Температура має бути скінченною і не нижчою за -273.15");
        }

        if (!Double.isFinite(humidity)
                || humidity < 0.0
                || humidity > 100.0) {
            throw new IllegalArgumentException(
                    "Вологість має бути в межах від 0 до 100");
        }
    }
}