package ua.lpnu.kzp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class CsvExporterTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    void exportsWeatherReadingRecordWithHeader()
            throws Exception {

        WeatherReadingRecord record =
                new WeatherReadingRecord(
                        LocalDate.of(2026, 9, 1),
                        18.5,
                        62.0,
                        1013.2,
                        3.4);

        Path path =
                temporaryDirectory.resolve(
                        "weather.csv");

        CsvExporter.write(
                path,
                WeatherReadingRecord.class,
                List.of(record));

        String csv =
                Files.readString(
                        path,
                        StandardCharsets.UTF_8);

        String[] lines =
                csv.split("\\R");

        assertEquals(
                "дата,вологість,тиск,температура,вітер",
                lines[0]);

        assertEquals(
                "2026-09-01,62.0,1013.2,18.5,3.4",
                lines[1]);
    }

    @Test
    void exportsHeaderForEmptyList()
            throws Exception {

        Path path =
                temporaryDirectory.resolve(
                        "empty.csv");

        CsvExporter.write(
                path,
                WeatherReadingRecord.class,
                List.of());

        String csv =
                Files.readString(
                        path,
                        StandardCharsets.UTF_8);

        assertTrue(
                csv.startsWith(
                        "дата,вологість,тиск,температура,вітер"));
    }

    @Test
    void exporterWorksWithAnotherAnnotatedClass()
            throws Exception {

        Path path =
                temporaryDirectory.resolve(
                        "generic.csv");

        CsvExporter.write(
                path,
                SampleRecord.class,
                List.of(
                        new SampleRecord(
                                "Київ",
                                10)));

        String csv =
                Files.readString(
                        path,
                        StandardCharsets.UTF_8);

        assertTrue(csv.contains("Київ"));
        assertTrue(csv.contains("10"));
    }

    private static final class SampleRecord {

        @CsvColumn("Місто")
        private final String city;

        @CsvColumn("Значення")
        private final int value;

        private SampleRecord(
                String city,
                int value) {

            this.city = city;
            this.value = value;
        }
    }

    @Test
    void overwritesExistingFileWithoutOldData()
            throws Exception {

        Path path =
                temporaryDirectory.resolve(
                        "overwrite.csv");

        WeatherReadingRecord first =
                new WeatherReadingRecord(
                        LocalDate.of(2026, 9, 1),
                        18.5,
                        62.0,
                        1013.2,
                        3.4);

        WeatherReadingRecord second =
                new WeatherReadingRecord(
                        LocalDate.of(2026, 9, 2),
                        20.0,
                        70.0,
                        1010.0,
                        4.0);

        CsvExporter.write(
                path,
                WeatherReadingRecord.class,
                List.of(first, second));

        CsvExporter.write(
                path,
                WeatherReadingRecord.class,
                List.of(first));

        String csv =
                Files.readString(
                        path,
                        StandardCharsets.UTF_8);

        assertTrue(csv.contains("2026-09-01"));
        assertTrue(!csv.contains("2026-09-02"));
    }
}