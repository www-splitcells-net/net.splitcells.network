package net.splitcells.gel.constraint;

import net.splitcells.gel.constraint.type.ForAlls;
import org.junit.jupiter.api.Test;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.gel.constraint.Constraint.incomingGroupsOfConstraintPath;
import static net.splitcells.gel.constraint.type.ForAlls.for_all;
import static net.splitcells.gel.data.table.attribute.AttributeI.attribute;
import static net.splitcells.gel.solution.SolutionBuilder.defineProblem;
import static net.splitcells.gel.solution.optimization.primitive.LinearInitialization.linearInitialization;
import static org.assertj.core.api.Assertions.assertThat;

public class ConstraintTest {

    @Test
    public void test_incomingGroupsOfConstraintPath() {
        final var attribute = attribute(Integer.class, "a");
        final int valueA = 1;
        final int valueB = 3;
        final var solution = defineProblem()
                .withDemandAttributes(attribute)
                .withDemands
                        (list
                                (list(valueA)
                                        , list(valueB)
                                        , list(valueA)
                                        , list(valueB)))
                .withSupplyAttributes()
                .withEmptySupplies(4)
                .withConstraint
                        (ForAlls.for_each(attribute)
                                .withChildren(ForAlls.for_each(attribute)
                                        , ForAlls.for_each(attribute)
                                                .withChildren(ForAlls.for_each(attribute))))
                .toProblem()
                .asSolution();
        solution.optimize(linearInitialization());
        final var testVerifier = solution.constraint().childrenView().get(1).childrenView().get(0);
        final var allocations = incomingGroupsOfConstraintPath
                (list
                        (solution.constraint()
                                , solution.constraint().childrenView().get(1)
                                , testVerifier))
                .stream()
                .map(e -> testVerifier.complying(e))
                .flatMap(e -> e.stream())
                .collect(toList());
        assertThat(solution.getLines()).hasSize(4);
        assertThat(allocations).containsAll(solution.getLines());
    }

    @Test
    public void testAllocationGroups() {
        final var constraint_1 = for_all();
        final var constraint_2 = for_all();
        final var constraint_3 = for_all();
        final var constraint_4 = for_all();
        final var constraint_5 = for_all();
        @SuppressWarnings("unchecked") final var solution
                = defineProblem()
                .withDemandAttributes()
                .withSupplyAttributes()
                .withConstraint(constraint_1
                        .withChildren(constraint_2)
                        .withChildren(constraint_3
                                .withChildren(constraint_4)
                                .withChildren(constraint_5)))
                .toProblem()
                .asSolution();
        final var testProduct = Constraint.allocationGroups(solution.constraint());
        assertThat(testProduct.get(0)).containsExactly(constraint_1);
        assertThat(testProduct.get(1)).containsExactly(constraint_1, constraint_2);
        assertThat(testProduct.get(2)).containsExactly(constraint_1, constraint_3);
        assertThat(testProduct.get(3)).containsExactly(constraint_1, constraint_3, constraint_4);
        assertThat(testProduct.get(4)).containsExactly(constraint_1, constraint_3, constraint_5);
        assertThat(testProduct).hasSize(5);
    }

    @Test
    public void test_allocation_groups_with_different_attributes() {
        final var A = attribute(Integer.class, "a");
        final var B = attribute(Integer.class, "b");
        final var C = attribute(Integer.class, "c");
        final var D = attribute(Integer.class, "d");
        final var solution = defineProblem()
                .withDemandAttributes(A, B)
                .withSupplyAttributes(C, D)
                .withConstraint(
                        ForAlls.for_each(A)
                                .withChildren(ForAlls.for_each(B)
                                        , ForAlls.for_each(C)
                                                .withChildren(ForAlls.for_each(D))))
                .toProblem()
                .asSolution();
        final var testData = Constraint.allocationGroups(solution.constraint());
        assertThat(testData).hasSize(4);
        assertThat(testData.get(0)).isEqualTo(list(solution.constraint()));
        assertThat(testData.get(1))
                .isEqualTo(list
                        (solution.constraint()
                                , solution.constraint().childrenView().get(0)));
        assertThat(testData.get(2))
                .isEqualTo(list
                        (solution.constraint()
                                , solution.constraint().childrenView().get(1)));
        assertThat(testData.get(3))
                .isEqualTo(list
                        (solution.constraint()
                                , solution.constraint().childrenView().get(1)
                                , solution.constraint().childrenView().get(1)
                                        .childrenView().get(0)));
    }
}
