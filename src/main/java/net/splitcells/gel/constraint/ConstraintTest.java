package net.splitcells.gel.constraint;

import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.constraint.type.ForAlls;
import org.junit.jupiter.api.Test;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.data.table.attribute.AttributeI.attribute;
import static net.splitcells.gel.solution.SolutionBuilder.define_problem;
import static org.assertj.core.api.Assertions.assertThat;

public class ConstraintTest {

    @Test
    public void testAllocationGroups() {
        final var a = attribute(Integer.class, "a");
        final var b = attribute(Integer.class, "b");
        final var constraint_1 = ForAlls.forAll(a);
        final var constraint_2 = ForAlls.forAll(a);
        final var constraint_3 = ForAlls.forAll(a);
        final var constraint_4 = ForAlls.forAll(a);
        final var constraint_5 = ForAlls.forAll(a);
        @SuppressWarnings("unchecked") final var solution
                = define_problem()
                .withDemandAttributes(a, b)
                .withDemands
                        (list(1, 1)
                                , list(1, 1)
                                , list(1, 2)
                                , list(1, 2)
                                , list(2, 1)
                                , list(2, 1))
                .withSupplyAttributes()
                .withSupplies
                        (list()
                                , list()
                                , list()
                                , list()
                                , list()
                                , list())
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
}
