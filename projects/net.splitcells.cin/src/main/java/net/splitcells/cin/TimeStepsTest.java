package net.splitcells.cin;

import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.gel.Gel;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

import static java.util.stream.IntStream.range;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.dem.testing.TestTypes.EXPERIMENTAL_TEST;
import static net.splitcells.gel.Gel.defineProblem;
import static net.splitcells.gel.constraint.Constraint.RESULTING_CONSTRAINT_GROUP;
import static net.splitcells.gel.data.table.attribute.AttributeI.attribute;
import static net.splitcells.gel.solution.optimization.primitive.LinearInitialization.linearInitialization;

public class TimeStepsTest {
    @Tag(EXPERIMENTAL_TEST)
    @Test
    public void testTimeSteps() {
        final var time = attribute(Integer.class, "time");
        final var value = attribute(Integer.class, "value");
        final var testSubject = defineProblem("testTimeSteps")
                .withDemandAttributes(time)
                .withDemands(range(0, 100).mapToObj(i -> {
                    if (i <= 33) {
                        return list((Object) 1);
                    } else if (i <= 66) {
                        return list((Object) 2);
                    } else {
                        return list((Object) 3);
                    }
                }).collect(toList()))
                .withSupplyAttributes(value)
                .withSupplies(range(0, 100).mapToObj(i -> list((Object) i)).collect(toList()))
                .withConstraint(c -> {
                    c.forAll(TimeSteps.timeSteps(time));
                    return c;
                })
                .toProblem()
                .asSolution();
        testSubject.optimize(linearInitialization());
        testSubject.constraint().childrenView().get(0).lineProcessing()
                .columnView(RESULTING_CONSTRAINT_GROUP)
                .values()
                .requireSizeOf(3);
    }
}
