package ua.lpnu.kzp;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Знаходить та кешує поля, позначені {@link CsvColumn}.
 */
final class AnnotatedFieldResolver {

    private static final Map<Class<?>, List<Field>> CACHE =
            new ConcurrentHashMap<>();

    private AnnotatedFieldResolver() {
    }

    /**
     * Повертає кешований список анотованих полів класу.
     *
     * @param type клас об'єктів
     * @return детерміновано впорядковані поля
     * @throws DataStorageException якщо поля неможливо використати
     */
    static List<Field> resolve(
            Class<?> type)
            throws DataStorageException {

        List<Field> cached =
                CACHE.get(type);

        if (cached != null) {
            return cached;
        }

        List<Field> discovered =
                resolveUncached(type);

        List<Field> previous =
                CACHE.putIfAbsent(
                        type,
                        discovered);

        return previous != null
                ? previous
                : discovered;
    }

    /**
     * Знаходить анотовані поля без використання кешу.
     *
     * <p>Метод потрібний також для порівняння часу
     * reflection із кешуванням і без нього.</p>
     *
     * @param type клас об'єктів
     * @return детерміновано впорядковані поля
     * @throws DataStorageException якщо поля неможливо використати
     */
    static List<Field> resolveUncached(
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

        if (fields.isEmpty()) {
            throw new DataStorageException(
                    "Клас не має полів з @CsvColumn");
        }

        for (Field field : fields) {
            if (!field.trySetAccessible()) {
                throw new DataStorageException(
                        "Немає доступу до поля "
                                + field.getName());
            }
        }

        return List.copyOf(fields);
    }

    /**
     * Очищає кеш reflection-полів.
     * Використовується у тестах і вимірюваннях.
     */
    static void clearCache() {
        CACHE.clear();
    }

    /**
     * Повертає кількість класів у кеші.
     *
     * @return розмір кешу
     */
    static int cacheSize() {
        return CACHE.size();
    }
}