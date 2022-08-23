package net.splitcells.gel.data.lookup;

import net.splitcells.gel.Gel;
import org.junit.jupiter.api.Test;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.constraint.type.ForAlls.forAll;
import static net.splitcells.gel.data.table.attribute.AttributeI.attribute;
import static net.splitcells.gel.solution.optimization.primitive.LinearDeinitializer.linearDeinitializer;
import static net.splitcells.gel.solution.optimization.primitive.LinearInitialization.linearInitialization;

public class LookupTableTest {
    @Test
    public void testLookup() {
        final var a = attribute(Integer.class, "a");
        final var b = attribute(Integer.class, "b");
        final var testSubject = Gel.defineProblem("testLookup")
                .withDemandAttributes(a, b)
                .withDemands(list(list(1, 1), list(1, 2)))
                .withSupplyAttributes()
                .withNoSupplies()
                .withConstraint(forAll())
                .toProblem()
                .asSolution();
        testSubject.lookup(a, 1).lookup(b, 2).lines();
        testSubject.optimize(linearInitialization());
        testSubject.lookup(a, 1).lookup(b, 2).lines();
        testSubject.optimize(linearDeinitializer());
        testSubject.lookup(a, 1).lookup(b, 2).lines();
        testSubject.optimize(linearInitialization());
        testSubject.lookup(a, 1).lookup(b, 2).lines();
        testSubject.optimize(linearDeinitializer());
        testSubject.lookup(a, 1).lookup(b, 2).lines();
    }
}
