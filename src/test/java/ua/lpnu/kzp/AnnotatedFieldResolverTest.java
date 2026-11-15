package ua.lpnu.kzp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.lang.reflect.Field;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AnnotatedFieldResolverTest {

    @BeforeEach
    void clearCache() {
        AnnotatedFieldResolver.clearCache();
    }

    @Test
    void resolvesAnnotatedFieldsInDeterministicOrder()
            throws Exception {

        List<Field> fields =
                AnnotatedFieldResolver.resolve(
                        SampleRecord.class);

        assertEquals(
                List.of("alpha", "beta"),
                fields.stream()
                        .map(Field::getName)
                        .toList());
    }

    @Test
    void reusesCachedFieldList()
            throws Exception {

        List<Field> first =
                AnnotatedFieldResolver.resolve(
                        SampleRecord.class);

        List<Field> second =
                AnnotatedFieldResolver.resolve(
                        SampleRecord.class);

        assertSame(first, second);

        assertEquals(
                1,
                AnnotatedFieldResolver.cacheSize());
    }

    @Test
    void uncachedLookupDoesNotPopulateCache()
            throws Exception {

        AnnotatedFieldResolver.resolveUncached(
                SampleRecord.class);

        assertEquals(
                0,
                AnnotatedFieldResolver.cacheSize());
    }

    private static final class SampleRecord {

        @CsvColumn("B")
        private final String beta = "b";

        @CsvColumn("A")
        private final String alpha = "a";
    }
}