package ua.lpnu.kzp;

import java.util.Locale;
import java.util.Objects;

/**
 * Описує одне погодне спостереження метеостанції.
 */
public final class WeatherReading {

    private final String date;
    private final double temperature;
    private final double humidity;
    private final double pressure;
    private final double wind;

    /**
     * Створює коректний запис метеостанції.
     *
     * @param date дата спостереження
     * @param temperature температура у градусах Цельсія
     * @param humidity відносна вологість у відсотках
     * @param pressure атмосферний тиск
     * @param wind швидкість вітру
     */
    public WeatherReading(
            String date,
            double temperature,
            double humidity,
            double pressure,
            double wind) {

        this.date = Objects.requireNonNull(
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

        if (!Double.isFinite(pressure) || pressure < 0.0) {
            throw new IllegalArgumentException(
                    "Тиск має бути скінченним і не від'ємним");
        }

        if (!Double.isFinite(wind) || wind < 0.0) {
            throw new IllegalArgumentException(
                    "Швидкість вітру має бути скінченною і не від'ємною");
        }

        this.temperature = temperature;
        this.humidity = humidity;
        this.pressure = pressure;
        this.wind = wind;
    }

    /**
     * Створює погодний запис із CSV-рядка.
     *
     * @param line рядок у форматі date;temperature;humidity;pressure;wind
     * @return створений погодний запис
     * @throws IllegalArgumentException якщо рядок має неправильний формат
     */
    public static WeatherReading fromCsv(String line) {
        Objects.requireNonNull(line, "Рядок не може бути null");

        String[] fields = line.split(";", -1);

        if (fields.length != 5) {
            throw new IllegalArgumentException("Очікується 5 полів");
        }

        try {
            return new WeatherReading(
                    fields[0].trim(),
                    Double.parseDouble(fields[1].trim()),
                    Double.parseDouble(fields[2].trim()),
                    Double.parseDouble(fields[3].trim()),
                    Double.parseDouble(fields[4].trim()));
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(
                    "Числове поле має неправильний формат",
                    exception);
        }
    }

    /**
     * Створює новий builder для погодного запису.
     *
     * @return builder погодного запису
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

        public Builder date(String date) {
            this.date = date;
            return this;
        }

        public Builder temperature(double temperature) {
            this.temperature = temperature;
            return this;
        }

        public Builder humidity(double humidity) {
            this.humidity = humidity;
            return this;
        }

        public Builder pressure(double pressure) {
            this.pressure = pressure;
            return this;
        }

        public Builder wind(double wind) {
            this.wind = wind;
            return this;
        }

        /**
         * Створює WeatherReading через основний конструктор.
         *
         * @return коректний погодний запис
         */
        public WeatherReading build() {
            if (temperature == null
                    || humidity == null
                    || pressure == null
                    || wind == null) {
                throw new IllegalStateException(
                        "Усі числові поля мають бути задані");
            }

            return new WeatherReading(
                    date,
                    temperature,
                    humidity,
                    pressure,
                    wind);
        }
    }

    /** Повертає дату спостереження. */
    public String getDate() {
        return date;
    }

    /** Повертає температуру. */
    public double getTemperature() {
        return temperature;
    }

    /** Повертає вологість. */
    public double getHumidity() {
        return humidity;
    }

    /** Повертає атмосферний тиск. */
    public double getPressure() {
        return pressure;
    }

    /** Повертає швидкість вітру. */
    public double getWind() {
        return wind;
    }

    /**
     * Повертає пару температури та вологості.
     *
     * @return температура і вологість
     */
    public TemperatureHumidity temperatureHumidity() {
        return new TemperatureHumidity(temperature, humidity);
    }

    /**
     * Повертає текстове подання погодного запису.
     *
     * @return форматований запис
     */
    @Override
    public String toString() {
        return String.format(
                Locale.ROOT,
                "%s: temperature=%.2f, humidity=%.2f, pressure=%.2f, wind=%.2f",
                date,
                temperature,
                humidity,
                pressure,
                wind);
    }
}