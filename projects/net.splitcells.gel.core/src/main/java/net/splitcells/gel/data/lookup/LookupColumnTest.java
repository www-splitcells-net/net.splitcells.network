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
package net.splitcells.gel.data.lookup;

import net.splitcells.dem.testing.annotations.UnitTest;
import net.splitcells.gel.data.view.attribute.AttributeI;

import static java.util.stream.IntStream.range;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.testing.Assertions.requireEquals;
import static net.splitcells.dem.testing.Assertions.requireThrow;
import static net.splitcells.gel.data.lookup.LookupTables.lookupTable;
import static net.splitcells.gel.data.table.Tables.table;

public class LookupColumnTest {
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
