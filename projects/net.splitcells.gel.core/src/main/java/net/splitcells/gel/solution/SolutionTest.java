package net.splitcells.gel.solution;

import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.utils.MathUtils;
import net.splitcells.gel.Gel;
import net.splitcells.gel.data.table.attribute.AttributeI;
import net.splitcells.gel.rating.rater.RaterBasedOnLineValue;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;
import java.util.stream.IntStream;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.dem.utils.MathUtils.modulus;
import static net.splitcells.dem.utils.random.RandomnessSource.randomness;
import static net.splitcells.gel.Gel.defineProblem;
import static net.splitcells.gel.data.table.attribute.AttributeI.attribute;
import static net.splitcells.gel.rating.rater.RaterBasedOnLineValue.lineValueRater;
import static net.splitcells.gel.solution.optimization.primitive.OnlineLinearDeinitializer.onlineLinearDeinitializer;
import static net.splitcells.gel.solution.optimization.primitive.OnlineLinearInitialization.onlineLinearInitialization;
import static net.splitcells.gel.solution.optimization.primitive.repair.ConstraintGroupBasedRepair.simpleConstraintGroupBasedRepair;
import static net.splitcells.gel.solution.optimization.primitive.repair.GroupSelectors.groupSelector;
import static net.splitcells.gel.solution.optimization.primitive.repair.SupplySelectors.hillClimber;

public class SolutionTest {
    @Test
    @Disabled
    public void testPerformance() {
        final var d = attribute(Integer.class, "d");
        final var s = attribute(Integer.class, "s");
        final var testSubject = defineProblem("testPerformance")
                .withDemandAttributes(d)
                .withDemands(IntStream.rangeClosed(1, 10000).mapToObj(i -> list((Object) i)).collect(toList()))
                .withSupplyAttributes(s)
                .withSupplies(IntStream.rangeClosed(1, 100000).mapToObj(i -> list((Object) modulus(i, 10))).collect(toList()))
                .withConstraint(c -> {
                    c.forAll(s).then(lineValueRater(l -> l.value(d) == -1));
                    return c;
                })
                .toProblem()
                .asSolution();
        testSubject.history().processWithoutHistory(() -> {
            testSubject.optimize(onlineLinearInitialization());
            simpleConstraintGroupBasedRepair(groupSelector(randomness(), 1
                    , 1), a -> solution -> {
            }, false).optimize(testSubject);
        });
    }
}
