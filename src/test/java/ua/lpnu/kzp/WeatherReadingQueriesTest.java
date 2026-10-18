package ua.lpnu.kzp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import java.util.DoubleSummaryStatistics;
import java.util.Optional;

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
        WeatherReadingQueries.averageTemperatureByMonthAndKind(readings);

        assertEquals(original, readings);
    }

    @Test
    void calculatesTemperatureStatistics() {
        List<WeatherReading> readings = List.of(
                new DailyReading(
                        "2026-09-01", 10.0, 60.0, 1012.0, 5.0),
                new DailyReading(
                        "2026-09-02", 20.0, 65.0, 1010.0, 7.0),
                new StormReading(
                        "2026-09-03", 5.0, 70.0, 1005.0, 15.0));

        DoubleSummaryStatistics statistics =
                WeatherReadingQueries.temperatureStatistics(readings);

        assertEquals(3, statistics.getCount());
        assertEquals(5.0, statistics.getMin(), 0.0001);
        assertEquals(20.0, statistics.getMax(), 0.0001);
        assertEquals(35.0, statistics.getSum(), 0.0001);
        assertEquals(35.0 / 3.0, statistics.getAverage(), 0.0001);
    }

    @Test
    void returnsEmptyTemperatureStatisticsForEmptyInput() {
        DoubleSummaryStatistics statistics =
                WeatherReadingQueries.temperatureStatistics(List.of());

        assertEquals(0, statistics.getCount());
        assertEquals(0.0, statistics.getSum(), 0.0001);
        assertEquals(0.0, statistics.getAverage(), 0.0001);
        assertEquals(
                Double.POSITIVE_INFINITY,
                statistics.getMin());
        assertEquals(
                Double.NEGATIVE_INFINITY,
                statistics.getMax());
    }

    @Test
    void returnsTopFiveByTemperatureAndDate() {
        List<WeatherReading> readings = List.of(
                new DailyReading(
                        "2026-09-01", 20.0, 60.0, 1012.0, 5.0),
                new DailyReading(
                        "2026-09-04", 25.0, 61.0, 1011.0, 6.0),
                new DailyReading(
                        "2026-09-06", 30.0, 62.0, 1010.0, 7.0),
                new DailyReading(
                        "2026-09-02", 25.0, 63.0, 1009.0, 8.0),
                new DailyReading(
                        "2026-09-03", 15.0, 64.0, 1008.0, 9.0),
                new DailyReading(
                        "2026-09-05", 10.0, 65.0, 1007.0, 10.0));

        List<WeatherReading> result =
                WeatherReadingQueries.topByTemperature(readings, 5);

        assertEquals(5, result.size());

        assertEquals("2026-09-06", result.get(0).getDate());
        assertEquals("2026-09-02", result.get(1).getDate());
        assertEquals("2026-09-04", result.get(2).getDate());
        assertEquals("2026-09-01", result.get(3).getDate());
        assertEquals("2026-09-03", result.get(4).getDate());
    }

    @Test
    void topWithZeroLimitReturnsEmptyList() {
        List<WeatherReading> readings = List.of(
                new DailyReading(
                        "2026-09-01", 20.0, 60.0, 1012.0, 5.0));

        assertTrue(
                WeatherReadingQueries
                        .topByTemperature(readings, 0)
                        .isEmpty());
    }

    @Test
    void topWithLimitGreaterThanSizeReturnsAllReadings() {
        List<WeatherReading> readings = List.of(
                new DailyReading(
                        "2026-09-01", 10.0, 60.0, 1012.0, 5.0),
                new DailyReading(
                        "2026-09-02", 30.0, 65.0, 1010.0, 7.0),
                new DailyReading(
                        "2026-09-03", 20.0, 70.0, 1008.0, 9.0));

        List<WeatherReading> result =
                WeatherReadingQueries.topByTemperature(readings, 5);

        assertEquals(3, result.size());
        assertEquals(30.0, result.get(0).getTemperature(), 0.0001);
        assertEquals(20.0, result.get(1).getTemperature(), 0.0001);
        assertEquals(10.0, result.get(2).getTemperature(), 0.0001);
    }

    @Test
    void findsReadingByDate() {
        List<WeatherReading> readings = List.of(
                new DailyReading(
                        "2026-09-01", 18.0, 60.0, 1012.0, 5.0),
                new StormReading(
                        "2026-09-02", 12.0, 70.0, 1005.0, 16.0));

        Optional<WeatherReading> result =
                WeatherReadingQueries.findByDate(
                        readings,
                        "2026-09-02");

        assertTrue(result.isPresent());
        assertEquals("2026-09-02", result.get().getDate());
    }

    @Test
    void findByDateReturnsEmptyWhenDateIsMissing() {
        List<WeatherReading> readings = List.of(
                new DailyReading(
                        "2026-09-01", 18.0, 60.0, 1012.0, 5.0));

        Optional<WeatherReading> result =
                WeatherReadingQueries.findByDate(
                        readings,
                        "2026-09-10");

        assertTrue(result.isEmpty());
    }

    @Test
    void findByDateReturnsFirstDuplicate() {
        WeatherReading first =
                new DailyReading(
                        "2026-09-01", 10.0, 60.0, 1012.0, 5.0);

        WeatherReading second =
                new DailyReading(
                        "2026-09-01", 20.0, 65.0, 1010.0, 7.0);

        List<WeatherReading> readings =
                List.of(first, second);

        Optional<WeatherReading> result =
                WeatherReadingQueries.findByDate(
                        readings,
                        "2026-09-01");

        assertTrue(result.isPresent());
        assertEquals(first, result.get());
    }

    @Test
    void groupsTemperatureByMonthAndWeatherKind() {
        List<WeatherReading> readings = List.of(
                new DailyReading(
                        "2026-09-01", 10.0, 60.0, 1012.0, 5.0),
                new DailyReading(
                        "2026-09-10", 20.0, 65.0, 1010.0, 7.0),
                new StormReading(
                        "2026-09-15", 5.0, 70.0, 1005.0, 15.0),
                new StormReading(
                        "2026-09-20", 15.0, 75.0, 1000.0, 16.0),
                new DailyReading(
                        "2026-10-01", 8.0, 55.0, 1015.0, 4.0));

        Map<String, Map<WeatherKind, Double>> result =
                WeatherReadingQueries
                        .averageTemperatureByMonthAndKind(readings);

        assertEquals(2, result.size());

        assertEquals(
                15.0,
                result.get("2026-09").get(WeatherKind.DAILY),
                0.0001);

        assertEquals(
                10.0,
                result.get("2026-09").get(WeatherKind.STORM),
                0.0001);

        assertEquals(
                8.0,
                result.get("2026-10").get(WeatherKind.DAILY),
                0.0001);
    }

    @Test
    void keepsDailyAndStormAsSeparateGroups() {
        List<WeatherReading> readings = List.of(
                new DailyReading(
                        "2026-09-01", 18.0, 60.0, 1012.0, 5.0),
                new StormReading(
                        "2026-09-02", 12.0, 70.0, 1005.0, 16.0));

        Map<String, Map<WeatherKind, Double>> result =
                WeatherReadingQueries
                        .averageTemperatureByMonthAndKind(readings);

        Map<WeatherKind, Double> september =
                result.get("2026-09");

        assertEquals(2, september.size());
        assertEquals(18.0, september.get(WeatherKind.DAILY), 0.0001);
        assertEquals(12.0, september.get(WeatherKind.STORM), 0.0001);
    }

    @Test
    void twoLevelGroupingReturnsEmptyMapForEmptyInput() {
        Map<String, Map<WeatherKind, Double>> result =
                WeatherReadingQueries
                        .averageTemperatureByMonthAndKind(List.of());

        assertTrue(result.isEmpty());
    }
}