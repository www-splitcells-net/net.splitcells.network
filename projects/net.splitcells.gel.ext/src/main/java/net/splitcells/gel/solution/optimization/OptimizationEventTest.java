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
package net.splitcells.gel.solution.optimization;


import net.splitcells.gel.data.table.Databases;
import org.junit.jupiter.api.Test;

import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.gel.solution.optimization.OptimizationEvent.optimizationEvent;
import static net.splitcells.gel.data.view.LinePointerI.linePointer;
import static net.splitcells.gel.data.view.attribute.AttributeI.attribute;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class OptimizationEventTest {
    @Test
    public void test_equals_ofEqualPair() {
        final var demandValue = 0;
        final var supplyValue = 1;
        final var tableA = Databases.table(attribute(Integer.class));
        final var tableB = Databases.table(attribute(Integer.class));
        final var testSubjectA = optimizationEvent
                (StepType.REMOVAL
                        , linePointer(tableA, demandValue)
                        , linePointer(tableB, supplyValue));
        final var testSubjectB = optimizationEvent
                (StepType.REMOVAL
                        , linePointer(tableA, demandValue)
                        , linePointer(tableB, supplyValue));
        assertThat(testSubjectA).isEqualTo(testSubjectB);
        assertThat(setOfUniques(testSubjectA)).contains(testSubjectB);
    }

    @Test
    public void test_equals_ofPairWithDifferentSupplies() {
        final var demandValue = 0;
        final var supplyValueA = 1;
        final var supplyValueB = 2;
        final var tableA = Databases.table(attribute(Integer.class));
        final var tableB = Databases.table(attribute(Integer.class));
        final var testSubjectA = optimizationEvent(StepType.REMOVAL, linePointer(tableA, demandValue),
                linePointer(tableB, supplyValueA));
        final var testSubjectB = optimizationEvent(StepType.REMOVAL, linePointer(tableA, demandValue),
                linePointer(tableB, supplyValueB));
        assertThat(testSubjectA).isNotEqualTo(testSubjectB);
        assertThat(setOfUniques(testSubjectA)).doesNotContain(testSubjectB);
    }
}
