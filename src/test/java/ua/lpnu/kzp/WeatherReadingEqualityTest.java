package ua.lpnu.kzp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class WeatherReadingEqualityTest {

    @Test
    void equalDailyReadingsAreEqual() {
        WeatherReading first = new DailyReading(
                "2026-10-01",
                18.0,
                60.0,
                1013.0,
                3.0);

        WeatherReading second = new DailyReading(
                "2026-10-01",
                18.0,
                60.0,
                1013.0,
                3.0);

        assertEquals(first, second);
    }

    @Test
    void equalReadingsHaveSameHashCode() {
        WeatherReading first = new DailyReading(
                "2026-10-01",
                18.0,
                60.0,
                1013.0,
                3.0);

        WeatherReading second = new DailyReading(
                "2026-10-01",
                18.0,
                60.0,
                1013.0,
                3.0);

        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void hashSetDoesNotDuplicateEqualReadings() {
        Set<WeatherReading> readings = new HashSet<>();

        readings.add(new DailyReading(
                "2026-10-01",
                18.0,
                60.0,
                1013.0,
                3.0));

        readings.add(new DailyReading(
                "2026-10-01",
                18.0,
                60.0,
                1013.0,
                3.0));

        assertEquals(1, readings.size());
    }

    @Test
    void differentReadingsAreNotEqual() {
        WeatherReading first = new DailyReading(
                "2026-10-01",
                18.0,
                60.0,
                1013.0,
                3.0);

        WeatherReading second = new DailyReading(
                "2026-10-02",
                18.0,
                60.0,
                1013.0,
                3.0);

        assertNotEquals(first, second);
    }

    @Test
    void differentSubtypesAreNotEqual() {
        WeatherReading daily = new DailyReading(
                "2026-10-01",
                18.0,
                60.0,
                1013.0,
                3.0);

        WeatherReading storm = new StormReading(
                "2026-10-01",
                18.0,
                60.0,
                1013.0,
                15.0);

        assertNotEquals(daily, storm);
    }
}