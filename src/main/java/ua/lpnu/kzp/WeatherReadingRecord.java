package ua.lpnu.kzp;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
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
     * Створює запис з одного CSV-рядка.
     *
     * @param line CSV-рядок
     * @return відновлений запис
     * @throws DataStorageException якщо CSV некоректний
     */
    public static WeatherReadingRecord fromCsv(
            String line)
            throws DataStorageException {

        List<String> fields =
                CsvParser.parse(line);

        return fromFields(fields);
    }

    /**
     * Створює запис із уже розібраних CSV-полів.
     *
     * @param fields значення CSV-полів
     * @return відновлений запис
     * @throws DataStorageException якщо значення некоректні
     */
    static WeatherReadingRecord fromFields(
            List<String> fields)
            throws DataStorageException {

        Objects.requireNonNull(
                fields,
                "CSV-поля не можуть бути null");

        if (fields.size() != 5) {
            throw new DataStorageException(
                    "Очікується 5 CSV-полів");
        }

        final LocalDate date;

        try {
            date = LocalDate.parse(
                    fields.get(0));

        } catch (DateTimeParseException exception) {
            throw new DataStorageException(
                    "Некоректна дата у CSV-записі метеостанції",
                    exception);
        }

        try {
            double humidity =
                    Double.parseDouble(
                            fields.get(1));

            double pressure =
                    Double.parseDouble(
                            fields.get(2));

            double temperature =
                    Double.parseDouble(
                            fields.get(3));

            double wind =
                    Double.parseDouble(
                            fields.get(4));

            return new WeatherReadingRecord(
                    date,
                    temperature,
                    humidity,
                    pressure,
                    wind);

        } catch (IllegalArgumentException exception) {
            throw new DataStorageException(
                    "Некоректний CSV-запис метеостанції",
                    exception);
        }
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