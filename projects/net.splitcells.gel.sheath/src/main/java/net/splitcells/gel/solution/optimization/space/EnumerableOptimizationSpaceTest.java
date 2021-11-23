package net.splitcells.gel.solution.optimization.space;

import org.junit.jupiter.api.Test;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.constraint.type.ForAlls.forAll;
import static net.splitcells.gel.solution.SolutionBuilder.defineProblem;
import static net.splitcells.gel.solution.optimization.primitive.enumerable.Initializer.initializer;
import static net.splitcells.gel.solution.optimization.space.EnumerableOptimizationSpaceI.enumerableOptimizationSpace;
import static org.assertj.core.api.Assertions.assertThat;

public class EnumerableOptimizationSpaceTest {
    @Test
    public void testHistory() {
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
        final var testSubject = enumerableOptimizationSpace(testData, initializer());
        assertThat(testSubject.currentState().size()).isEqualTo(0);
    }
}
