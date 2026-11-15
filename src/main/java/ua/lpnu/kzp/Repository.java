package ua.lpnu.kzp;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * Типобезпечне сховище об'єктів у пам'яті.
 *
 * @param <T> тип об'єктів у сховищі
 */
public final class Repository<T> {

    private final List<T> items = new ArrayList<>();

    /**
     * Додає об'єкт до сховища.
     *
     * @param item об'єкт для додавання
     */
    public void add(T item) {
        items.add(
                Objects.requireNonNull(
                        item,
                        "Запис не може бути null"));
    }

    /**
     * Повертає незмінний знімок усіх об'єктів.
     *
     * @return незмінний список об'єктів
     */
    public List<T> all() {
        return List.copyOf(items);
    }

    /**
     * Повертає об'єкти, що задовольняють умову.
     *
     * @param condition умова пошуку
     * @return список знайдених об'єктів
     */
    public List<T> find(
            Predicate<? super T> condition) {

        Objects.requireNonNull(
                condition,
                "Умова пошуку не може бути null");

        return items.stream()
                .filter(condition)
                .toList();
    }
}