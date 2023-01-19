package net.splitcells.gel.constraint;

import net.splitcells.dem.testing.annotations.UnitTest;
import org.junit.jupiter.api.Test;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.Gel.defineProblem;
import static net.splitcells.gel.data.table.attribute.AttributeI.attribute;
import static net.splitcells.gel.rating.rater.classification.ForAllAttributeValues.forAllAttributeValues;

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

    @UnitTest
    public void testForWithMultipleClassifiers() {
        final var d = attribute(Integer.class, "d");
        final var s = attribute(Integer.class, "s");
        final var testSubject = defineProblem("testForWithMultipleClassifiers")
                .withDemandAttributes(d)
                .withNoDemands()
                .withSupplyAttributes(s)
                .withNoSupplies()
                .withConstraint(r -> {
                    r.forAll(list(forAllAttributeValues(d), forAllAttributeValues(s))).forAll(forAllAttributeValues(d));
                    return r;
                }).toProblem()
                .asSolution();
        final var testResult = testSubject.constraint();
        testResult.childrenView().requireSizeOf(2);
        testResult.childrenView().get(0).childrenView().requireSizeOf(1);
        testResult.childrenView().get(1).childrenView().requireSizeOf(1);
        testResult.childrenView().get(0).childrenView().get(0).childrenView().requireSizeOf(1);
        testResult.childrenView().get(0).childrenView().get(0).childrenView().get(0).childrenView().requireSizeOf(0);
    }

}
