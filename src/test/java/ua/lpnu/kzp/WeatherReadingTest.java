package ua.lpnu.kzp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class WeatherReadingTest {

    @Test
    void constructorCreatesValidReading() {
        WeatherReading reading =
                new WeatherReading("2026-09-01", 18.5, 62.0, 1013.2, 3.4);

        assertEquals("2026-09-01", reading.getDate());
        assertEquals(18.5, reading.getTemperature(), 0.0001);
        assertEquals(62.0, reading.getHumidity(), 0.0001);
        assertEquals(1013.2, reading.getPressure(), 0.0001);
        assertEquals(3.4, reading.getWind(), 0.0001);
    }

    @Test
    void constructorRejectsNullDate() {
        assertThrows(
                NullPointerException.class,
                () -> new WeatherReading(null, 18.5, 62.0, 1013.2, 3.4));
    }

    @Test
    void constructorRejectsBlankDate() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new WeatherReading(" ", 18.5, 62.0, 1013.2, 3.4));
    }

    @Test
    void constructorRejectsInvalidHumidity() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new WeatherReading(
                        "2026-09-01", 18.5, 101.0, 1013.2, 3.4));
    }

    @Test
    void constructorAcceptsBoundaryValues() {
        WeatherReading reading =
                new WeatherReading("2026-09-01", -273.15, 100.0, 0.0, 0.0);

        assertEquals(-273.15, reading.getTemperature(), 0.0001);
        assertEquals(100.0, reading.getHumidity(), 0.0001);
        assertEquals(0.0, reading.getPressure(), 0.0001);
        assertEquals(0.0, reading.getWind(), 0.0001);
    }

    @Test
    void fromCsvCreatesValidReading() {
        WeatherReading reading =
                WeatherReading.fromCsv(
                        "2026-09-01;18.5;62.0;1013.2;3.4");

        assertEquals("2026-09-01", reading.getDate());
        assertEquals(18.5, reading.getTemperature(), 0.0001);
        assertEquals(62.0, reading.getHumidity(), 0.0001);
    }

    @Test
    void fromCsvRejectsWrongFieldCount() {
        assertThrows(
                IllegalArgumentException.class,
                () -> WeatherReading.fromCsv(
                        "2026-09-01;18.5;62.0;1013.2"));
    }

    @Test
    void fromCsvRejectsInvalidNumber() {
        assertThrows(
                IllegalArgumentException.class,
                () -> WeatherReading.fromCsv(
                        "2026-09-01;помилка;62.0;1013.2;3.4"));
    }

    @Test
    void recordUsesValueEqualityAndComponents() {
        TemperatureHumidity first =
                new TemperatureHumidity(18.5, 62.0);

        TemperatureHumidity second =
                new TemperatureHumidity(18.5, 62.0);

        assertEquals(first, second);
        assertEquals(18.5, first.temperature(), 0.0001);
        assertEquals(62.0, first.humidity(), 0.0001);
    }

    @Test
    void recordRejectsInvalidHumidity() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new TemperatureHumidity(18.5, 120.0));
    }
}