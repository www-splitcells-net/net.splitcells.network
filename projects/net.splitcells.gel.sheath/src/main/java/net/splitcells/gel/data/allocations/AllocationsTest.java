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
package net.splitcells.gel.data.allocations;

import net.splitcells.dem.testing.TestSuiteI;
import net.splitcells.gel.data.database.Databases;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.testing.TestTypes.UNIT_TEST;
import static net.splitcells.gel.data.allocation.Allocationss.allocations;
import static net.splitcells.gel.data.table.attribute.AttributeI.attribute;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AllocationsTest extends TestSuiteI {

    @Tag(UNIT_TEST)
    @Test
    public void test_subscribe_to_afterAdditions() {
        final var demands = Databases.database();
        final var supplies = Databases.database();
        final var allocations = allocations("test", demands, supplies);
        allocations.subscribeToAfterAdditions(
                pieķiršana -> assertThat(allocations.demandOfAllocation(pieķiršana)).isNotNull());
        allocations.allocate
                (demands.addTranslated(list())
                        , supplies.addTranslated(list()));
    }

    @Tag(UNIT_TEST)
    @Test
    public void test_subscriber_to_beforeRemoval() {
        final var demands = Databases.database();
        final var supplies = Databases.database();
        final var allocations = allocations("test", demands, supplies);
        allocations.subscriberToBeforeRemoval
                (pieķiršana -> assertThat(allocations.demandOfAllocation(pieķiršana)).isNotNull());
        allocations.remove(
                allocations.allocate
                        (demands.addTranslated(list())
                                , supplies.addTranslated(list()))
        );
    }

    @Tag(UNIT_TEST)
    @Test
    public void test_subscriber_to_afterRemoval() {
        assertThrows(Exception.class, () -> {
            final var demands = Databases.database();
            final var supplies = Databases.database();
            final var allocations = allocations("test", demands, supplies);
            allocations.subscriberToAfterRemoval
                    (pieķiršana -> assertThat(allocations.demandOfAllocation(pieķiršana)));
            allocations.remove(
                    allocations.allocate
                            (demands.addTranslated(list())
                                    , supplies.addTranslated(list()))
            );
        });
    }

    @Tag(UNIT_TEST)
    @Test
    public void test_allocate_and_remove() {
        final var demandAttribute = attribute(Double.class);
        final var demands = Databases.database(demandAttribute);
        demands.addTranslated(list(1.0));
        final var supplyAttribute = attribute(Integer.class);
        final var supplies = Databases.database(supplyAttribute);
        supplies.addTranslated(list(2));
        final var testSubject = allocations("test", demands, supplies);
        assertThat(testSubject.headerView())
                .containsExactly(demands.headerView().get(0), supplies.headerView().get(0));
        assertThat(testSubject.demands().size()).isEqualTo(1);
        assertThat(testSubject.supplies().size()).isEqualTo(1);
        demands.addTranslated(list(3.0));
        supplies.addTranslated(list(4));
        assertThat(testSubject.demands().size()).isEqualTo(2);
        assertThat(testSubject.supplies().size()).isEqualTo(2);
        final var firstAllocation = testSubject
                .allocate(testSubject.demands().rawLinesView().get(0)
                        , testSubject.supplies().rawLinesView().get(0));
        assertThat(firstAllocation.value(demandAttribute)).isEqualTo(1.0);
        assertThat(firstAllocation.value(supplyAttribute)).isEqualTo(2);
        final var secondAllocation = testSubject
                .allocate(testSubject.demands().rawLinesView().get(1)
                        , testSubject.supplies().rawLinesView().get(1));
        assertThat(secondAllocation.value(demandAttribute)).isEqualTo(3.0);
        assertThat(secondAllocation.value(supplyAttribute)).isEqualTo(4);
        assertThat(testSubject.size()).isEqualTo(2);
        testSubject.remove(firstAllocation);
        assertThat(testSubject.size()).isEqualTo(1);
    }
}
