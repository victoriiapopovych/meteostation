package ua.lpnu.kzp;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Плоский запис метеостанції для збереження та CSV-експорту.
 */
public final class WeatherReadingRecord {

    @CsvColumn("дата")
    private final LocalDate date;

    @CsvColumn("температура")
    private final double temperature;

    @CsvColumn("вологість")
    private final double humidity;

    @CsvColumn("тиск")
    private final double pressure;

    @CsvColumn("вітер")
    private final double wind;

    /**
     * Створює перевірений запис метеостанції.
     *
     * @param date дата спостереження
     * @param temperature температура у градусах Цельсія
     * @param humidity відносна вологість у відсотках
     * @param pressure атмосферний тиск
     * @param wind швидкість вітру
     */
    public WeatherReadingRecord(
            LocalDate date,
            double temperature,
            double humidity,
            double pressure,
            double wind) {

        this.date = Objects.requireNonNull(
                date,
                "Дата не може бути null");

        if (!Double.isFinite(temperature)
                || temperature < -273.15) {
            throw new IllegalArgumentException(
                    "Температура має бути скінченною "
                            + "і не нижчою за -273.15");
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
                    "Тиск має бути скінченним "
                            + "і не може бути від'ємним");
        }

        if (!Double.isFinite(wind)
                || wind < 0.0) {
            throw new IllegalArgumentException(
                    "Швидкість вітру має бути скінченною "
                            + "і не може бути від'ємною");
        }

        this.temperature = temperature;
        this.humidity = humidity;
        this.pressure = pressure;
        this.wind = wind;
    }

    /**
     * Повертає дату спостереження.
     *
     * @return дата
     */
    public LocalDate date() {
        return date;
    }

    /**
     * Повертає температуру.
     *
     * @return температура
     */
    public double temperature() {
        return temperature;
    }

    /**
     * Повертає вологість.
     *
     * @return вологість
     */
    public double humidity() {
        return humidity;
    }

    /**
     * Повертає атмосферний тиск.
     *
     * @return тиск
     */
    public double pressure() {
        return pressure;
    }

    /**
     * Повертає швидкість вітру.
     *
     * @return швидкість вітру
     */
    public double wind() {
        return wind;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof WeatherReadingRecord record)) {
            return false;
        }

        return Double.compare(
                        temperature,
                        record.temperature) == 0
                && Double.compare(
                        humidity,
                        record.humidity) == 0
                && Double.compare(
                        pressure,
                        record.pressure) == 0
                && Double.compare(
                        wind,
                        record.wind) == 0
                && date.equals(record.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                date,
                temperature,
                humidity,
                pressure,
                wind);
    }
}