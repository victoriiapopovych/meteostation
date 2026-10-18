package ua.lpnu.kzp;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Містить потокові запити для колекції погодних спостережень.
 */
public final class WeatherReadingQueries {

    private WeatherReadingQueries() {
    }

    /**
     * Повертає спостереження, у яких швидкість вітру
     * перевищує заданий поріг.
     *
     * @param readings погодні спостереження
     * @param threshold поріг швидкості вітру
     * @return спостереження з вітром понад поріг
     */
    public static List<WeatherReading> filterByWind(
            List<WeatherReading> readings,
            double threshold) {

        Objects.requireNonNull(
                readings,
                "Список спостережень не може бути null");

        if (!Double.isFinite(threshold)) {
            throw new IllegalArgumentException(
                    "Поріг швидкості вітру має бути скінченним");
        }

        return readings.stream()
                .filter(reading -> reading.getWind() > threshold)
                .toList();
    }

    /**
     * Повертає дати всіх погодних спостережень.
     *
     * @param readings погодні спостереження
     * @return список дат
     */
    public static List<String> dates(
            List<WeatherReading> readings) {

        Objects.requireNonNull(
                readings,
                "Список спостережень не може бути null");

        return readings.stream()
                .map(WeatherReading::getDate)
                .toList();
    }

    /**
     * Обчислює середню температуру для кожного місяця.
     * Місяць представлено у форматі YYYY-MM.
     *
     * @param readings погодні спостереження
     * @return середня температура за місяцями
     */
    public static Map<String, Double> averageTemperatureByMonth(
            List<WeatherReading> readings) {

        Objects.requireNonNull(
                readings,
                "Список спостережень не може бути null");

        return readings.stream()
                .collect(Collectors.groupingBy(
                        reading -> reading.getDate().substring(0, 7),
                        TreeMap::new,
                        Collectors.averagingDouble(
                                WeatherReading::getTemperature)));
    }
}