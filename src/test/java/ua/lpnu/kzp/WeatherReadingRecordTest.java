package ua.lpnu.kzp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

class WeatherReadingRecordTest {

    @Test
    void createsValidRecord() {
        WeatherReadingRecord record =
                new WeatherReadingRecord(
                        LocalDate.of(2026, 9, 1),
                        18.5,
                        62.0,
                        1013.2,
                        3.4);

        assertEquals(
                LocalDate.of(2026, 9, 1),
                record.date());

        assertEquals(18.5, record.temperature());
        assertEquals(62.0, record.humidity());
        assertEquals(1013.2, record.pressure());
        assertEquals(3.4, record.wind());
    }

    @Test
    void acceptsBoundaryValues() {
        WeatherReadingRecord record =
                new WeatherReadingRecord(
                        LocalDate.of(2026, 9, 1),
                        -273.15,
                        100.0,
                        0.0,
                        0.0);

        assertEquals(-273.15, record.temperature());
        assertEquals(100.0, record.humidity());
        assertEquals(0.0, record.pressure());
        assertEquals(0.0, record.wind());
    }

    @Test
    void rejectsNullDate() {
        assertThrows(
                NullPointerException.class,
                () -> new WeatherReadingRecord(
                        null,
                        18.5,
                        62.0,
                        1013.2,
                        3.4));
    }

    @Test
    void rejectsInvalidHumidity() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new WeatherReadingRecord(
                        LocalDate.of(2026, 9, 1),
                        18.5,
                        120.0,
                        1013.2,
                        3.4));
    }

    @Test
    void equalityUsesAllFields() {
        WeatherReadingRecord first =
                new WeatherReadingRecord(
                        LocalDate.of(2026, 9, 1),
                        18.5,
                        62.0,
                        1013.2,
                        3.4);

        WeatherReadingRecord same =
                new WeatherReadingRecord(
                        LocalDate.of(2026, 9, 1),
                        18.5,
                        62.0,
                        1013.2,
                        3.4);

        WeatherReadingRecord different =
                new WeatherReadingRecord(
                        LocalDate.of(2026, 9, 2),
                        18.5,
                        62.0,
                        1013.2,
                        3.4);

        assertEquals(first, same);
        assertEquals(first.hashCode(), same.hashCode());
        assertNotEquals(first, different);
    }

    @Test
    void createsRecordFromCsv() throws Exception {
        WeatherReadingRecord record =
                WeatherReadingRecord.fromCsv(
                        "2026-09-01,62.0,1013.2,18.5,3.4");

        assertEquals(
                LocalDate.of(2026, 9, 1),
                record.date());

        assertEquals(18.5, record.temperature());
        assertEquals(62.0, record.humidity());
        assertEquals(1013.2, record.pressure());
        assertEquals(3.4, record.wind());
    }

    @Test
    void createsRecordFromFields() throws Exception {
        WeatherReadingRecord record =
                WeatherReadingRecord.fromFields(
                        List.of(
                                "2026-09-01",
                                "62.0",
                                "1013.2",
                                "18.5",
                                "3.4"));

        assertEquals(
                LocalDate.of(2026, 9, 1),
                record.date());
    }

    @Test
    void rejectsWrongFieldCount() {
        assertThrows(
                DataStorageException.class,
                () -> WeatherReadingRecord.fromFields(
                        List.of(
                                "2026-09-01",
                                "62.0")));
    }

    @Test
    void rejectsInvalidNumberWithCause() {
        DataStorageException exception =
                assertThrows(
                        DataStorageException.class,
                        () -> WeatherReadingRecord.fromCsv(
                                "2026-09-01,помилка,1013.2,18.5,3.4"));

        assertNotNull(exception.getCause());
    }

    @Test
    void rejectsInvalidDateWithCause() {
        DataStorageException exception =
                assertThrows(
                        DataStorageException.class,
                        () -> WeatherReadingRecord.fromCsv(
                                "не-дата,62.0,1013.2,18.5,3.4"));

        assertNotNull(exception.getCause());
    }

    @Test
    void validationStillAppliesAfterImport() {
        DataStorageException exception =
                assertThrows(
                        DataStorageException.class,
                        () -> WeatherReadingRecord.fromCsv(
                                "2026-09-01,120.0,1013.2,18.5,3.4"));

        assertNotNull(exception.getCause());
    }
}