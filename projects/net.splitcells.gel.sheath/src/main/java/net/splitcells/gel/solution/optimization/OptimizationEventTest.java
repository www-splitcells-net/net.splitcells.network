/*
 * Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License,
 * which is available at https://spdx.org/licenses/MIT.html.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License, version 2
 * or any later versions with the GNU Classpath Exception which is
 * available at https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT OR GPL-2.0-or-later WITH Classpath-exception-2.0
 */
package net.splitcells.gel.solution.optimization;


import net.splitcells.gel.data.database.Databases;
import net.splitcells.gel.solution.optimization.StepType;
import org.junit.jupiter.api.Test;

import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.gel.solution.optimization.OptimizationEvent.optimizationEvent;
import static net.splitcells.gel.data.table.LinePointerI.linePointer;
import static net.splitcells.gel.data.table.attribute.AttributeI.attribute;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class OptimizationEventTest {
    @Test
    public void test_equals_ofEqualPair() {
        final var demandValue = 0;
        final var supplyValue = 1;
        final var tableA = Databases.database(attribute(Integer.class));
        final var tableB = Databases.database(attribute(Integer.class));
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
        final var tableA = Databases.database(attribute(Integer.class));
        final var tableB = Databases.database(attribute(Integer.class));
        final var testSubjectA = optimizationEvent(StepType.REMOVAL, linePointer(tableA, demandValue),
                linePointer(tableB, supplyValueA));
        final var testSubjectB = optimizationEvent(StepType.REMOVAL, linePointer(tableA, demandValue),
                linePointer(tableB, supplyValueB));
        assertThat(testSubjectA).isNotEqualTo(testSubjectB);
        assertThat(setOfUniques(testSubjectA)).doesNotContain(testSubjectB);
    }
}
