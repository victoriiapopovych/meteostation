package ua.lpnu.kzp;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.ArrayList;

/**
 * Лабораторна робота № 2.
 * Варіант 18 — «Метеостанція».
 */
public final class Main {

    private static final Path DEFAULT_INPUT = Path.of("data", "input.csv");
    private static final Path DEFAULT_OUTPUT = Path.of("out", "report.txt");
    private static final String VERSION = "3.0.0";

    private Main() {
    }

    /**
     * Точка входу програми.
     * Обробляє аргументи командного рядка та запускає обробку файла.
     *
     * @param args аргументи командного рядка
     */
    public static void main(String[] args) {
        Path input = DEFAULT_INPUT;
        Path output = DEFAULT_OUTPUT;

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--help" -> {
                    printHelp();
                    return;
                }

                case "--version" -> {
                    System.out.println(VERSION);
                    return;
                }

                case "--input" -> {
                    if (i + 1 >= args.length) {
                        System.err.printf(
                                "Помилка: після --input потрібно вказати шлях.%n");
                        return;
                    }

                    input = Path.of(args[++i]);
                }

                case "--output" -> {
                    if (i + 1 >= args.length) {
                        System.err.printf(
                                "Помилка: після --output потрібно вказати шлях.%n");
                        return;
                    }

                    output = Path.of(args[++i]);
                }

                default -> {
                    System.err.printf(
                            "Помилка: невідомий аргумент \"%s\".%n",
                            args[i]);
                    printHelp();
                    return;
                }
            }
        }

        processFile(input, output);
    }

    /**
     * Читає вхідний файл, перевіряє записи,
     * обчислює статистику та формує звіт.
     *
     * @param input шлях до вхідного файла
     * @param output шлях до файла звіту
     */
    private static void processFile(Path input, Path output) {
        final List<String> lines;

        try {
            lines = Files.readAllLines(input, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            System.err.printf(
                    "Не вдалося прочитати файл \"%s\": %s%n",
                    input,
                    exception.getMessage());
            return;
        }

        List<WeatherReading> readings = new ArrayList<>();

        for (int index = 0; index < lines.size(); index++) {
            String line = lines.get(index);
            int lineNumber = index + 1;

            if (line.isBlank()) {
                printSkippedLine(lineNumber, "порожній рядок");
                continue;
            }

            try {
                WeatherReading reading = WeatherReading.fromCsv(line);
                readings.add(reading);
            } catch (IllegalArgumentException exception) {
                printSkippedLine(
                        lineNumber,
                        exception.getMessage());
            }
        }

        int validCount = readings.size();

        if (validCount == 0) {
            String report = String.format(
                    Locale.ROOT,
                    "Метеостанція — варіант 18%n"
                            + "Коректних записів: 0%n"
                            + "Показники не обчислено: "
                            + "немає коректних записів.%n");

            System.out.print(report);
            writeReport(output, report);
            return;
        }

        double minTemperature = Double.POSITIVE_INFINITY;
        double totalHumidity = 0.0;
        double maxWind = Double.NEGATIVE_INFINITY;

        for (WeatherReading reading : readings) {
            TemperatureHumidity values = reading.temperatureHumidity();

            minTemperature = Math.min(
                    minTemperature,
                    values.temperature());

            totalHumidity += values.humidity();

            maxWind = Math.max(
                    maxWind,
                    reading.getWind());
        }

        double averageHumidity = totalHumidity / validCount;

        String report = String.format(
                Locale.ROOT,
                "Метеостанція — варіант 18%n"
                        + "Коректних записів: %d%n"
                        + "Мінімальна температура: %.2f%n"
                        + "Середня вологість: %.2f%n"
                        + "Найбільша швидкість вітру: %.2f%n",
                        
                validCount,
                minTemperature,
                averageHumidity,
                maxWind);

        System.out.print(report);
        writeReport(output, report);
    }

    /**
     * Записує сформований звіт у UTF-8 файл.
     *
     * @param output шлях до файла звіту
     * @param report текст звіту
     */
    private static void writeReport(Path output, String report) {
        try {
            Path parent = output.getParent();

            // Для "report.txt" батьківського каталогу може не бути.
            if (parent != null) {
                Files.createDirectories(parent);
            }

            Files.writeString(
                    output,
                    report,
                    StandardCharsets.UTF_8);

            System.out.printf(
                    "Звіт записано у: %s%n",
                    output);

        } catch (IOException exception) {
            System.err.printf(
                    "Не вдалося записати звіт \"%s\": %s%n",
                    output,
                    exception.getMessage());
        }
    }

    /**
     * Виводить повідомлення про пропущений некоректний рядок.
     *
     * @param lineNumber номер рядка
     * @param reason причина пропуску
     */
    private static void printSkippedLine(
            int lineNumber,
            String reason) {

        System.err.printf(
                "Пропущено рядок %d: %s%n",
                lineNumber,
                reason);
    }

    /**
     * Виводить довідку про доступні параметри командного рядка.
     */
    private static void printHelp() {
        System.out.printf(
                "Лабораторна робота № 2, варіант 18 — Метеостанція%n"
                        + "%n"
                        + "Використання:%n"
                        + "  java ua.lpnu.kzp.Main [параметри]%n"
                        + "%n"
                        + "Параметри:%n"
                        + "  --help            показати цю довідку%n"
                        + "  --version         показати версію програми%n"
                        + "  --input <файл>    вхідний UTF-8 файл%n"
                        + "  --output <файл>   файл звіту%n"
                        + "%n"
                        + "За замовчуванням:%n"
                        + "  input:  data/input.csv%n"
                        + "  output: out/report.txt%n");
    }
}