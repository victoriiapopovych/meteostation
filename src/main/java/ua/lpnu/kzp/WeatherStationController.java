package ua.lpnu.kzp;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

/**
 * Керує основним процесом роботи програми метеостанції.
 */
public final class WeatherStationController {

    /**
     * Запускає програму з переданими аргументами командного рядка.
     *
     * @param args аргументи командного рядка
     */
    public void run(String[] args) {
        final AppArguments arguments;

        try {
            arguments = ArgumentsParser.parse(args);
        } catch (IllegalArgumentException exception) {
            System.err.printf(
                    "Помилка: %s%n",
                    exception.getMessage());

            if (exception.getMessage().startsWith("невідомий аргумент")) {
                printHelp();
            }

            return;
        }

        if (arguments.help()) {
            printHelp();
            return;
        }

        if (arguments.version()) {
            System.out.println(ProjectVersion.get());
            return;
        }

        processFile(
                arguments.inputPath(),
                arguments.outputPath());
    }

    private void processFile(
            Path input,
            Path output) {

        final List<String> lines;

        try {
            lines = Files.readAllLines(
                    input,
                    StandardCharsets.UTF_8);
        } catch (IOException exception) {
            System.err.printf(
                    "Не вдалося прочитати файл \"%s\": %s%n",
                    input,
                    exception.getMessage());
            return;
        }

        Repository<WeatherReading> repository =
            new Repository<>();

        for (int index = 0;
                index < lines.size();
                index++) {

            String line = lines.get(index);
            int lineNumber = index + 1;

            if (line.isBlank()) {
                printSkippedLine(
                        lineNumber,
                        "порожній рядок");
                continue;
            }

            try {
                WeatherReading reading =
                        WeatherReading.fromCsv(line);

                repository.add(reading);

            } catch (IllegalArgumentException exception) {
                printSkippedLine(
                        lineNumber,
                        exception.getMessage());
            }
        }
        List<WeatherReading> readings =
            repository.all();

        Path csvOutput =
                output.resolveSibling(
                        "weather-readings.csv");

        try {
            WeatherReadingCsvStorage.write(
                    csvOutput,
                    readings);
        } catch (DataStorageException exception) {
            System.err.printf(
                    "Не вдалося зберегти CSV \"%s\": %s%n",
                    csvOutput,
                    exception.getMessage());
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

        double minTemperature =
                Double.POSITIVE_INFINITY;

        double totalHumidity = 0.0;

        double maxWind =
                Double.NEGATIVE_INFINITY;

        for (WeatherReading reading : readings) {
            TemperatureHumidity values =
                    reading.temperatureHumidity();

            minTemperature = Math.min(
                    minTemperature,
                    values.temperature());

            totalHumidity += values.humidity();

            maxWind = Math.max(
                    maxWind,
                    reading.getWind());
        }

        double averageHumidity =
                totalHumidity / validCount;

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

    private void writeReport(
            Path output,
            String report) {

        try {
            Path parent = output.getParent();

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

    private void printSkippedLine(
            int lineNumber,
            String reason) {

        System.err.printf(
                "Пропущено рядок %d: %s%n",
                lineNumber,
                reason);
    }

    private void printHelp() {
        System.out.printf(
                "Метеостанція — варіант 18%n"
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