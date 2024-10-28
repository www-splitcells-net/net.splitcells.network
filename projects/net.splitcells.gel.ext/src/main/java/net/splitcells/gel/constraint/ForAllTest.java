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

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.testing.TestSuiteI;
import net.splitcells.gel.constraint.type.ForAlls;
import net.splitcells.gel.constraint.type.Then;
import net.splitcells.gel.data.table.Tables;
import net.splitcells.gel.data.view.Line;
import net.splitcells.gel.data.view.attribute.AttributeI;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static java.util.stream.IntStream.range;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.testing.TestTypes.UNIT_TEST;
import static net.splitcells.gel.constraint.type.ForAlls.forAll;
import static net.splitcells.gel.data.table.Tables.table;
import static net.splitcells.gel.data.view.attribute.AttributeI.attribute;
import static net.splitcells.gel.rating.type.Cost.cost;
import static net.splitcells.gel.solution.SolutionBuilder.defineProblem;
import static net.splitcells.gel.solution.optimization.primitive.OfflineLinearInitialization.offlineLinearInitialization;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * TODO Test if only incoming groups are used.
 */
public class ForAllTest extends TestSuiteI {
    @Tag(UNIT_TEST)
    @Test
    public void testPropagation() {
        final int size = 3;
        final var lineProducer = Tables.table();
        final var testSubject = forAll();
        assertThat(testSubject.complying()).isEmpty();
        assertThat(testSubject.defying()).isEmpty();
        range(0, size).forEach(i ->
                testSubject.register_addition(lineProducer.addTranslated(list()))
        );
        assertThat(testSubject.complying()).hasSize(size);
        assertThat(testSubject.complying(testSubject.injectionGroup())).hasSize(size);
        assertThat(testSubject.defying()).isEmpty();
    }

    @Tag(UNIT_TEST)
    @Test
    public void testNoGrouping() {
        final var attribute = attribute(Integer.class);
        final var lineSupplier = Tables.table(attribute);
        final var testSubject = ForAlls.forEach(attribute);
        final var validator = forAll();
        testSubject.withChildren(validator);
        final List<Line> lines = list();
        {
            lines.addAll(lineSupplier.addTranslated(list(1))
                    , lineSupplier.addTranslated(list(2))
                    , lineSupplier.addTranslated(list(2))
                    , lineSupplier.addTranslated(list(3))
                    , lineSupplier.addTranslated(list(3))
                    , lineSupplier.addTranslated(list(3)));
            testSubject.register_addition(lines);
        }
        {
            assertThat(testSubject.defying()).isEmpty();
            assertThat(testSubject.complying()).hasSize(lines.size());
            assertThat(validator.defying()).isEmpty();
            assertThat(validator.complying()).isEmpty();
        }
        {
            assertThat(validator.complying(testSubject.groupOf(lines.get(0)))).hasSize(1);
            assertThat(validator.defying(testSubject.groupOf(lines.get(0)))).isEmpty();
            assertThat(validator.complying(testSubject.groupOf(lines.get(1)))).hasSize(2);
            assertThat(validator.defying(testSubject.groupOf(lines.get(1)))).isEmpty();
            assertThat(validator.complying(testSubject.groupOf(lines.get(2)))).hasSize(2);
            assertThat(validator.defying(testSubject.groupOf(lines.get(2)))).isEmpty();
            assertThat(validator.complying(testSubject.groupOf(lines.get(3)))).hasSize(3);
            assertThat(validator.defying(testSubject.groupOf(lines.get(3)))).isEmpty();
            assertThat(validator.complying(testSubject.groupOf(lines.get(4)))).hasSize(3);
            assertThat(validator.defying(testSubject.groupOf(lines.get(4)))).isEmpty();
            assertThat(validator.complying(testSubject.groupOf(lines.get(5)))).hasSize(3);
            assertThat(validator.defying(testSubject.groupOf(lines.get(5)))).isEmpty();
        }
    }

    /**
     * TODO Use this test for all {@link Constraint} implementations.
     */
    @Test
    public void test_consistency_of_compliance_and_defiance() {
        final var a = AttributeI.attribute(Integer.class, "a");
        final var b = AttributeI.attribute(Integer.class, "b");
        @SuppressWarnings("unchecked") final var testSubject
                = defineProblem()
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
                .withConstraint(ForAlls.forEach(a)
                        .withChildren(Then.then(cost(1))))
                .toProblem()
                .asSolution();
        testSubject.optimize(offlineLinearInitialization());
        assertThat(testSubject.constraint().rating()).isEqualTo(cost(6));
        assertThat(testSubject.constraint().complying()).isEmpty();
        assertThat(testSubject.constraint().defying()).hasSize(6);
    }
}
