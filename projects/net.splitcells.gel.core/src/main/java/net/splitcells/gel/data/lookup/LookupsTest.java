package net.splitcells.gel.data.lookup;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static java.util.stream.IntStream.rangeClosed;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.utils.MathUtils.modulus;
import static net.splitcells.dem.utils.random.RandomnessSource.randomness;
import static net.splitcells.gel.Gel.defineProblem;
import static net.splitcells.gel.data.database.Databases.database;
import static net.splitcells.gel.data.table.attribute.AttributeI.attribute;
import static net.splitcells.gel.rating.rater.RaterBasedOnLineValue.lineValueRater;
import static net.splitcells.gel.solution.optimization.primitive.repair.ConstraintGroupBasedRepair.simpleConstraintGroupBasedRepair;
import static net.splitcells.gel.solution.optimization.primitive.repair.GroupSelectors.groupSelector;

public class LookupsTest {
    @Test
    @Disabled
    public void testPerformance() {
        final var a = attribute(Integer.class, "a");
        final var b = attribute(Integer.class, "b");
        final var testSubject = database("testPerformance", a, b);
        final var s = attribute(Integer.class, "s");
        rangeClosed(1, 100000).forEach(i ->
                testSubject.addTranslated(list(i, (Object) modulus(i, 10))));
        testSubject.lookup(b, 1);
        testSubject.lookup(a, 1);
        testSubject.lookup(b, 1).lookup(a, 1);
    }
}
