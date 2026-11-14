package ua.lpnu.kzp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

class CsvColumnTest {

    private static final class SampleRecord {

        @CsvColumn("Назва")
        private final String name = "test";
    }

    @Test
    void annotationIsAvailableAtRuntimeAndTargetsFields() {
        Retention retention =
                CsvColumn.class.getAnnotation(
                        Retention.class);

        Target target =
                CsvColumn.class.getAnnotation(
                        Target.class);

        assertNotNull(retention);
        assertEquals(
                RetentionPolicy.RUNTIME,
                retention.value());

        assertNotNull(target);
        assertTrue(
                Arrays.asList(target.value())
                        .contains(ElementType.FIELD));
    }

    @Test
    void annotationValueCanBeReadThroughReflection()
            throws NoSuchFieldException {

        Field field =
                SampleRecord.class
                        .getDeclaredField("name");

        CsvColumn column =
                field.getAnnotation(
                        CsvColumn.class);

        assertNotNull(column);
        assertEquals(
                "Назва",
                column.value());
    }
}