package ua.lpnu.kzp;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

/**
 * Зберігає та відновлює записи метеостанції у CSV.
 */
public final class WeatherReadingCsvStorage {

    private WeatherReadingCsvStorage() {
    }

    /**
     * Перетворює предметні спостереження та записує їх у CSV.
     *
     * @param path шлях до CSV-файла
     * @param readings спостереження метеостанції
     * @throws DataStorageException якщо збереження неможливе
     */
    public static void write(
            Path path,
            List<WeatherReading> readings)
            throws DataStorageException {

        List<WeatherReadingRecord> records =
                readings.stream()
                        .map(WeatherReadingCsvStorage::toRecord)
                        .toList();

        CsvExporter.write(
                path,
                WeatherReadingRecord.class,
                records);
    }

    /**
     * Читає CSV-файл у UTF-8 та відновлює записи метеостанції.
     *
     * @param path шлях до CSV-файла
     * @return відновлені записи без рядка заголовка
     * @throws DataStorageException якщо файл або CSV некоректні
     */
    public static List<WeatherReadingRecord> read(
            Path path)
            throws DataStorageException {

        final String csv;

        try {
            csv = Files.readString(
                    path,
                    StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new DataStorageException(
                    "Не вдалося прочитати CSV: " + path,
                    exception);
        }

        List<List<String>> rows =
                CsvParser.parseDocument(csv);

        if (rows.isEmpty()
                || rows.size() == 1) {
            return List.of();
        }

        try {
            return rows.stream()
                    .skip(1)
                    .map(WeatherReadingCsvStorage::restoreUnchecked)
                    .toList();

        } catch (CsvRestoreRuntimeException exception) {
            throw exception.storageException();
        }
    }

    private static WeatherReadingRecord toRecord(
            WeatherReading reading) {

        return new WeatherReadingRecord(
                LocalDate.parse(reading.getDate()),
                reading.getTemperature(),
                reading.getHumidity(),
                reading.getPressure(),
                reading.getWind());
    }

    private static WeatherReadingRecord restoreUnchecked(
            List<String> fields) {

        try {
            return WeatherReadingRecord.fromFields(fields);
        } catch (DataStorageException exception) {
            throw new CsvRestoreRuntimeException(exception);
        }
    }

    private static final class CsvRestoreRuntimeException
            extends RuntimeException {

        private final DataStorageException storageException;

        private CsvRestoreRuntimeException(
                DataStorageException storageException) {

            super(storageException);
            this.storageException = storageException;
        }

        private DataStorageException storageException() {
            return storageException;
        }
    }
}