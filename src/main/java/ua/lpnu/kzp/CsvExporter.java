package ua.lpnu.kzp;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Експортує анотовані поля об'єктів у CSV-файл.
 *
 * @param <T> тип об'єктів для експорту
 */
public final class CsvExporter<T>
        implements Exporter<T> {

    /**
     * Створює CSV-exporter.
     */
    public CsvExporter() {
    }

    /**
     * Експортує записи відповідно до спільного контракту.
     *
     * @param path шлях до CSV-файла
     * @param type клас об'єктів
     * @param records записи для експорту
     * @throws DataStorageException якщо експорт неможливий
     */
    @Override
    public void export(
            Path path,
            Class<T> type,
            List<? extends T> records)
            throws DataStorageException {

        write(path, type, records);
    }

    /**
     * Записує об'єкти у CSV із використанням кешованих
     * reflection-полів.
     *
     * @param path шлях до CSV-файла
     * @param type клас об'єктів
     * @param records об'єкти для експорту
     * @param <E> тип об'єктів
     * @throws DataStorageException якщо експорт неможливий
     */
    public static <E> void write(
            Path path,
            Class<E> type,
            List<? extends E> records)
            throws DataStorageException {

        List<Field> fields =
                AnnotatedFieldResolver.resolve(type);

        writeInternal(
                path,
                records,
                fields);
    }

    /**
     * Виконує такий самий CSV-експорт,
     * але без використання кешу reflection-полів.
     *
     * <p>Метод використовується для вимірювання
     * ефекту кешування.</p>
     *
     * @param path шлях до CSV-файла
     * @param type клас об'єктів
     * @param records об'єкти для експорту
     * @param <E> тип об'єктів
     * @throws DataStorageException якщо експорт неможливий
     */
    static <E> void writeWithoutCache(
            Path path,
            Class<E> type,
            List<? extends E> records)
            throws DataStorageException {

        List<Field> fields =
                AnnotatedFieldResolver.resolveUncached(
                        type);

        writeInternal(
                path,
                records,
                fields);
    }

    private static <E> void writeInternal(
            Path path,
            List<? extends E> records,
            List<Field> fields)
            throws DataStorageException {

        Objects.requireNonNull(
                path,
                "Шлях не може бути null");

        Objects.requireNonNull(
                records,
                "Список записів не може бути null");

        String header = fields.stream()
                .map(field -> field
                        .getAnnotation(CsvColumn.class)
                        .value())
                .map(CsvExporter::escape)
                .collect(Collectors.joining(","));

        StringBuilder csv =
                new StringBuilder(header)
                        .append(System.lineSeparator());

        for (E record : records) {

            if (record == null) {
                throw new DataStorageException(
                        "CSV-запис не може бути null");
            }

            StringBuilder row =
                    new StringBuilder();

            for (int index = 0;
                    index < fields.size();
                    index++) {

                Field field =
                        fields.get(index);

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
                    "Не вдалося записати CSV: "
                            + path,
                    exception);
        }
    }

    private static String escape(
            String value) {

        boolean requiresQuotes =
                value.contains(",")
                        || value.contains("\"")
                        || value.contains("\r")
                        || value.contains("\n");

        if (!requiresQuotes) {
            return value;
        }

        return "\""
                + value.replace(
                        "\"",
                        "\"\"")
                + "\"";
    }
}