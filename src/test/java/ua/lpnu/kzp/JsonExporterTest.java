package ua.lpnu.kzp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class JsonExporterTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    void exportsWeatherReadingThroughExporterInterface()
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
                        "weather.json");

        Exporter<WeatherReadingRecord> exporter =
                new JsonExporter<>();

        exporter.export(
                path,
                WeatherReadingRecord.class,
                List.of(record));

        String json =
                Files.readString(
                        path,
                        StandardCharsets.UTF_8);

        assertTrue(
                json.contains(
                        "\"дата\":\"2026-09-01\""));

        assertTrue(
                json.contains(
                        "\"вологість\":62.0"));

        assertTrue(
                json.contains(
                        "\"тиск\":1013.2"));

        assertTrue(
                json.contains(
                        "\"температура\":18.5"));

        assertTrue(
                json.contains(
                        "\"вітер\":3.4"));
    }

    @Test
    void worksWithAnotherAnnotatedClass()
            throws Exception {

        Path path =
                temporaryDirectory.resolve(
                        "sample.json");

        Exporter<SampleRecord> exporter =
                new JsonExporter<>();

        exporter.export(
                path,
                SampleRecord.class,
                List.of(
                        new SampleRecord(
                                1,
                                "станція \"Північ\"\nЛьвів")));

        String json =
                Files.readString(
                        path,
                        StandardCharsets.UTF_8);

        assertTrue(
                json.contains(
                        "\"id\":1"));

        assertTrue(
                json.contains(
                        "станція \\\"Північ\\\"\\nЛьвів"));
    }

    @Test
    void exportsEmptyListAsEmptyJsonArray()
            throws Exception {

        Path path =
                temporaryDirectory.resolve(
                        "empty.json");

        Exporter<WeatherReadingRecord> exporter =
                new JsonExporter<>();

        exporter.export(
                path,
                WeatherReadingRecord.class,
                List.of());

        String json =
                Files.readString(
                        path,
                        StandardCharsets.UTF_8);

        assertEquals(
                "["
                        + System.lineSeparator()
                        + "]",
                json);
    }

    @Test
    void rejectsClassWithoutAnnotatedFields() {

        Path path =
                temporaryDirectory.resolve(
                        "invalid.json");

        Exporter<PlainRecord> exporter =
                new JsonExporter<>();

        assertThrows(
                DataStorageException.class,
                () -> exporter.export(
                        path,
                        PlainRecord.class,
                        List.of(
                                new PlainRecord("test"))));
    }

    private static final class SampleRecord {

        @CsvColumn("id")
        private final int id;

        @CsvColumn("опис")
        private final String description;

        private SampleRecord(
                int id,
                String description) {

            this.id = id;
            this.description = description;
        }
    }

    private static final class PlainRecord {

        private final String value;

        private PlainRecord(
                String value) {

            this.value = value;
        }
    }
}