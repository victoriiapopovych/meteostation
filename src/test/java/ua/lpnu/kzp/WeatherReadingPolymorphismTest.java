package ua.lpnu.kzp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.Test;

class WeatherReadingPolymorphismTest {

    @Test
    void dailyReadingHasDailyKindAndCalculatesDangerIndex() {
        WeatherReading reading = new DailyReading(
                "2026-10-01",
                18.0,
                60.0,
                1013.0,
                3.0);

        assertEquals(WeatherKind.DAILY, reading.getKind());
        assertEquals(6.0, reading.dangerIndex(), 0.0001);
    }

    @Test
    void stormReadingHasStormKindAndCalculatesDangerIndex() {
        WeatherReading reading = new StormReading(
                "2026-10-02",
                12.0,
                60.0,
                995.0,
                15.0);

        assertEquals(WeatherKind.STORM, reading.getKind());
        assertEquals(36.0, reading.dangerIndex(), 0.0001);
    }

    @Test
    void fromCsvCreatesDailyReadingForNormalWind() {
        WeatherReading reading = WeatherReading.fromCsv(
                "2026-10-01;18.0;60.0;1013.0;3.0");

        assertInstanceOf(DailyReading.class, reading);
        assertEquals(WeatherKind.DAILY, reading.getKind());
    }

    @Test
    void fromCsvCreatesStormReadingForStrongWind() {
        WeatherReading reading = WeatherReading.fromCsv(
                "2026-10-02;12.0;60.0;995.0;15.0");

        assertInstanceOf(StormReading.class, reading);
        assertEquals(WeatherKind.STORM, reading.getKind());
    }

    @Test
    void dailyReadingRejectsStormWind() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new DailyReading(
                        "2026-10-01",
                        18.0,
                        60.0,
                        1013.0,
                        15.0));
    }

    @Test
    void stormReadingRejectsNonStormWind() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new StormReading(
                        "2026-10-02",
                        12.0,
                        60.0,
                        995.0,
                        14.9));
    }

    @Test
    void polymorphicCollectionUsesSubtypeImplementations() {
        List<WeatherReading> readings = List.of(
                new DailyReading(
                        "2026-10-01",
                        18.0,
                        60.0,
                        1013.0,
                        3.0),
                new StormReading(
                        "2026-10-02",
                        12.0,
                        60.0,
                        995.0,
                        15.0));

        assertEquals(6.0, readings.get(0).dangerIndex(), 0.0001);
        assertEquals(36.0, readings.get(1).dangerIndex(), 0.0001);
    }

    @Test
    void weatherKindProvidesLabels() {
        assertEquals("добове", WeatherKind.DAILY.label());
        assertEquals("штормове", WeatherKind.STORM.label());
    }

    @Test
    void dailyReadingHasSubtypeDescription() {
        WeatherReading reading = new DailyReading(
                "2026-10-01",
                18.0,
                60.0,
                1013.0,
                3.0);

        assertEquals(
                "Добове спостереження, вітер 3.00 м/с",
                reading.subtypeDescription());
    }

    @Test
    void stormReadingHasSubtypeDescription() {
        WeatherReading reading = new StormReading(
                "2026-10-02",
                12.0,
                60.0,
                995.0,
                15.0);

        assertEquals(
                "Штормове спостереження, вітер 15.00 м/с",
                reading.subtypeDescription());
    }
}