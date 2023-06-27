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
package net.splitcells.gel.data.table;

import net.splitcells.dem.testing.annotations.UnitTest;

import static net.splitcells.dem.data.atom.Integers.requireEqualInts;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.testing.Assertions.requireEquals;
import static net.splitcells.gel.data.database.Databases.database;
import static net.splitcells.gel.data.table.LineWithValues.lineWithValues;
import static net.splitcells.gel.data.table.attribute.AttributeI.attribute;
import static net.splitcells.gel.data.table.attribute.IndexedAttribute.indexedAttribute;

public class LineWithValuesTest {
    @UnitTest
    public void testValues() {
        final var attribute1 = attribute(Integer.class);
        final var attribute2 = attribute(Integer.class);
        final var attribute3 = attribute(Integer.class);
        final var valueTypes = database(attribute1, attribute2, attribute3);
        final var indexedAttribute1 = indexedAttribute(attribute1, valueTypes);
        final var indexedAttribute2 = indexedAttribute(attribute2, valueTypes);
        final var indexedAttribute3 = indexedAttribute(attribute3, valueTypes);
        final var testIndex = 7;
        final var testSubject = lineWithValues(valueTypes, list(1, 2, 3), testIndex);
        requireEquals(testSubject.context(), valueTypes);
        testSubject.values().requireEqualityTo(list(1, 2, 3));
        requireEqualInts(testSubject.index(), testIndex);
        requireEquals(testSubject.value(attribute1), 1);
        requireEquals(testSubject.value(attribute2), 2);
        requireEquals(testSubject.value(attribute3), 3);
        requireEquals(testSubject.value(indexedAttribute1), 1);
        requireEquals(testSubject.value(indexedAttribute2), 2);
        requireEquals(testSubject.value(indexedAttribute3), 3);
    }
}
