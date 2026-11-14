package ua.lpnu.kzp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class WeatherReadingCsvStorageTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    void roundTripRestoresEqualRecords()
            throws Exception {

        List<WeatherReadingRecord> original =
                List.of(
                        new WeatherReadingRecord(
                                LocalDate.of(2026, 9, 1),
                                18.5,
                                62.0,
                                1013.2,
                                3.4),

                        new WeatherReadingRecord(
                                LocalDate.of(2026, 9, 2),
                                -2.0,
                                80.0,
                                1005.0,
                                7.8));

        Path path =
                temporaryDirectory.resolve(
                        "weather.csv");

        CsvExporter.write(
                path,
                WeatherReadingRecord.class,
                original);

        List<WeatherReadingRecord> restored =
                WeatherReadingCsvStorage.read(path);

        assertEquals(original, restored);
    }

    @Test
    void roundTripPreservesDateKey()
            throws Exception {

        List<WeatherReadingRecord> original =
                List.of(
                        new WeatherReadingRecord(
                                LocalDate.of(2026, 9, 1),
                                18.5,
                                62.0,
                                1013.2,
                                3.4));

        Path path =
                temporaryDirectory.resolve(
                        "key.csv");

        CsvExporter.write(
                path,
                WeatherReadingRecord.class,
                original);

        List<WeatherReadingRecord> restored =
                WeatherReadingCsvStorage.read(path);

        assertEquals(
                original.get(0).date(),
                restored.get(0).date());
    }

    @Test
    void emptyFileReturnsEmptyList()
            throws Exception {

        Path path =
                temporaryDirectory.resolve(
                        "empty.csv");

        java.nio.file.Files.writeString(
                path,
                "",
                java.nio.charset.StandardCharsets.UTF_8);

        assertEquals(
                List.of(),
                WeatherReadingCsvStorage.read(path));
    }

    @Test
    void headerOnlyReturnsEmptyList()
            throws Exception {

        Path path =
                temporaryDirectory.resolve(
                        "header-only.csv");

        java.nio.file.Files.writeString(
                path,
                "дата,вологість,тиск,температура,вітер"
                        + System.lineSeparator(),
                java.nio.charset.StandardCharsets.UTF_8);

        assertEquals(
                List.of(),
                WeatherReadingCsvStorage.read(path));
    }

    @Test
    void invalidNumberIsReported()
            throws Exception {

        Path path =
                temporaryDirectory.resolve(
                        "invalid.csv");

        java.nio.file.Files.writeString(
                path,
                "дата,вологість,тиск,температура,вітер"
                        + System.lineSeparator()
                        + "2026-09-01,помилка,1013.2,18.5,3.4"
                        + System.lineSeparator(),
                java.nio.charset.StandardCharsets.UTF_8);

        DataStorageException exception =
                assertThrows(
                        DataStorageException.class,
                        () -> WeatherReadingCsvStorage.read(path));

        assertNotNull(exception.getCause());
    }

    @Test
    void missingFileKeepsIoCause() {
        Path path =
                temporaryDirectory.resolve(
                        "missing.csv");

        DataStorageException exception =
                assertThrows(
                        DataStorageException.class,
                        () -> WeatherReadingCsvStorage.read(path));

        assertNotNull(exception.getCause());
    }
}