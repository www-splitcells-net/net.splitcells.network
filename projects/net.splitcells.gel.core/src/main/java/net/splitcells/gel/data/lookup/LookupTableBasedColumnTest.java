/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.data.lookup;

import net.splitcells.dem.testing.annotations.UnitTest;
import net.splitcells.gel.data.view.attribute.AttributeI;

import static java.util.stream.IntStream.range;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.testing.Assertions.requireEquals;
import static net.splitcells.dem.testing.Assertions.requireThrow;
import static net.splitcells.gel.data.lookup.LookupTables.lookupTable;
import static net.splitcells.gel.data.table.Tables.table;

public class LookupTableBasedColumnTest {
    @UnitTest
    public void testSteam() {
        final var attribute = AttributeI.attribute(Integer.class, "attribute");
        final var testData = table("test-data", attribute);
        range(0, 10).forEach(i -> testData.addTranslated(list(i)));
        final var lookupTable = lookupTable(testData, "test-subject");
        final var testSubject = lookupTable.columnsView().get(0);
        testSubject.flow().toList().requireEmpty().requireContentsOf(testSubject.values());
        lookupTable.register(testData.orderedLine(0));
        testSubject.flow().toList().requireContentsOf(0).requireContentsOf(testSubject.values());
        lookupTable.register(testData.orderedLine(1));
        lookupTable.register(testData.orderedLine(2));
        testSubject.flow().toList().requireContentsOf(0, 1, 2).requireContentsOf(testSubject.values());
        lookupTable.removeRegistration(lookupTable.orderedLine(1));
        testSubject.flow().toList().requireContentsOf(0, 2).requireContentsOf(testSubject.values());
        lookupTable.removeRegistration(lookupTable.orderedLine(0));
        lookupTable.removeRegistration(lookupTable.orderedLine(0));
        testSubject.flow().toList().requireEmpty().requireContentsOf(testSubject.values());
    }

    @UnitTest
    public void testGetWithInvalidIndex() {
        final var attribute = AttributeI.attribute(Integer.class, "attribute");
        final var testData = table("test-data", attribute);
        range(0, 10).forEach(i -> testData.addTranslated(list(i)));
        final var lookupTable = lookupTable(testData, "test-subject");
        final var testSubject = lookupTable.columnsView().get(0);
        lookupTable.register(testData.orderedLine(0));
        requireEquals(testSubject.get(0), 0);
        requireThrow(() -> testSubject.get(1));
    }
}
