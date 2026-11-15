package ua.lpnu.kzp;

import java.nio.file.Path;
import java.util.List;

/**
 * Спільний контракт для експорту об'єктів у файловий формат.
 *
 * @param <T> тип об'єктів для експорту
 */
public interface Exporter<T> {

    /**
     * Експортує об'єкти у файл.
     *
     * @param path шлях до файла
     * @param type клас об'єктів
     * @param records об'єкти для експорту
     * @throws DataStorageException якщо експорт неможливий
     */
    void export(
            Path path,
            Class<T> type,
            List<? extends T> records)
            throws DataStorageException;
}