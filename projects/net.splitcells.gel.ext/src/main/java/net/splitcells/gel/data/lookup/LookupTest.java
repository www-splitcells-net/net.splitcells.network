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

import net.splitcells.dem.testing.TestSuiteI;
import net.splitcells.gel.data.database.Database;
import net.splitcells.gel.data.database.DatabaseI;
import net.splitcells.gel.data.database.Databases;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.data.table.column.Column;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.function.Predicate;
import java.util.function.Supplier;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.testing.Assertions.requireNotNull;
import static net.splitcells.dem.testing.TestTypes.INTEGRATION_TEST;
import static net.splitcells.gel.data.database.DatabaseI.databaseI;
import static net.splitcells.gel.data.table.attribute.AttributeI.attribute;

/**
 * TODO {@link Database} and {@link Column} should not be based on {@link Lookup}.
 * Instead only {@link Lookup} should depend on {@link Database} and {@link Column} as a regular  subscriber,
 * in order to simplify code. Otherwise it is too hard to add an {@link Lookup} without adding code to {@link Database} and {@link Column} too.
 */
public class LookupTest extends TestSuiteI {
    @Test
    @Tag(INTEGRATION_TEST)
    public void testMultipleLookupsSimultaneously() {
        final var a = attribute(Integer.class, "a");
        final var b = attribute(Integer.class, "b");
        final var testSubject = databaseI(a, b);
        testSubject.addTranslated(list(1, 2));
        testSubject.columnView(a).lookup(1).unorderedLines().requireSizeOf(1);
        testSubject.columnView(a).lookup(3).unorderedLines().requireEmpty();
        testSubject.columnView(a).lookup(1).columnView(b).lookup(2)
                .unorderedLines()
                .requireSizeOf(1);
        requireNotNull(testSubject
                .columnView(a)
                .lookup(1)
                .columnView(b)
                .lookup(2)
                .rawLinesView()
                .get(0));
        testSubject
                .columnView(a)
                .lookup(1)
                .columnView(b)
                .lookup(3)
                .unorderedLines()
                .requireEmpty();
        testSubject
                .columnView(a)
                .lookup(3)
                .columnView(b)
                .lookup(3)
                .unorderedLines()
                .requireEmpty();
        testSubject
                .columnView(a)
                .lookup(3)
                .columnView(b)
                .lookup(2)
                .unorderedLines()
                .requireEmpty();
    }

    @Test
    @Tag(INTEGRATION_TEST)
    public void testNestedLookupWithMultipleLines() {
        final var a = attribute(Integer.class, "a");
        final var b = attribute(Integer.class, "b");
        final var testSubject = databaseI(a, b);
        testSubject.addTranslated(list(1, 2));
        testSubject.addTranslated(list(1, 2));
        testSubject.addTranslated(list(2, 1));
        testSubject.addTranslated(list(2, 1));
        testSubject.addTranslated(list(3, 3));
        testSubject.addTranslated(list(1, 3));
        testSubject.addTranslated(list(3, 2));
        testSubject.columnView(a).lookup(1).unorderedLines().requireSizeOf(3);
        testSubject
                .columnView(a)
                .lookup(1)
                .columnView(b)
                .lookup(2)
                .unorderedLines()
                .requireSizeOf(2);
        requireNotNull(testSubject
                .columnView(a)
                .lookup(1)
                .columnView(b)
                .lookup(2)
                .rawLinesView()
                .get(0));
        requireNotNull(testSubject
                .columnView(a)
                .lookup(1)
                .columnView(b)
                .lookup(2)
                .rawLinesView()
                .get(1));
        testSubject
                .columnView(a)
                .lookup(1)
                .columnView(b)
                .lookup(3)
                .unorderedLines()
                .requireSizeOf(1);
        testSubject
                .columnView(a)
                .lookup(1)
                .columnView(b)
                .lookup(4)
                .unorderedLines()
                .requireEmpty();
        testSubject
                .columnView(a)
                .lookup(3)
                .columnView(b)
                .lookup(2)
                .unorderedLines()
                .requireSizeOf(1);
        testSubject
                .columnView(a)
                .lookup(3)
                .columnView(b)
                .lookup(1)
                .unorderedLines()
                .requireEmpty();
    }

