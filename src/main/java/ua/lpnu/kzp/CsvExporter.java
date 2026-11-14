package ua.lpnu.kzp;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Експортує анотовані поля об'єктів у CSV-файл.
 */
public final class CsvExporter {

    private CsvExporter() {
    }

    /**
     * Записує об'єкти у CSV-файл з кодуванням UTF-8.
     *
     * @param path шлях до CSV-файла
     * @param type клас об'єктів
     * @param records об'єкти для експорту
     * @param <T> тип об'єктів
     * @throws DataStorageException якщо експорт неможливий
     */
    public static <T> void write(
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
                annotatedFields(type);

        if (fields.isEmpty()) {
            throw new DataStorageException(
                    "Клас не має полів з @CsvColumn");
        }

        String header = fields.stream()
                .map(field -> field
                        .getAnnotation(CsvColumn.class)
                        .value())
                .map(CsvExporter::escape)
                .collect(Collectors.joining(","));

        StringBuilder csv =
                new StringBuilder(header)
                        .append(System.lineSeparator());

        for (T record : records) {

            if (record == null) {
                throw new DataStorageException(
                        "CSV-запис не може бути null");
            }

            StringBuilder row =
                    new StringBuilder();

            for (int index = 0;
                    index < fields.size();
                    index++) {

                Field field = fields.get(index);

                if (index > 0) {
                    row.append(',');
                }

                try {
                    Object value =
                            field.get(record);

                    row.append(
                            escape(
                                    Objects.toString(
                                            value,
                                            "")));

                } catch (IllegalAccessException exception) {
                    throw new DataStorageException(
                            "Не вдалося прочитати поле "
                                    + field.getName(),
                            exception);
                }
            }

            csv.append(row)
                    .append(System.lineSeparator());
        }

        try {
            Path parent =
                    path.toAbsolutePath()
                            .getParent();

            if (parent != null) {
                Files.createDirectories(parent);
            }

            Files.writeString(
                    path,
                    csv.toString(),
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE);

        } catch (IOException exception) {
            throw new DataStorageException(
                    "Не вдалося записати CSV: " + path,
                    exception);
        }
    }

    private static List<Field> annotatedFields(
            Class<?> type)
            throws DataStorageException {

        List<Field> fields =
                Arrays.stream(type.getDeclaredFields())
                        .filter(field ->
                                field.isAnnotationPresent(
                                        CsvColumn.class))
                        .sorted(
                                Comparator.comparing(
                                        Field::getName))
                        .toList();

        for (Field field : fields) {
            if (!field.trySetAccessible()) {
                throw new DataStorageException(
                        "Немає доступу до поля "
                                + field.getName());
            }
        }

        return fields;
    }

    private static String escape(String value) {
        boolean requiresQuotes =
                value.contains(",")
                        || value.contains("\"")
                        || value.contains("\r")
                        || value.contains("\n");

        if (!requiresQuotes) {
            return value;
        }

        return "\""
                + value.replace("\"", "\"\"")
                + "\"";
    }
}