package ua.lpnu.kzp;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ReflectionCacheBenchmarkTest {

    private static final int RECORD_COUNT = 5000;
    private static final int ITERATIONS = 10;

    @TempDir
    Path temporaryDirectory;

    @Test
    void comparesExportTimeWithAndWithoutFieldCache()
            throws Exception {

        List<WeatherReadingRecord> records =
                createRecords();

        Path uncachedPath =
                temporaryDirectory.resolve(
                        "uncached.csv");

        Path cachedPath =
                temporaryDirectory.resolve(
                        "cached.csv");

        // Прогрів JVM перед вимірюванням.
        CsvExporter.writeWithoutCache(
                uncachedPath,
                WeatherReadingRecord.class,
                records);

        AnnotatedFieldResolver.clearCache();

        CsvExporter.write(
                cachedPath,
                WeatherReadingRecord.class,
                records);

        long uncachedStart =
                System.nanoTime();

        for (int index = 0;
                index < ITERATIONS;
                index++) {

            CsvExporter.writeWithoutCache(
                    uncachedPath,
                    WeatherReadingRecord.class,
                    records);
        }

        long uncachedNanoseconds =
                System.nanoTime()
                        - uncachedStart;

        AnnotatedFieldResolver.clearCache();

        // Перший resolve заповнює кеш.
        CsvExporter.write(
                cachedPath,
                WeatherReadingRecord.class,
                records);

        long cachedStart =
                System.nanoTime();

        for (int index = 0;
                index < ITERATIONS;
                index++) {

            CsvExporter.write(
                    cachedPath,
                    WeatherReadingRecord.class,
                    records);
        }

        long cachedNanoseconds =
                System.nanoTime()
                        - cachedStart;

        double uncachedMilliseconds =
                uncachedNanoseconds
                        / 1_000_000.0;

        double cachedMilliseconds =
                cachedNanoseconds
                        / 1_000_000.0;

        System.out.printf(
                "Reflection benchmark:%n"
                        + "Records: %d%n"
                        + "Iterations: %d%n"
                        + "Without cache: %.3f ms%n"
                        + "With cache: %.3f ms%n",
                RECORD_COUNT,
                ITERATIONS,
                uncachedMilliseconds,
                cachedMilliseconds);

        /*
         * Не перевіряємо cached < uncached:
         * результат одного запуску залежить від JVM,
         * файлової системи та навантаження ОС.
         */
        assertTrue(uncachedNanoseconds > 0);
        assertTrue(cachedNanoseconds > 0);
    }

    private static List<WeatherReadingRecord> createRecords() {

        List<WeatherReadingRecord> records =
                new ArrayList<>();

        LocalDate start =
                LocalDate.of(
                        2026,
                        1,
                        1);

        for (int index = 0;
                index < RECORD_COUNT;
                index++) {

            records.add(
                    new WeatherReadingRecord(
                            start.plusDays(index),
                            15.0 + index % 20,
                            40.0 + index % 50,
                            1000.0 + index % 30,
                            1.0 + index % 10));
        }

        return List.copyOf(records);
    }
}