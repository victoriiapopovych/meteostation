package ua.lpnu.kzp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

class CsvParserTest {

    @Test
    void parsesSimpleCsvRow()
            throws Exception {

        List<String> fields =
                CsvParser.parse(
                        "2026-09-01,62.0,1013.2,18.5,3.4");

        assertEquals(
                List.of(
                        "2026-09-01",
                        "62.0",
                        "1013.2",
                        "18.5",
                        "3.4"),
                fields);
    }

    @Test
    void parsesCommaInsideQuotedField()
            throws Exception {

        List<String> fields =
                CsvParser.parse(
                        "\"Львів, Україна\",10");

        assertEquals(
                List.of(
                        "Львів, Україна",
                        "10"),
                fields);
    }

    @Test
    void parsesEscapedQuoteInsideQuotedField()
            throws Exception {

        List<String> fields =
                CsvParser.parse(
                        "\"станція \"\"Північ\"\"\",10");

        assertEquals(
                List.of(
                        "станція \"Північ\"",
                        "10"),
                fields);
    }

    @Test
    void parsesMultilineQuotedField()
            throws Exception {

        String csv =
                "\"перший рядок\nдругий рядок\",10";

        List<String> fields =
                CsvParser.parse(csv);

        assertEquals(
                "перший рядок\nдругий рядок",
                fields.get(0));

        assertEquals(
                "10",
                fields.get(1));
    }

    @Test
    void parsesMultipleRecordsWithCrLf()
            throws Exception {

        String csv =
                "first,1\r\n"
                        + "second,2\r\n";

        List<List<String>> records =
                CsvParser.parseDocument(csv);

        assertEquals(
                List.of(
                        List.of("first", "1"),
                        List.of("second", "2")),
                records);
    }

    @Test
    void emptyDocumentReturnsEmptyList()
            throws Exception {

        assertEquals(
                List.of(),
                CsvParser.parseDocument(""));
    }

    @Test
    void parseRejectsMultipleRecords() {

        assertThrows(
                DataStorageException.class,
                () -> CsvParser.parse(
                        "first,1\nsecond,2"));
    }

    @Test
    void rejectsUnclosedQuotes() {

        assertThrows(
                DataStorageException.class,
                () -> CsvParser.parseDocument(
                        "\"незакрите поле,10"));
    }

    @Test
    void rejectsCharacterAfterClosingQuote() {

        assertThrows(
                DataStorageException.class,
                () -> CsvParser.parseDocument(
                        "\"значення\"помилка,10"));
    }

    @Test
    void rejectsNullDocument() {

        assertThrows(
                DataStorageException.class,
                () -> CsvParser.parseDocument(null));
    }
}