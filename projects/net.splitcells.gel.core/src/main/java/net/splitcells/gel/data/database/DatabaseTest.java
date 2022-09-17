package net.splitcells.gel.data.database;

import net.splitcells.dem.testing.TestTypes;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

import static java.util.stream.IntStream.rangeClosed;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.gel.constraint.Constraint.LINE;
import static net.splitcells.gel.data.database.Databases.database;
import static net.splitcells.gel.data.table.attribute.AttributeI.attribute;

public class DatabaseTest {
    @Test
    @Tag(TestTypes.EXPERIMENTAL_TEST)
    public void testQueryInitialization() {
        final var index = attribute(Integer.class);
        final var testSubject = database(listWithValuesOf(index));
        rangeClosed(1, 10).forEach(i -> {
            testSubject.addTranslated(listWithValuesOf(i));
        });
        testSubject
                .query()
                .forAllCombinationsOf(listWithValuesOf(index))
                .currentConstraint()
                .lines()
                .columnView(LINE)
                .values()
                .requireSizeOf(10);
    }
}
