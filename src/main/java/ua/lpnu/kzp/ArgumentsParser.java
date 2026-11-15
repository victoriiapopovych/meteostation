package ua.lpnu.kzp;

import java.nio.file.Path;
import java.util.Objects;

/**
 * Розбирає аргументи командного рядка програми.
 */
public final class ArgumentsParser {

    private static final Path DEFAULT_INPUT =
            Path.of("data", "input.csv");

    private static final Path DEFAULT_OUTPUT =
            Path.of("out", "report.txt");

    private ArgumentsParser() {
    }

    /**
     * Перетворює аргументи командного рядка на параметри запуску.
     *
     * @param args аргументи командного рядка
     * @return параметри запуску
     * @throws IllegalArgumentException якщо аргументи некоректні
     */
    public static AppArguments parse(String[] args) {
        Objects.requireNonNull(
                args,
                "Аргументи не можуть бути null");

        Path input = DEFAULT_INPUT;
        Path output = DEFAULT_OUTPUT;

        for (int i = 0; i < args.length; i++) {
            String argument = args[i];

            switch (argument) {
                case "--help" -> {
                    return new AppArguments(
                            input,
                            output,
                            true,
                            false);
                }

                case "--version" -> {
                    return new AppArguments(
                            input,
                            output,
                            false,
                            true);
                }

                case "--input" -> {
                    i = requireValueIndex(
                            args,
                            i,
                            "--input");

                    input = Path.of(args[i]);
                }

                case "--output" -> {
                    i = requireValueIndex(
                            args,
                            i,
                            "--output");

                    output = Path.of(args[i]);
                }

                default -> throw new IllegalArgumentException(
                        "невідомий аргумент \""
                                + argument
                                + "\".");
            }
        }

        return new AppArguments(
                input,
                output,
                false,
                false);
    }

    private static int requireValueIndex(
            String[] args,
            int optionIndex,
            String option) {

        int valueIndex = optionIndex + 1;

        if (valueIndex >= args.length
                || args[valueIndex].startsWith("--")) {
            throw new IllegalArgumentException(
                    "після "
                            + option
                            + " потрібно вказати шлях.");
        }

        return valueIndex;
    }
}