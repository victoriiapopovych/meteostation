package ua.lpnu.kzp;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.DoubleSummaryStatistics;
import java.util.Comparator;
import java.util.Optional;

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

    /**
     * Обчислює зведену статистику температури.
     *
     * @param readings погодні спостереження
     * @return статистика температури
     */
    public static DoubleSummaryStatistics temperatureStatistics(
            List<WeatherReading> readings) {

        Objects.requireNonNull(
                readings,
                "Список спостережень не може бути null");

        return readings.stream()
                .collect(Collectors.summarizingDouble(
                        WeatherReading::getTemperature));
    }

    /**
     * Повертає перші N погодних спостережень,
     * відсортованих за температурою за спаданням,
     * а при однаковій температурі — за датою за зростанням.
     *
     * @param readings погодні спостереження
     * @param limit максимальна кількість результатів
     * @return впорядкований список спостережень
     */
    public static List<WeatherReading> topByTemperature(
            List<WeatherReading> readings,
            int limit) {

        Objects.requireNonNull(
                readings,
                "Список спостережень не може бути null");

        if (limit < 0) {
            throw new IllegalArgumentException(
                    "Кількість результатів не може бути від'ємною");
        }

        Comparator<WeatherReading> comparator =
                Comparator.comparingDouble(
                        WeatherReading::getTemperature)
                        .reversed()
                        .thenComparing(WeatherReading::getDate);

        return readings.stream()
                .sorted(comparator)
                .limit(limit)
                .toList();
    }

    /**
     * Повертає перше погодне спостереження із заданою датою.
     *
     * @param readings погодні спостереження
     * @param date дата пошуку
     * @return перше знайдене спостереження або Optional.empty()
     */
    public static Optional<WeatherReading> findByDate(
            List<WeatherReading> readings,
            String date) {

        Objects.requireNonNull(
                readings,
                "Список спостережень не може бути null");

        Objects.requireNonNull(
                date,
                "Дата пошуку не може бути null");

        return readings.stream()
                .filter(reading -> reading.getDate().equals(date))
                .findFirst();
    }

    /**
     * Групує погодні спостереження за місяцем і типом погоди
     * та обчислює середню температуру для кожної групи.
     *
     * @param readings погодні спостереження
     * @return місяць -> тип погоди -> середня температура
     */
    public static Map<String, Map<WeatherKind, Double>>
            averageTemperatureByMonthAndKind(
                    List<WeatherReading> readings) {

        Objects.requireNonNull(
                readings,
                "Список спостережень не може бути null");

        return readings.stream()
                .collect(Collectors.groupingBy(
                        reading -> reading.getDate().substring(0, 7),
                        TreeMap::new,
                        Collectors.groupingBy(
                                WeatherReading::getKind,
                                Collectors.averagingDouble(
                                        WeatherReading::getTemperature))));
    }
}