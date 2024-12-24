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
package net.splitcells.gel.data.allocations;

import net.splitcells.dem.testing.Assertions;
import net.splitcells.dem.testing.TestSuiteI;
import net.splitcells.dem.testing.annotations.UnitTest;
import net.splitcells.gel.data.table.Tables;
import net.splitcells.gel.data.view.Line;

import static java.util.stream.IntStream.rangeClosed;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.testing.Assertions.requireThrow;
import static net.splitcells.dem.testing.Assertions.requireEquals;
import static net.splitcells.dem.testing.Assertions.requireNotNull;
import static net.splitcells.gel.data.assignment.Assignmentss.assignments;
import static net.splitcells.gel.data.table.Tables.table;
import static net.splitcells.gel.data.view.attribute.AttributeI.attribute;

public class AssignmentsTest extends TestSuiteI {

    @UnitTest
    public void testMultipleAllocationsPerDemand() {
        final var demandValue = attribute(Integer.class, "demandValue");
        final var supplyValue = attribute(Integer.class, "supplyValue");
        final var demands = Tables.table("demand", demandValue);
        demands.addTranslated(list(0));
        demands.addTranslated(list(1));
        demands.addTranslated(list(2));
        final var supplies = Tables.table("supply", supplyValue);
        supplies.addTranslated(list(0));
        supplies.addTranslated(list(1));
        supplies.addTranslated(list(2));
        final var testSubject = assignments("testMultipleAllocationsPerDemand", demands, supplies);
        testSubject.assign(testSubject.demands().rawLine(0)
                , testSubject.supplies().rawLine(0));
        testSubject.assign(testSubject.demands().rawLine(0)
                , testSubject.supplies().rawLine(0));
        testSubject.assign(testSubject.demands().rawLine(1)
                , testSubject.supplies().rawLine(1));
        testSubject.assign(testSubject.demands().rawLine(0)
                , testSubject.supplies().rawLine(1));
        testSubject.assign(testSubject.demands().rawLine(2)
                , testSubject.supplies().rawLine(2));
        testSubject.assign(testSubject.demands().rawLine(0)
                , testSubject.supplies().rawLine(2));
        testSubject.unorderedLines().requireSizeOf(6);
        final var multipleAllocationsPerDemands = testSubject.assignmentsOfDemand(testSubject.demands().rawLine(0));
        multipleAllocationsPerDemands.requireSetSizeOf(4);
        multipleAllocationsPerDemands
                .mapped(Line::values)
                .requireContentsOf(list(list(0, 0), list(0, 0), list(0, 1), list(0, 2)));
        final var oneAllocationsPerDemands1 = testSubject.assignmentsOfDemand(testSubject.demands().rawLine(1));
        oneAllocationsPerDemands1.requireSetSizeOf(1);
        oneAllocationsPerDemands1
                .mapped(Line::values)
                .requireContentsOf(list(list(1, 1)));
        final var oneAllocationsPerDemands2 = testSubject.assignmentsOfDemand(testSubject.demands().rawLine(2));
        oneAllocationsPerDemands2.requireSetSizeOf(1);
        oneAllocationsPerDemands2
                .mapped(Line::values)
                .requireContentsOf(list(list(2, 2)));
    }

    @UnitTest
    public void testPath() {
        final var demands = Tables.table("demands");
        final var supplies = Tables.table("supplies");
        final var allocations = assignments("test", demands, supplies);

        Assertions.requireEquals(allocations.path(), list("demands", "test"));
    }

    @UnitTest
    public void testRawLinesGrowth() {
        final var demands = Tables.table();
        final var supplies = Tables.table();
        final var allocations = assignments("test", demands, supplies);
        rangeClosed(1, 10).forEach(i -> {
            final var allocation = allocations.assign(demands.addTranslated(list()), supplies.addTranslated(list()));
            allocations.remove(allocation);
        });
        allocations.rawLinesView().requireSizeOf(1);
    }

    @UnitTest
    public void test_subscribe_to_afterAdditions() {
        final var demands = Tables.table();
        final var supplies = Tables.table();
        final var allocations = assignments("test", demands, supplies);
        allocations.subscribeToAfterAdditions(
                allocation -> requireNotNull(allocations.demandOfAssignment(allocation)));
        allocations.assign
                (demands.addTranslated(list())
                        , supplies.addTranslated(list()));
    }

    @UnitTest
    public void test_subscriber_to_beforeRemoval() {
        final var demands = Tables.table();
        final var supplies = Tables.table();
        final var allocations = assignments("test", demands, supplies);
        allocations.subscribeToBeforeRemoval
                (allocation -> requireNotNull(allocations.demandOfAssignment(allocation)));
        allocations.remove(
                allocations.assign
                        (demands.addTranslated(list())
                                , supplies.addTranslated(list()))
        );
    }

    @UnitTest
    public void test_subscriber_to_afterRemoval() {
        requireThrow(Exception.class, () -> {
            final var demands = Tables.table();
            final var supplies = Tables.table();
            final var allocations = assignments("test", demands, supplies);
            allocations.subscribeToAfterRemoval
                    (allocation -> requireNotNull(allocations.demandOfAssignment(allocation)));
            allocations.remove(
                    allocations.assign
                            (demands.addTranslated(list())
                                    , supplies.addTranslated(list()))
            );
        });
    }

    @UnitTest
    public void test_allocate_and_remove() {
        final var demandAttribute = attribute(Double.class);
        final var demands = Tables.table(demandAttribute);
        demands.addTranslated(list(1.0));
        final var supplyAttribute = attribute(Integer.class);
        final var supplies = Tables.table(supplyAttribute);
        supplies.addTranslated(list(2));
        final var testSubject = assignments("test", demands, supplies);
        testSubject.headerView().requireEqualityTo(list(demands.headerView().get(0), supplies.headerView().get(0)));
        testSubject.demands().unorderedLines().requireSizeOf(1);
        testSubject.supplies().unorderedLines().requireSizeOf(1);
        demands.addTranslated(list(3.0));
        supplies.addTranslated(list(4));
        testSubject.demands().unorderedLines().requireSizeOf(2);
        testSubject.supplies().unorderedLines().requireSizeOf(2);
        final var firstAllocation = testSubject
                .assign(testSubject.demands().rawLinesView().get(0)
                        , testSubject.supplies().rawLinesView().get(0));
        requireEquals(firstAllocation.value(demandAttribute), 1.0);
        requireEquals(firstAllocation.value(supplyAttribute), 2);
        final var secondAllocation = testSubject
                .assign(testSubject.demands().rawLinesView().get(1)
                        , testSubject.supplies().rawLinesView().get(1));
        requireEquals(secondAllocation.value(demandAttribute), 3.0);
        requireEquals(secondAllocation.value(supplyAttribute), 4);
        requireEquals(testSubject.size(), 2);
        testSubject.remove(firstAllocation);
        requireEquals(testSubject.size(), 1);
    }
}
