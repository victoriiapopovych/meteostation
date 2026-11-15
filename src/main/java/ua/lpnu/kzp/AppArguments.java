package ua.lpnu.kzp;

import java.nio.file.Path;
import java.util.Objects;

/**
 * Зберігає параметри запуску програми.
 *
 * @param inputPath шлях до вхідного CSV-файла
 * @param outputPath шлях до файла звіту
 * @param help чи потрібно показати довідку
 * @param version чи потрібно показати версію
 */
public record AppArguments(
        Path inputPath,
        Path outputPath,
        boolean help,
        boolean version) {

    /**
     * Створює параметри запуску та перевіряє обов'язкові шляхи.
     */
    public AppArguments {
        Objects.requireNonNull(
                inputPath,
                "Шлях до вхідного файла не може бути null");

        Objects.requireNonNull(
                outputPath,
                "Шлях до файла звіту не може бути null");
    }
}