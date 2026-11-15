package ua.lpnu.kzp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

class ArgumentsParserTest {

    @Test
    void usesDefaultPathsWithoutArguments() {
        AppArguments arguments =
                ArgumentsParser.parse(new String[]{});

        assertEquals(
                Path.of("data", "input.csv"),
                arguments.inputPath());

        assertEquals(
                Path.of("out", "report.txt"),
                arguments.outputPath());

        assertFalse(arguments.help());
        assertFalse(arguments.version());
    }

    @Test
    void parsesInputAndOutputPaths() {
        AppArguments arguments =
                ArgumentsParser.parse(new String[]{
                        "--input", "data/custom.csv",
                        "--output", "out/custom.txt"
                });

        assertEquals(
                Path.of("data/custom.csv"),
                arguments.inputPath());

        assertEquals(
                Path.of("out/custom.txt"),
                arguments.outputPath());
    }

    @Test
    void parsesHelpArgument() {
        AppArguments arguments =
                ArgumentsParser.parse(
                        new String[]{"--help"});

        assertTrue(arguments.help());
    }

    @Test
    void parsesVersionArgument() {
        AppArguments arguments =
                ArgumentsParser.parse(
                        new String[]{"--version"});

        assertTrue(arguments.version());
    }

    @Test
    void rejectsUnknownArgument() {
        assertThrows(
                IllegalArgumentException.class,
                () -> ArgumentsParser.parse(
                        new String[]{"--unknown"}));
    }

    @Test
    void rejectsMissingOptionValue() {
        assertThrows(
                IllegalArgumentException.class,
                () -> ArgumentsParser.parse(
                        new String[]{"--input"}));
    }
}