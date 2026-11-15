package ua.lpnu.kzp;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Objects;

/**
 * Експортує анотовані поля об'єктів у JSON-файл.
 *
 * @param <T> тип об'єктів для експорту
 */
public final class JsonExporter<T>
        implements Exporter<T> {

    /**
     * Створює JSON-exporter.
     */
    public JsonExporter() {
    }

    /**
     * Експортує об'єкти у JSON-файл з кодуванням UTF-8.
     *
     * @param path шлях до JSON-файла
     * @param type клас об'єктів
     * @param records об'єкти для експорту
     * @throws DataStorageException якщо експорт неможливий
     */
    @Override
    public void export(
            Path path,
            Class<T> type,
            List<? extends T> records)
            throws DataStorageException {

        Objects.requireNonNull(
                path,
                "Шлях не може бути null");

        Objects.requireNonNull(
                type,
                "Тип не може бути null");

        Objects.requireNonNull(
                records,
                "Список записів не може бути null");

        List<Field> fields =
                AnnotatedFieldResolver.resolve(type);

        StringBuilder json =
                new StringBuilder("[")
                        .append(System.lineSeparator());

        for (int recordIndex = 0;
                recordIndex < records.size();
                recordIndex++) {

            T record =
                    records.get(recordIndex);

            if (record == null) {
                throw new DataStorageException(
                        "JSON-запис не може бути null");
            }

            json.append("  {");

            for (int fieldIndex = 0;
                    fieldIndex < fields.size();
                    fieldIndex++) {

                Field field =
                        fields.get(fieldIndex);

                if (fieldIndex > 0) {
                    json.append(',');
                }

                String columnName =
                        field.getAnnotation(
                                CsvColumn.class)
                                .value();

                json.append('"')
                        .append(escape(columnName))
                        .append("\":");

                try {
                    Object value =
                            field.get(record);

                    appendValue(json, value);

                } catch (IllegalAccessException exception) {
                    throw new DataStorageException(
                            "Не вдалося прочитати поле "
                                    + field.getName(),
                            exception);
                }
            }

            json.append('}');

            if (recordIndex
                    < records.size() - 1) {
                json.append(',');
            }

            json.append(
                    System.lineSeparator());
        }

        json.append(']');

        try {
            Path parent =
                    path.toAbsolutePath()
                            .getParent();

            if (parent != null) {
                Files.createDirectories(parent);
            }

            Files.writeString(
                    path,
                    json.toString(),
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE);

        } catch (IOException exception) {
            throw new DataStorageException(
                    "Не вдалося записати JSON: "
                            + path,
                    exception);
        }
    }

    private static void appendValue(
            StringBuilder json,
            Object value) {

        if (value == null) {
            json.append("null");
            return;
        }

        if (value instanceof Number
                || value instanceof Boolean) {

            json.append(value);
            return;
        }

        json.append('"')
                .append(
                        escape(
                                value.toString()))
                .append('"');
    }

    private static String escape(
            String value) {

        StringBuilder escaped =
                new StringBuilder();

        for (int index = 0;
                index < value.length();
                index++) {

            char symbol =
                    value.charAt(index);

            switch (symbol) {
                case '"' -> escaped.append("\\\"");
                case '\\' -> escaped.append("\\\\");
                case '\b' -> escaped.append("\\b");
                case '\f' -> escaped.append("\\f");
                case '\n' -> escaped.append("\\n");
                case '\r' -> escaped.append("\\r");
                case '\t' -> escaped.append("\\t");

                default -> {
                    if (symbol < 0x20) {
                        escaped.append(
                                "\\u%04x".formatted(
                                        (int) symbol));
                    } else {
                        escaped.append(symbol);
                    }
                }
            }
        }

        return escaped.toString();
    }
}