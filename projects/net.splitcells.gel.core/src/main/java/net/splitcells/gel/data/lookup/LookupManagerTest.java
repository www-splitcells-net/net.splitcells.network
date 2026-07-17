/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.data.lookup;

import net.splitcells.dem.testing.annotations.BenchmarkTest;
import net.splitcells.dem.testing.annotations.UnitTest;

import static java.util.stream.IntStream.range;
import static java.util.stream.IntStream.rangeClosed;
import static net.splitcells.dem.data.atom.Bools.require;
import static net.splitcells.dem.data.atom.Bools.requireNot;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.gel.data.lookup.LookupManager.DEFAULT_MIN_STRATEGY_TIME;
import static net.splitcells.gel.data.lookup.LookupManager.lookupManager;
import static net.splitcells.gel.data.table.Tables.table;
import static net.splitcells.gel.data.view.attribute.AttributeI.attribute;

public class LookupManagerTest {
    @UnitTest
    public void testIsPersistedLookupActive() {
        final var index = attribute(Integer.class);
        final var testData = table("test data", index);
        rangeClosed(0, 10).forEach(i -> {
            testData.addTranslated(listWithValuesOf(i));
        });
        final var testSubject = lookupManager(testData, index, 3);
        requireNot(testSubject.isPersistedLookupActive());
        testSubject.lookup(0).unorderedLines().requireEquals(list(testData.orderedLine(0)));
        requireNot(testSubject.isPersistedLookupActive());
        testSubject.lookup(1).unorderedLines().requireEquals(list(testData.orderedLine(1)));
        requireNot(testSubject.isPersistedLookupActive());
        testSubject.lookup(2).unorderedLines().requireEquals(list(testData.orderedLine(2)));
        require(testSubject.isPersistedLookupActive());
        testSubject.lookup(3).unorderedLines().requireEquals(list(testData.orderedLine(3)));
        require(testSubject.isPersistedLookupActive());
    }

    @BenchmarkTest
    public void testIsPersistedLookupActiveBenchmark() {
        final var index = attribute(Integer.class);
        final var testData = table("test data", index);
        final var testSubject = lookupManager(testData, index);
        range(0, DEFAULT_MIN_STRATEGY_TIME).forEach(i -> {
            testData.addTranslated(listWithValuesOf(i));
        });
        range(0, DEFAULT_MIN_STRATEGY_TIME).forEach(i -> {
            requireNot(testSubject.isPersistedLookupActive());
            testSubject.lookup(0).unorderedLines().requireEquals(list(testData.orderedLine(0)));
        });
        require(testSubject.isPersistedLookupActive());
    }
}
