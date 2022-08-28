/*
 * Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License,
 * which is available at https://spdx.org/licenses/MIT.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 */
package net.splitcells.gel.constraint;

import net.splitcells.gel.constraint.type.ForAlls;
import org.junit.jupiter.api.Test;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.gel.constraint.Constraint.incomingGroupsOfConstraintPath;
import static net.splitcells.gel.constraint.type.ForAlls.forAll;
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
        final var solution = defineProblem("test_incomingGroupsOfConstraintPath")
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
                        (ForAlls.forEach(attribute)
                                .withChildren(ForAlls.forEach(attribute)
                                        , ForAlls.forEach(attribute)
                                                .withChildren(ForAlls.forEach(attribute))))
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
        assertThat(solution.lines()).hasSize(4);
        assertThat(allocations).containsAll(solution.lines());
    }

    @Test
    public void testAllocationGroups() {
        final var constraint_1 = forAll();
        final var constraint_2 = forAll();
        final var constraint_3 = forAll();
        final var constraint_4 = forAll();
        final var constraint_5 = forAll();
        @SuppressWarnings("unchecked") final var solution
                = defineProblem("testAllocationGroups")
                .withDemandAttributes()
                .withNoDemands()
                .withSupplyAttributes()
                .withNoSupplies()
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
        final var solution = defineProblem("test_allocation_groups_with_different_attributes")
                .withDemandAttributes(A, B)
                .withNoDemands()
                .withSupplyAttributes(C, D)
                .withNoSupplies()
                .withConstraint(
                        ForAlls.forEach(A)
                                .withChildren(ForAlls.forEach(B)
                                        , ForAlls.forEach(C)
                                                .withChildren(ForAlls.forEach(D))))
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