    @Test
    @Tag(INTEGRATION_TEST)
    public void test_nested_lookup_adaption_with_multiple_lines() {
        final var a = attribute(Integer.class, "a");
        final var b = attribute(Integer.class, "b");
        final var testSubject = Databases.database(a, b);
        {
            testSubject
                    .columnView(a)
                    .lookup(1)
                    .columnView(b)
                    .lookup(1)
                    .unorderedLines()
                    .requireEmpty();
            testSubject
                    .columnView(a)
                    .lookup(1)
                    .columnView(b)
                    .lookup(2)
                    .unorderedLines()
                    .requireEmpty();
            testSubject.columnView(a).lookup(1).columnView(b).lookup(3).unorderedLines().requireEmpty();
            testSubject.columnView(a).lookup(1).columnView(b).lookup(4).unorderedLines().requireEmpty();
            testSubject.columnView(a).lookup(3).columnView(b).lookup(2).unorderedLines().requireEmpty();
            testSubject.columnView(a).lookup(3).columnView(b).lookup(1).unorderedLines().requireEmpty();
        }
        {
            testSubject.addTranslated(list(1, 2));
            testSubject.addTranslated(list(1, 2));
            testSubject.addTranslated(list(2, 1));
            testSubject.addTranslated(list(2, 1));
            testSubject.addTranslated(list(3, 3));
            testSubject.addTranslated(list(1, 3));
            testSubject.addTranslated(list(3, 2));
        }
        {
            testSubject.columnView(a).lookup(1).unorderedLines().requireSizeOf(3);
            testSubject.columnView(a).lookup(1).columnView(b).lookup(2).unorderedLines().requireSizeOf(2);
            requireNotNull(testSubject.columnView(a).lookup(1).columnView(b).lookup(2).rawLinesView().get(0));
            requireNotNull(testSubject.columnView(a).lookup(1).columnView(b).lookup(2).rawLinesView().get(1));
            testSubject.columnView(a).lookup(1).columnView(b).lookup(3).unorderedLines().requireSizeOf(1);
            testSubject.columnView(a).lookup(1).columnView(b).lookup(4).unorderedLines().requireEmpty();
            testSubject.columnView(a).lookup(3).columnView(b).lookup(2).unorderedLines().requireSizeOf(1);
            testSubject.columnView(a).lookup(3).columnView(b).lookup(1).unorderedLines().requireEmpty();
        }
    }

    @Test
    @Tag(INTEGRATION_TEST)
    public void testLookupByPredicate() {
        final var a = attribute(Integer.class, "a");
        final var b = attribute(Integer.class, "b");
        final var testSubject = databaseI(a, b);
        testSubject.addTranslated(list(1, 2));
        final Predicate<Integer> predicate = aa -> aa == 1;
        testSubject.columnView(a).lookup(1).unorderedLines().requireSizeOf(1);
        testSubject.columnView(a).lookup(3).unorderedLines().requireEmpty();
        testSubject.columnView(a).lookup(predicate).unorderedLines().requireSizeOf(1);
    }

    @Test
    public void testLookupAdaptationToRemoval() {
        final var a = attribute(Integer.class, "a");
        final var b = attribute(Integer.class, "b");
        final var testSubject = Databases.database(list(a, b), list());
        final Supplier<Table> testValues = () -> testSubject.columnView(a)
                .lookup(1)
                .columnView(b)
                .lookup(2);
        testValues.get().unorderedLines().requireEmpty();
        final var firstLine = testSubject.addTranslated(list(1, 1));
        testValues.get().unorderedLines().requireEmpty();
        final var secondLine = testSubject.addTranslated(list(1, 2));
        testValues.get().unorderedLines().requireSizeOf(1);
        testSubject.remove(firstLine);
        testValues.get().unorderedLines().requireSizeOf(1);
        testSubject.remove(secondLine);
        testValues.get().unorderedLines().requireEmpty();
    }
}
