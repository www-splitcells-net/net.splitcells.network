package net.splitcells.gel.constraint;

import net.splitcells.gel.Gel;
import org.junit.jupiter.api.Test;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.Gel.defineProblem;
import static net.splitcells.gel.data.table.attribute.AttributeI.attribute;

public class QueryTest {
    @Test
    public void testConstraintPath() {
        final var d = attribute(Integer.class, "d");
        final var s = attribute(Integer.class, "s");
        final var testData = defineProblem("testConstraintPath")
                .withDemandAttributes(d)
                .withNoDemands()
                .withSupplyAttributes(s)
                .withNoSupplies()
                .withConstraint(r -> {
                    r.forAll(d).forAll(s).forAll(d);
                    return r;
                }).toProblem()
                .asSolution();
        final var testProduct = testData.constraint().query().forAll(d).forAll(s).forAll(d).constraintPath();
        testProduct.assertEquals(list(testData.constraint()
                , testData.constraint().childrenView().get(0)
                , testData.constraint().childrenView().get(0).childrenView().get(0)
                , testData.constraint().childrenView().get(0).childrenView().get(0).childrenView().get(0)));
    }
}
