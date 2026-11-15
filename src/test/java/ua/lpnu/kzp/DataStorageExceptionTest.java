package ua.lpnu.kzp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

class DataStorageExceptionTest {

    @Test
    void storesMessage() {
        DataStorageException exception =
                new DataStorageException(
                        "Помилка збереження");

        assertEquals(
                "Помилка збереження",
                exception.getMessage());
    }

    @Test
    void storesCause() {
        Exception cause =
                new IllegalStateException(
                        "Першопричина");

        DataStorageException exception =
                new DataStorageException(
                        "Помилка збереження",
                        cause);

        assertSame(
                cause,
                exception.getCause());
    }
}