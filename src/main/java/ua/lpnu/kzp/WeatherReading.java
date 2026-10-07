package ua.lpnu.kzp;

import java.util.Locale;
import java.util.Objects;

/**
 * Базовий тип погодного спостереження метеостанції.
 */
public sealed abstract class WeatherReading
        permits DailyReading, StormReading {

    /**
     * Межа швидкості вітру, починаючи з якої
     * спостереження вважається штормовим.
     */
    protected static final double STORM_WIND_THRESHOLD = 15.0;

    private final String date;
    private final double temperature;
    private final double humidity;
    private final double pressure;
    private final double wind;
    private final WeatherKind kind;

    /**
     * Зберігає вже перевірений спільний стан погодного спостереження.
     */
    protected record ValidatedState(
            String date,
            double temperature,
            double humidity,
            double pressure,
            double wind) {
    }

    /**
     * Створює погодне спостереження з уже перевіреним станом.
     *
     * @param state перевірені погодні значення
     * @param kind тип погодного спостереження
     */
    protected WeatherReading(
            ValidatedState state,
            WeatherKind kind) {

        this.date = state.date();
        this.temperature = state.temperature();
        this.humidity = state.humidity();
        this.pressure = state.pressure();
        this.wind = state.wind();
        this.kind = kind;
    }

    /**
     * Перевіряє спільні інваріанти погодного спостереження.
     *
     * @param date дата спостереження
     * @param temperature температура
     * @param humidity вологість
     * @param pressure атмосферний тиск
     * @param wind швидкість вітру
     * @return перевірений стан
     */
    protected static ValidatedState validateState(
            String date,
            double temperature,
            double humidity,
            double pressure,
            double wind) {

        Objects.requireNonNull(
                date,
                "Дата не може бути null");

        if (date.isBlank()) {
            throw new IllegalArgumentException(
                    "Дата не може бути порожньою");
        }

        if (!date.matches("\\d{4}-\\d{2}-\\d{2}")) {
            throw new IllegalArgumentException(
                    "Дата повинна мати формат YYYY-MM-DD");
        }

        if (!Double.isFinite(temperature)
                || temperature < -273.15) {
            throw new IllegalArgumentException(
                    "Температура має бути скінченною і не нижчою за -273.15");
        }

        if (!Double.isFinite(humidity)
                || humidity < 0.0
                || humidity > 100.0) {
            throw new IllegalArgumentException(
                    "Вологість має бути в межах від 0 до 100");
        }

        if (!Double.isFinite(pressure)
                || pressure < 0.0) {
            throw new IllegalArgumentException(
                    "Тиск має бути скінченним і не від'ємним");
        }

        if (!Double.isFinite(wind)
                || wind < 0.0) {
            throw new IllegalArgumentException(
                    "Швидкість вітру має бути скінченною і не від'ємною");
        }

        return new ValidatedState(
                date,
                temperature,
                humidity,
                pressure,
                wind);
    }

    /**
     * Створює погодний запис із CSV-рядка.
     *
     * @param line рядок у форматі
     *             date;temperature;humidity;pressure;wind
     * @return створене погодне спостереження
     * @throws IllegalArgumentException якщо рядок має неправильний формат
     */
    public static WeatherReading fromCsv(String line) {
        Objects.requireNonNull(line, "Рядок не може бути null");

        String[] fields = line.split(";", -1);

        if (fields.length != 5) {
            throw new IllegalArgumentException("Очікується 5 полів");
        }

        try {
            String date = fields[0].trim();
            double temperature =
                    Double.parseDouble(fields[1].trim());
            double humidity =
                    Double.parseDouble(fields[2].trim());
            double pressure =
                    Double.parseDouble(fields[3].trim());
            double wind =
                    Double.parseDouble(fields[4].trim());

            return createReading(
                    date,
                    temperature,
                    humidity,
                    pressure,
                    wind);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(
                    "Числове поле має неправильний формат",
                    exception);
        }
    }

    /**
     * Створює відповідний підтип погодного спостереження.
     */
    private static WeatherReading createReading(
            String date,
            double temperature,
            double humidity,
            double pressure,
            double wind) {

        if (wind >= STORM_WIND_THRESHOLD) {
            return new StormReading(
                    date,
                    temperature,
                    humidity,
                    pressure,
                    wind);
        }

        return new DailyReading(
                date,
                temperature,
                humidity,
                pressure,
                wind);
    }

    /**
     * Створює новий Builder погодного запису.
     *
     * @return Builder погодного запису
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder для створення об'єктів WeatherReading.
     */
    public static final class Builder {

        private String date;
        private Double temperature;
        private Double humidity;
        private Double pressure;
        private Double wind;

        private Builder() {
        }

        /**
         * Задає дату спостереження.
         *
         * @param date дата спостереження
         * @return цей Builder
         */
        public Builder date(String date) {
            this.date = date;
            return this;
        }

        /**
         * Задає температуру.
         *
         * @param temperature температура
         * @return цей Builder
         */
        public Builder temperature(double temperature) {
            this.temperature = temperature;
            return this;
        }

        /**
         * Задає вологість.
         *
         * @param humidity вологість
         * @return цей Builder
         */
        public Builder humidity(double humidity) {
            this.humidity = humidity;
            return this;
        }

        /**
         * Задає атмосферний тиск.
         *
         * @param pressure атмосферний тиск
         * @return цей Builder
         */
        public Builder pressure(double pressure) {
            this.pressure = pressure;
            return this;
        }

        /**
         * Задає швидкість вітру.
         *
         * @param wind швидкість вітру
         * @return цей Builder
         */
        public Builder wind(double wind) {
            this.wind = wind;
            return this;
        }

        /**
         * Створює відповідний підтип WeatherReading.
         *
         * @return коректне погодне спостереження
         */
        public WeatherReading build() {
            if (temperature == null
                    || humidity == null
                    || pressure == null
                    || wind == null) {
                throw new IllegalStateException(
                        "Усі числові поля мають бути задані");
            }

            return createReading(
                    date,
                    temperature,
                    humidity,
                    pressure,
                    wind);
        }
    }

    /**
     * Повертає дату спостереження.
     *
     * @return дата
     */
    public final String getDate() {
        return date;
    }

    /**
     * Повертає температуру.
     *
     * @return температура
     */
    public final double getTemperature() {
        return temperature;
    }

    /**
     * Повертає вологість.
     *
     * @return вологість
     */
    public final double getHumidity() {
        return humidity;
    }

    /**
     * Повертає атмосферний тиск.
     *
     * @return атмосферний тиск
     */
    public final double getPressure() {
        return pressure;
    }

    /**
     * Повертає швидкість вітру.
     *
     * @return швидкість вітру
     */
    public final double getWind() {
        return wind;
    }

    /**
     * Повертає тип погодного спостереження.
     *
     * @return тип спостереження
     */
    public final WeatherKind getKind() {
        return kind;
    }

    /**
     * Повертає пару температури та вологості.
     *
     * @return температура і вологість
     */
    public final TemperatureHumidity temperatureHumidity() {
        return new TemperatureHumidity(
                temperature,
                humidity);
    }

    /**
     * Обчислює індекс небезпеки.
     *
     * @return індекс небезпеки
     */
    public abstract double dangerIndex();

    /**
     * Порівнює погодні спостереження за логічним станом.
     *
     * @param other інший об'єкт
     * @return true, якщо об'єкти логічно рівні
     */
    @Override
    public final boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        WeatherReading reading = (WeatherReading) other;

        return Double.compare(
                        temperature,
                        reading.temperature) == 0
                && Double.compare(
                        humidity,
                        reading.humidity) == 0
                && Double.compare(
                        pressure,
                        reading.pressure) == 0
                && Double.compare(
                        wind,
                        reading.wind) == 0
                && date.equals(reading.date)
                && kind == reading.kind;
    }

    /**
     * Повертає хеш-код, узгоджений з equals.
     *
     * @return хеш-код погодного спостереження
     */
    @Override
    public final int hashCode() {
        return Objects.hash(
                date,
                temperature,
                humidity,
                pressure,
                wind,
                kind);
    }

    /**
     * Повертає короткий опис конкретного підтипу спостереження.
     *
     * @return опис підтипу
     */
    public final String subtypeDescription() {
        return switch (this) {
            case DailyReading reading ->
                    String.format(
                            Locale.ROOT,
                            "Добове спостереження, вітер %.2f м/с",
                            reading.getWind());

            case StormReading reading ->
                    String.format(
                            Locale.ROOT,
                            "Штормове спостереження, вітер %.2f м/с",
                            reading.getWind());
        };
    }

    /**
     * Повертає текстове подання погодного запису.
     *
     * @return форматований запис
     */
    @Override
    public final String toString() {
        return String.format(
                Locale.ROOT,
                "%s: temperature=%.2f, humidity=%.2f, "
                        + "pressure=%.2f, wind=%.2f",
                date,
                temperature,
                humidity,
                pressure,
                wind);
    }
}