package ua.lpnu.kzp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class CsvEscapingRoundTripTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    void preservesCommaInsideTextField()
            throws Exception {

        Path path =
                temporaryDirectory.resolve(
                        "comma.csv");

        TextRecord original =
                new TextRecord(
                        1,
                        "Львів, Україна");

        CsvExporter.write(
                path,
                TextRecord.class,
                List.of(original));

        String csv =
                Files.readString(
                        path,
                        StandardCharsets.UTF_8);

        List<List<String>> rows =
                CsvParser.parseDocument(csv);

        assertEquals(2, rows.size());
        assertEquals(
                "Львів, Україна",
                rows.get(1).get(1));
    }

    @Test
    void preservesQuotesInsideTextField()
            throws Exception {

        Path path =
                temporaryDirectory.resolve(
                        "quotes.csv");

        TextRecord original =
                new TextRecord(
                        1,
                        "станція \"Північ\"");

        CsvExporter.write(
                path,
                TextRecord.class,
                List.of(original));

        String csv =
                Files.readString(
                        path,
                        StandardCharsets.UTF_8);

        List<List<String>> rows =
                CsvParser.parseDocument(csv);

        assertEquals(
                "станція \"Північ\"",
                rows.get(1).get(1));
    }

    @Test
    void preservesMultilineTextField()
            throws Exception {

        Path path =
                temporaryDirectory.resolve(
                        "multiline.csv");

        TextRecord original =
                new TextRecord(
                        1,
                        "перший рядок\nдругий рядок");

        CsvExporter.write(
                path,
                TextRecord.class,
                List.of(original));

        String csv =
                Files.readString(
                        path,
                        StandardCharsets.UTF_8);

        List<List<String>> rows =
                CsvParser.parseDocument(csv);

        assertEquals(2, rows.size());

        assertEquals(
                "перший рядок\nдругий рядок",
                rows.get(1).get(1));
    }

    @Test
    void rejectsClassWithoutCsvColumns() {

        Path path =
                temporaryDirectory.resolve(
                        "invalid.csv");

        assertThrows(
                DataStorageException.class,
                () -> CsvExporter.write(
                        path,
                        PlainRecord.class,
                        List.of(
                                new PlainRecord("test"))));
    }

    private static final class TextRecord {

        @CsvColumn("id")
        private final int id;

        @CsvColumn("текст")
        private final String text;

        private TextRecord(
                int id,
                String text) {

            this.id = id;
            this.text = text;
        }
    }

    private static final class PlainRecord {

        private final String value;

        private PlainRecord(String value) {
            this.value = value;
        }
    }
}