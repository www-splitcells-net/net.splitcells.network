package net.splitcells.gel.solution.optimization.meta;

import org.junit.jupiter.api.Test;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.constraint.type.ForAlls.forAll;
import static net.splitcells.gel.data.table.attribute.AttributeI.integerAttribute;
import static net.splitcells.gel.solution.SolutionBuilder.defineProblem;
import static net.splitcells.gel.solution.optimization.meta.Backtracking.backtracking;
import static net.splitcells.gel.solution.optimization.primitive.enumerable.Initializer.initializer;
import static net.splitcells.gel.solution.optimization.space.EnumerableOptimizationSpaceI.enumerableOptimizationSpace;
import static org.assertj.core.api.Assertions.assertThat;

public class BacktrackingTest {
    @Test
    public void testSimplestCase() {
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
        backtracking().optimize(testData);
        assertThat(testData.size()).isEqualTo(3);
        assertThat(testData.columnView(demandAttribute).get(0)).isEqualTo(1);
        assertThat(testData.columnView(supplyAttribute).get(0)).isEqualTo(4);
        assertThat(testData.columnView(demandAttribute).get(1)).isEqualTo(2);
        assertThat(testData.columnView(supplyAttribute).get(1)).isEqualTo(5);
        assertThat(testData.columnView(demandAttribute).get(2)).isEqualTo(3);
        assertThat(testData.columnView(supplyAttribute).get(2)).isEqualTo(6);
    }

    @Test
    public void testBranching() {
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
                .withConstraint(
                        forAll()
                )
                .toProblem()
                .asSolution();
        backtracking().optimize(testData);
        assertThat(testData.size()).isEqualTo(3);
        assertThat(testData.columnView(demandAttribute).get(0)).isEqualTo(1);
        assertThat(testData.columnView(supplyAttribute).get(0)).isEqualTo(4);
        assertThat(testData.columnView(demandAttribute).get(1)).isEqualTo(2);
        assertThat(testData.columnView(supplyAttribute).get(1)).isEqualTo(5);
        assertThat(testData.columnView(demandAttribute).get(2)).isEqualTo(3);
        assertThat(testData.columnView(supplyAttribute).get(2)).isEqualTo(6);
    }
}
