/*
 * Copyright (c) 2021 Contributors To The `net.splitcells.*` Projects
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License v2.0 or later
 * which is available at https://www.gnu.org/licenses/old-licenses/gpl-2.0-standalone.html
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.constraint;

import net.splitcells.gel.constraint.type.ForAlls;
import org.junit.jupiter.api.Test;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.gel.constraint.Constraint.allocationGroups;
import static net.splitcells.gel.constraint.Constraint.incomingGroupsOfConstraintPath;
import static net.splitcells.gel.constraint.type.ForAlls.forAll;
import static net.splitcells.gel.data.view.attribute.AttributeI.attribute;
import static net.splitcells.gel.solution.SolutionBuilder.defineProblem;
import static net.splitcells.gel.solution.optimization.primitive.OfflineLinearInitialization.offlineLinearInitialization;
import static org.assertj.core.api.Assertions.assertThat;

public class ConstraintTest {

    @Test
    public void test_incomingGroupsOfConstraintPath() {
        final var attribute = attribute(Integer.class, "a");
        final int valueA = 1;
        final int valueB = 3;
        final var solution = defineProblem("test_incomingGroupsOfConstraintPath")
                .withDemandAttributes(attribute)
                .withDemands(list(list(valueA)
                        , list(valueB)
                        , list(valueA)
                        , list(valueB)))
                .withSupplyAttributes()
                .withEmptySupplies(4)
                .withConstraint(ForAlls.forEach(attribute)
                        .withChildren(ForAlls.forEach(attribute)
                                , ForAlls.forEach(attribute)
                                        .withChildren(ForAlls.forEach(attribute))))
                .toProblem()
                .asSolution();
        solution.optimize(offlineLinearInitialization());
        final var testVerifier = solution.constraint().childrenView().get(1).childrenView().get(0);
        final var allocations = incomingGroupsOfConstraintPath(list(solution.constraint()
                , solution.constraint().childrenView().get(1)
                , testVerifier))
                .stream()
                .map(e -> testVerifier.complying(e))
                .flatMap(e -> e.stream())
                .collect(toList());
        assertThat(solution.unorderedLines()).hasSize(4);
        assertThat(allocations).containsAll(solution.unorderedLines());
    }

    @Test
    public void testAllocationGroups() {
        final var constraint1 = forAll();
        final var constraint2 = forAll();
        final var constraint3 = forAll();
        final var constraint4 = forAll();
        final var constraint5 = forAll();
        @SuppressWarnings("unchecked") final var solution
                = defineProblem("testAllocationGroups")
                .withDemandAttributes()
                .withNoDemands()
                .withSupplyAttributes()
                .withNoSupplies()
                .withConstraint(constraint1
                        .withChildren(constraint2)
                        .withChildren(constraint3
                                .withChildren(constraint4)
                                .withChildren(constraint5)))
                .toProblem()
                .asSolution();
        final var testProduct = allocationGroups(solution.constraint());
        assertThat(testProduct.get(0)).containsExactly(constraint1);
        assertThat(testProduct.get(1)).containsExactly(constraint1, constraint2);
        assertThat(testProduct.get(2)).containsExactly(constraint1, constraint3);
        assertThat(testProduct.get(3)).containsExactly(constraint1, constraint3, constraint4);
        assertThat(testProduct.get(4)).containsExactly(constraint1, constraint3, constraint5);
        assertThat(testProduct).hasSize(5);
    }

    @Test
    public void test_allocation_groups_with_different_attributes() {
        final var a = attribute(Integer.class, "a");
        final var b = attribute(Integer.class, "b");
        final var c = attribute(Integer.class, "c");
        final var d = attribute(Integer.class, "d");
        final var solution = defineProblem("test_allocation_groups_with_different_attributes")
                .withDemandAttributes(a, b)
                .withNoDemands()
                .withSupplyAttributes(c, d)
                .withNoSupplies()
                .withConstraint(ForAlls.forEach(a)
                        .withChildren(ForAlls.forEach(b)
                                , ForAlls.forEach(c)
                                        .withChildren(ForAlls.forEach(d))))
                .toProblem()
                .asSolution();
        final var testData = allocationGroups(solution.constraint());
        assertThat(testData).hasSize(4);
        assertThat(testData.get(0)).isEqualTo(list(solution.constraint()));
        assertThat(testData.get(1)).isEqualTo(list(solution.constraint()
                , solution.constraint().childrenView().get(0)));
        assertThat(testData.get(2)).isEqualTo(list(solution.constraint()
                , solution.constraint().childrenView().get(1)));
        assertThat(testData.get(3)).isEqualTo(list(solution.constraint()
                , solution.constraint().childrenView().get(1)
                , solution.constraint().childrenView().get(1)
                        .childrenView().get(0)));
    }
}
