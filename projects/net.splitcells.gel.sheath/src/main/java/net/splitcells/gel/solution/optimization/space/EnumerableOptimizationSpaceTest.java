package net.splitcells.gel.solution.optimization.space;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.constraint.type.ForAlls.forAll;
import static net.splitcells.gel.data.table.attribute.AttributeI.integerAttribute;
import static net.splitcells.gel.solution.SolutionBuilder.defineProblem;
import static net.splitcells.gel.solution.optimization.primitive.enumerable.Initializer.initializer;
import static net.splitcells.gel.solution.optimization.space.EnumerableOptimizationSpaceI.enumerableOptimizationSpace;
import static org.assertj.core.api.Assertions.assertThat;

public class EnumerableOptimizationSpaceTest {
    @Test
    public void testSimpleHistory() {
        final var testData = defineProblem()
                .withDemandAttributes()
                .withDemands(list
                        (list()
                                , list()
                                , list()))
                .withSupplyAttributes()
                .withSupplies(list
                        (list()
                                , list()
                                , list()))
                .withConstraint(forAll())
                .toProblem()
                .asSolution();
        var testSubject = enumerableOptimizationSpace(testData, initializer());
        assertThat(testSubject.currentState().size()).isEqualTo(0);
        testSubject = testSubject.child(0);
        assertThat(testSubject.currentState().size()).isEqualTo(1);
        testSubject = testSubject.child(0);
        assertThat(testSubject.currentState().size()).isEqualTo(2);
        testSubject = testSubject.parent().get();
        assertThat(testSubject.currentState().size()).isEqualTo(1);
        testSubject = testSubject.parent().get();
        assertThat(testSubject.currentState().size()).isEqualTo(0);
        assertThat(testSubject.parent()).isEqualTo(Optional.empty());
    }

    @Test
    public void testDiscoveryPath() {
        final var demandAttribute = integerAttribute("d");
        final var supplyAttribute = integerAttribute("s");
        final var testData = defineProblem()
                .withDemandAttributes(demandAttribute)
                .withDemands(list
                        (list(1)
                                , list(2)
                                , list(3)))
                .withSupplyAttributes(supplyAttribute)
                .withSupplies(list
                        (list(4)
                                , list(5)
                                , list(6)))
                .withConstraint(forAll())
                .toProblem()
                .asSolution();
        var testSubject = enumerableOptimizationSpace(testData, initializer());
        assertThat(testSubject.currentState().size()).isEqualTo(0);

        testSubject = testSubject.child(0);
        assertThat(testSubject.currentState().columnView(demandAttribute).get(0)).isEqualTo(1);
        assertThat(testSubject.currentState().columnView(supplyAttribute).get(0)).isEqualTo(4);
        assertThat(testSubject.currentState().size()).isEqualTo(1);

        testSubject = testSubject.child(2);
        assertThat(testSubject.currentState().columnView(demandAttribute).get(1)).isEqualTo(3);
        assertThat(testSubject.currentState().columnView(supplyAttribute).get(1)).isEqualTo(5);
        assertThat(testSubject.currentState().size()).isEqualTo(2);

        testSubject = testSubject.parent().get();
        assertThat(testSubject.currentState().columnView(demandAttribute).get(0)).isEqualTo(1);
        assertThat(testSubject.currentState().columnView(supplyAttribute).get(0)).isEqualTo(4);
        assertThat(testSubject.currentState().size()).isEqualTo(1);

        testSubject = testSubject.parent().get();
        assertThat(testSubject.currentState().size()).isEqualTo(0);
        assertThat(testSubject.parent()).isEqualTo(Optional.empty());
    }
}
