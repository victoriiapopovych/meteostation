package ua.lpnu.kzp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

class WeatherReadingQueriesTest {

    @Test
    void filtersReadingsWithWindAboveThreshold() {
        List<WeatherReading> readings = List.of(
                new DailyReading(
                        "2026-09-01", 18.0, 60.0, 1012.0, 5.0),
                new DailyReading(
                        "2026-09-02", 20.0, 65.0, 1010.0, 10.0),
                new StormReading(
                        "2026-09-03", 15.0, 70.0, 1005.0, 15.0));

        List<WeatherReading> result =
                WeatherReadingQueries.filterByWind(readings, 10.0);

        assertEquals(1, result.size());
        assertEquals("2026-09-03", result.get(0).getDate());
    }

    @Test
    void filterExcludesReadingAtThreshold() {
        List<WeatherReading> readings = List.of(
                new DailyReading(
                        "2026-09-01", 18.0, 60.0, 1012.0, 10.0));

        List<WeatherReading> result =
                WeatherReadingQueries.filterByWind(readings, 10.0);

        assertTrue(result.isEmpty());
    }

    @Test
    void mapsReadingDates() {
        List<WeatherReading> readings = List.of(
                new DailyReading(
                        "2026-09-01", 18.0, 60.0, 1012.0, 5.0),
                new StormReading(
                        "2026-09-02", 12.0, 70.0, 1005.0, 16.0));

        List<String> result =
                WeatherReadingQueries.dates(readings);

        assertEquals(
                List.of("2026-09-01", "2026-09-02"),
                result);
    }

    @Test
    void groupsAverageTemperatureByMonth() {
        List<WeatherReading> readings = List.of(
                new DailyReading(
                        "2026-09-01", 10.0, 60.0, 1012.0, 5.0),
                new DailyReading(
                        "2026-09-15", 20.0, 65.0, 1010.0, 7.0),
                new StormReading(
                        "2026-10-01", 5.0, 70.0, 1005.0, 15.0));

        Map<String, Double> result =
                WeatherReadingQueries.averageTemperatureByMonth(readings);

        assertEquals(2, result.size());
        assertEquals(15.0, result.get("2026-09"), 0.0001);
        assertEquals(5.0, result.get("2026-10"), 0.0001);
    }

    @Test
    void returnsEmptyResultsForEmptyInput() {
        List<WeatherReading> readings = List.of();

        assertTrue(
                WeatherReadingQueries
                        .filterByWind(readings, 10.0)
                        .isEmpty());

        assertTrue(
                WeatherReadingQueries
                        .dates(readings)
                        .isEmpty());

        assertTrue(
                WeatherReadingQueries
                        .averageTemperatureByMonth(readings)
                        .isEmpty());
    }

    @Test
    void queriesDoNotModifyInputCollection() {
        List<WeatherReading> readings = new ArrayList<>(List.of(
                new DailyReading(
                        "2026-09-01", 18.0, 60.0, 1012.0, 5.0),
                new StormReading(
                        "2026-09-02", 12.0, 70.0, 1005.0, 16.0)));

        List<WeatherReading> original =
                List.copyOf(readings);

        WeatherReadingQueries.filterByWind(readings, 10.0);
        WeatherReadingQueries.dates(readings);
        WeatherReadingQueries.averageTemperatureByMonth(readings);

        assertEquals(original, readings);
    }
}