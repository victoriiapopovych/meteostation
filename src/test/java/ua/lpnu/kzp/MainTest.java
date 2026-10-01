package ua.lpnu.kzp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

class MainTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    void correctLineProducesExpectedReport() throws IOException {
        String report = runProgram(
                "2026-09-01;18.5;65.0;1013.2;3.4");

        assertTrue(report.contains("Коректних записів: 1"));
        assertTrue(report.contains("Мінімальна температура: 18.50"));
        assertTrue(report.contains("Середня вологість: 65.00"));
        assertTrue(report.contains("Найбільша швидкість вітру: 3.40"));
    }

    @Test
    void blankLineIsSkipped() throws IOException {
        String report = runProgram(
                "",
                "2026-09-01;18.5;65.0;1013.2;3.4");

        assertTrue(report.contains("Коректних записів: 1"));
    }

    @Test
    void wrongFieldCountIsSkipped() throws IOException {
        String report = runProgram(
                "2026-09-01;18.5;65.0",
                "2026-09-02;17.0;70.0;1010.0;4.0");

        assertTrue(report.contains("Коректних записів: 1"));
        assertTrue(report.contains("Мінімальна температура: 17.00"));
    }

    @Test
    void nonNumericFieldIsSkipped() throws IOException {
        String report = runProgram(
                "2026-09-01;помилка;65.0;1013.2;3.4",
                "2026-09-02;17.0;70.0;1010.0;4.0");

        assertTrue(report.contains("Коректних записів: 1"));
        assertTrue(report.contains("Середня вологість: 70.00"));
    }

    @Test
    void negativeValueWhereNotAllowedIsSkipped() throws IOException {
        String report = runProgram(
                "2026-09-01;-20.0;-5.0;1013.2;20.0",
                "2026-09-02;10.0;60.0;1010.0;2.0");

        assertTrue(report.contains("Коректних записів: 1"));
        assertTrue(report.contains("Мінімальна температура: 10.00"));
        assertTrue(report.contains("Середня вологість: 60.00"));
        assertTrue(report.contains("Найбільша швидкість вітру: 2.00"));
    }

    @Test
    void fileWithoutValidRecordsReportsNoStatistics() throws IOException {
        String report = runProgram(
                "2026-09-01;помилка;65.0;1013.2;3.4",
                "2026-09-02;18.0;70.0");

        assertTrue(report.contains("Коректних записів: 0"));
        assertTrue(report.contains(
                "Показники не обчислено: немає коректних записів."));
    }

    @Test
    void averageHumidityIsCorrectForSeveralRecords() throws IOException {
        String report = runProgram(
                "2026-09-01;10.0;60.0;1010.0;2.0",
                "2026-09-02;15.0;70.0;1012.0;4.0",
                "2026-09-03;20.0;80.0;1014.0;6.0");

        assertTrue(report.contains("Коректних записів: 3"));
        assertTrue(report.contains("Середня вологість: 70.00"));
    }

    @Test
    void reportPreservesUkrainianTextInUtf8() throws IOException {
        Path input = temporaryDirectory.resolve("вхід.csv");
        Path output = temporaryDirectory.resolve("результат").resolve("звіт.txt");

        Files.write(
                input,
                List.of("2026-09-01;18.5;65.0;1013.2;3.4"),
                StandardCharsets.UTF_8);

        Main.main(new String[] {
            "--input", input.toString(),
            "--output", output.toString()
        });

        String report = Files.readString(output, StandardCharsets.UTF_8);

        assertTrue(report.contains("Метеостанція — варіант 18"));
        assertTrue(report.contains("Коректних записів"));
        assertTrue(report.contains("Середня вологість"));
        assertEquals(report, new String(Files.readAllBytes(output), StandardCharsets.UTF_8));
    }

    @Test
    void versionArgumentPrintsVersion() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;

        try {
            System.setOut(new PrintStream(
                    output,
                    true,
                    StandardCharsets.UTF_8));

            Main.main(new String[]{"--version"});

        } finally {
            System.setOut(originalOut);
        }

        assertEquals(
                "2.0.0" + System.lineSeparator(),
                output.toString(StandardCharsets.UTF_8));
    }

    private String runProgram(String... lines) throws IOException {
        Path input = temporaryDirectory.resolve("input.csv");
        Path output = temporaryDirectory.resolve("out").resolve("report.txt");

        Files.write(input, List.of(lines), StandardCharsets.UTF_8);

        Main.main(new String[] {
            "--input", input.toString(),
            "--output", output.toString()
        });

        return Files.readString(output, StandardCharsets.UTF_8);
    }
}
