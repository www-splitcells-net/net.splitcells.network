package net.splitcells.gel.solution.optimization.primitive;

import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.utils.random.Randomness;
import net.splitcells.gel.constraint.type.Then;
import net.splitcells.gel.data.table.attribute.Attribute;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.testing.TestTypes.INTEGRATION_TEST;
import static net.splitcells.gel.data.table.attribute.AttributeI.attribute;
import static net.splitcells.gel.rating.rater.ConstantRater.constantRater;
import static net.splitcells.gel.rating.type.Cost.cost;
import static net.splitcells.gel.solution.SolutionBuilder.defineProblem;
import static net.splitcells.gel.solution.optimization.primitive.LinearInitialization.linearInitialization;
import static net.splitcells.gel.solution.optimization.primitive.UsedSupplySwitcher.usedSupplySwitcher;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.AdditionalAnswers.returnsElementsOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UsedSupplySwitcherTest {
    public static final Attribute<Integer> A = attribute(Integer.class, "A");
    public static final Attribute<Integer> B = attribute(Integer.class, "B");

    @Tag(INTEGRATION_TEST)
    @Test
    public void testSwitchWithNoUnused() {
        final var testSolution = defineProblem()
                .withDemandAttributes(A)
                .withDemands(
                        list(
                                list(1)
                                , list(2)))
                .withSupplyAttributes(B)
                .withSupplies(
                        list(
                                list(1)
                                , list(2)))
                .withConstraint(Then.then(constantRater(cost(1))))
                .toProblem()
                .asSolution();
        {
            testSolution.optimize(linearInitialization());
            assertThat(testSolution.allocations().getLines(0).value(A)).isEqualTo(1);
            assertThat(testSolution.allocations().getLines(0).value(B)).isEqualTo(1);
            assertThat(testSolution.allocations().getLines(1).value(A)).isEqualTo(2);
            assertThat(testSolution.allocations().getLines(1).value(B)).isEqualTo(2);
            assertThat(testSolution.demands_unused().getLines()).isEmpty();
            assertThat(testSolution.supplies_free().getLines()).isEmpty();
        }
        final var randomness = mock(Randomness.class);
        doReturn(0, 1)
                .when(randomness)
                .integer(any(), any());
        testSolution.optimizeOnce(UsedSupplySwitcher.usedSupplySwitcher(randomness));
        {
            assertThat(testSolution.allocations().getLines(0).value(A)).isEqualTo(1);
            assertThat(testSolution.allocations().getLines(0).value(B)).isEqualTo(2);
            assertThat(testSolution.allocations().getLines(1).value(A)).isEqualTo(2);
            assertThat(testSolution.allocations().getLines(1).value(B)).isEqualTo(1);
        }
    }

    @Tag(INTEGRATION_TEST)
    @Test
    public void testSwitchWithUnusedSupplyPresent() {
        final var testSolution = defineProblem()
                .withDemandAttributes(A)
                .withDemands(
                        list(
                                list(1)
                                , list(2)))
                .withSupplyAttributes(B)
                .withSupplies(
                        list(
                                list(1)
                                , list(2)
                                , list(3)))
                .withConstraint(Then.then(constantRater(cost(1))))
                .toProblem()
                .asSolution();
        {
            testSolution.optimize(linearInitialization());
            assertThat(testSolution.allocations().getLines(0).value(A)).isEqualTo(1);
            assertThat(testSolution.allocations().getLines(0).value(B)).isEqualTo(1);
            assertThat(testSolution.allocations().getLines(1).value(A)).isEqualTo(2);
            assertThat(testSolution.allocations().getLines(1).value(B)).isEqualTo(2);
            assertThat(testSolution.demands_unused().getLines()).isEmpty();
            assertThat(testSolution.supplies_free().getLines()).hasSize(1);
        }
        final var randomness = mock(Randomness.class);
        doReturn(0, 1)
                .when(randomness)
                .integer(any(), any());
        testSolution.optimizeOnce(UsedSupplySwitcher.usedSupplySwitcher(randomness));
        {
            assertThat(testSolution.allocations().getLines(0).value(A)).isEqualTo(1);
            assertThat(testSolution.allocations().getLines(0).value(B)).isEqualTo(2);
            assertThat(testSolution.allocations().getLines(1).value(A)).isEqualTo(2);
            assertThat(testSolution.allocations().getLines(1).value(B)).isEqualTo(1);
        }
    }

    @Tag(INTEGRATION_TEST)
    @Test
    public void testSwitchWithUnusedDemandPresent() {
        final var testSolution = defineProblem()
                .withDemandAttributes(A)
                .withDemands(
                        list(
                                list(1)
                                , list(2)
                                , list(3)))
                .withSupplyAttributes(B)
                .withSupplies(
                        list(
                                list(1)
                                , list(2)))
                .withConstraint(Then.then(constantRater(cost(1))))
                .toProblem()
                .asSolution();
        {
            testSolution.optimize(linearInitialization());
            assertThat(testSolution.allocations().getLines(0).value(A)).isEqualTo(1);
            assertThat(testSolution.allocations().getLines(0).value(B)).isEqualTo(1);
            assertThat(testSolution.allocations().getLines(1).value(A)).isEqualTo(2);
            assertThat(testSolution.allocations().getLines(1).value(B)).isEqualTo(2);
            assertThat(testSolution.demands_unused().getLines()).hasSize(1);
            assertThat(testSolution.supplies_free().getLines()).isEmpty();
        }
        final var randomness = mock(Randomness.class);
        doReturn(0, 1)
                .when(randomness)
                .integer(any(), any());
        testSolution.optimizeOnce(UsedSupplySwitcher.usedSupplySwitcher(randomness));
        {
            assertThat(testSolution.allocations().getLines(0).value(A)).isEqualTo(1);
            assertThat(testSolution.allocations().getLines(0).value(B)).isEqualTo(2);
            assertThat(testSolution.allocations().getLines(1).value(A)).isEqualTo(2);
            assertThat(testSolution.allocations().getLines(1).value(B)).isEqualTo(1);
        }
    }

    @Tag(INTEGRATION_TEST)
    @Test
    public void testSwitchMultipleStepsInOptimization() {
        final var stepCount = 2;
        final var variables = 4;
        final var testSolution = defineProblem()
                .withDemandAttributes(A)
                .withDemands(
                        range(0, variables).boxed().map(i -> Lists.<Object>list(i)).collect(Lists.toList()))
                .withSupplyAttributes(B)
                .withSupplies(
                        range(0, variables).boxed().map(i -> Lists.<Object>list(i)).collect(Lists.toList()))
                .withConstraint(Then.then(constantRater(cost(1))))
                .toProblem()
                .asSolution();
        {
            testSolution.optimize(linearInitialization());
            assertThat(testSolution.allocations().size()).isEqualTo(4);
            range(0, variables).forEach(i -> assertThat(testSolution.allocations().getLines(i).value(A)).isEqualTo(i));
            assertThat(testSolution.demands_unused().getLines()).isEmpty();
            assertThat(testSolution.supplies_free().getLines()).isEmpty();
        }
        final var randomness = mock(Randomness.class);
        doAnswer(returnsElementsOf(range(0, variables).boxed().collect(toList())))
                .when(randomness)
                .integer(any(), any());
        testSolution.optimizeOnce(usedSupplySwitcher(randomness, stepCount));
        assertThat(testSolution.history().size()).isEqualTo(variables + stepCount * 4);
    }
}