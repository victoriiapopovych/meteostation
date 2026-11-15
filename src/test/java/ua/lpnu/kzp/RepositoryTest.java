package ua.lpnu.kzp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

class RepositoryTest {

    @Test
    void addsAndReturnsItems() {
        Repository<String> repository =
                new Repository<>();

        repository.add("first");
        repository.add("second");

        assertEquals(
                List.of("first", "second"),
                repository.all());
    }

    @Test
    void allReturnsImmutableSnapshot() {
        Repository<String> repository =
                new Repository<>();

        repository.add("first");

        List<String> snapshot = repository.all();

        assertThrows(
                UnsupportedOperationException.class,
                () -> snapshot.add("second"));

        repository.add("second");

        assertEquals(
                List.of("first"),
                snapshot);

        assertEquals(
                List.of("first", "second"),
                repository.all());
    }

    @Test
    void findsItemsByPredicate() {
        Repository<Integer> repository =
                new Repository<>();

        repository.add(5);
        repository.add(10);
        repository.add(15);

        assertEquals(
                List.of(10, 15),
                repository.find(
                        value -> value >= 10));
    }

    @Test
    void rejectsNullItem() {
        Repository<String> repository =
                new Repository<>();

        assertThrows(
                NullPointerException.class,
                () -> repository.add(null));
    }

    @Test
    void rejectsNullPredicate() {
        Repository<String> repository =
                new Repository<>();

        assertThrows(
                NullPointerException.class,
                () -> repository.find(null));
    }
}