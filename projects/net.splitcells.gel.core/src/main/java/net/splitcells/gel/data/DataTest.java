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
package net.splitcells.gel.data;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.testing.annotations.BenchmarkTest;
import net.splitcells.gel.data.table.TableModificationCounter;

import static java.util.stream.IntStream.range;
import static net.splitcells.dem.Dem.configValue;
import static net.splitcells.dem.Dem.process;
import static net.splitcells.dem.data.atom.DescribedBool.describedBool;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.resource.Time.measureTimeInNanoSeconds;
import static net.splitcells.gel.data.assignment.AssignmentsI.assignments;
import static net.splitcells.gel.data.table.Tables.table;
import static net.splitcells.gel.data.view.attribute.AttributeI.attribute;

public class DataTest {

    @BenchmarkTest
    public void test_runtime_performance_difference_of_assignments_and_tables() {
        final var tableTestTime = measureTimeInNanoSeconds(DataTest::testTableRuntimePerformance);
        final var assignmentsTestTime = measureTimeInNanoSeconds(() -> testAssignmentsRuntimePerformance(false));
        final var assignmentsTestWorstCaseTime = measureTimeInNanoSeconds(() -> testAssignmentsRuntimePerformance(true));
        describedBool(tableTestTime * 3 < assignmentsTestWorstCaseTime, tableTestTime + " * 3 < " + assignmentsTestWorstCaseTime)
                .required();
        describedBool(tableTestTime < assignmentsTestTime, tableTestTime + " < " + assignmentsTestTime)
                .required();
    }

    private static void testTableRuntimePerformance() {
        range(0, 10).forEach(i -> {
            process(() -> {
                final var d = attribute(Integer.class, "d");
                final var s = attribute(Integer.class, "s");
                final var table = table(d, s);
                range(0, 10_000).forEach(j -> {
                    table.addTranslated(list(j, j));
                });
                range(0, 10_000).forEach(table::remove);
            });
        });
    }

    private static void testAssignmentsRuntimePerformance(boolean worstCase) {
        range(0, 10).forEach(i -> {
            process(() -> {
                final var d = attribute(Integer.class, "d");
                final var s = attribute(Integer.class, "s");
                final var demands = table(d);
                final var supplies = table(s);
                final var assignments = assignments("test", demands, supplies);
                if (worstCase) {
                    range(0, 10_000).forEach(j -> {
                        demands.addTranslated(list(j));
                        supplies.addTranslated(list(j));
                    });
                    range(0, 10_000).forEach(j -> {
                        assignments.assign(demands.rawLine(j), supplies.rawLine(j));
                    });
                } else {
                    range(0, 10_000).forEach(j -> {
                        assignments.assign(demands.addTranslated(list(j)), supplies.addTranslated(list(j)));
                    });
                }
                range(0, 10_000).forEach(assignments::remove);
            });
        });
    }

    @BenchmarkTest
    public void test_performance_difference_of_assignments_and_tables() {
        final List<Long> tableTestCounts = list();
        final List<Long> assignmentsTestCounts = list();
        range(0, 1).forEach(i -> {
            testTablePerformance(tableTestCounts);
            testAssignmentsPerformance(assignmentsTestCounts);
        });
        describedBool(tableTestCounts.get(0) * 6 < assignmentsTestCounts.get(0)
                , tableTestCounts.get(0) + " * 6 < " + assignmentsTestCounts.get(0))
                .required();
    }

    private static void testTablePerformance(List<Long> tableTestCounts) {
        process(() -> {
            final var d = attribute(Integer.class, "d");
            final var s = attribute(Integer.class, "s");
            final var table = table(d, s);
            range(0, 10_000).forEach(i -> {
                table.addTranslated(list(i, i));
            });
            tableTestCounts.add(configValue(TableModificationCounter.class).sumCounter().currentCount());
        }, env -> env.config().withInitedOption(TableModificationCounter.class));
    }

    private static void testAssignmentsPerformance(List<Long> tableTestCounts) {
        process(() -> {
            final var d = attribute(Integer.class, "d");
            final var s = attribute(Integer.class, "s");
            final var demands = table(d);
            final var supplies = table(s);
            final var assignments = assignments("test", demands, supplies);
            range(0, 10_000).forEach(i -> {
                assignments.assign(demands.addTranslated(list(i)), supplies.addTranslated(list(i)));
            });
            tableTestCounts.add(configValue(TableModificationCounter.class).sumCounter().currentCount());
        }, env -> env.config().withInitedOption(TableModificationCounter.class));
    }
}
