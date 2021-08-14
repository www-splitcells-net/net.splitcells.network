/*
 * Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License,
 * which is available at https://spdx.org/licenses/MIT.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
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
import static net.splitcells.dem.testing.TestTypes.INTEGRATION_TEST;
import static net.splitcells.gel.data.table.attribute.AttributeI.attribute;
import static org.assertj.core.api.Assertions.assertThat;

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
        final var testSubject = new DatabaseI(a, b);
        testSubject.addTranslated(list(1, 2));
        assertThat(testSubject.columnView(a).lookup(1).size()).isEqualTo(1);
        assertThat(testSubject.columnView(a).lookup(3).size()).isEqualTo(0);
        assertThat(testSubject.columnView(a).lookup(1).columnView(b).lookup(2).size())
                .isEqualTo(1);
        assertThat(testSubject
                .columnView(a)
                .lookup(1)
                .columnView(b)
                .lookup(2)
                .rawLinesView()
                .get(0)
        ).isNotNull();
        assertThat(testSubject
                .columnView(a)
                .lookup(1)
                .columnView(b)
                .lookup(3)
                .size()
        ).isEqualTo(0);
        assertThat(testSubject
                .columnView(a)
                .lookup(3)
                .columnView(b)
                .lookup(3)
                .size()
        ).isEqualTo(0);
        assertThat(testSubject
                .columnView(a)
                .lookup(3)
                .columnView(b)
                .lookup(2)
                .size()
        ).isEqualTo(0);
    }

    @Test
    @Tag(INTEGRATION_TEST)
    public void testNestedLookupWithMultipleLines() {
        final var a = attribute(Integer.class, "a");
        final var b = attribute(Integer.class, "b");
        final var testSubject = new DatabaseI(a, b);
        testSubject.addTranslated(list(1, 2));
        testSubject.addTranslated(list(1, 2));
        testSubject.addTranslated(list(2, 1));
        testSubject.addTranslated(list(2, 1));
        testSubject.addTranslated(list(3, 3));
        testSubject.addTranslated(list(1, 3));
        testSubject.addTranslated(list(3, 2));
        assertThat(testSubject.columnView(a).lookup(1).size()).isEqualTo(3);
        assertThat(testSubject
                .columnView(a)
                .lookup(1)
                .columnView(b)
                .lookup(2)
                .size()
        ).isEqualTo(2);
        assertThat(testSubject
                .columnView(a)
                .lookup(1)
                .columnView(b)
                .lookup(2)
                .rawLinesView()
                .get(0)
        ).isNotNull();
        assertThat(testSubject
                .columnView(a)
                .lookup(1)
                .columnView(b)
                .lookup(2)
                .rawLinesView()
                .get(1)
        ).isNotNull();
        assertThat(testSubject
                .columnView(a)
                .lookup(1)
                .columnView(b)
                .lookup(3)
                .size()
        ).isEqualTo(1);
        assertThat(testSubject
                .columnView(a)
                .lookup(1)
                .columnView(b)
                .lookup(4)
                .size()
        ).isEqualTo(0);
        assertThat(testSubject
                .columnView(a)
                .lookup(3)
                .columnView(b)
                .lookup(2)
                .size()
        ).isEqualTo(1);
        assertThat(testSubject
                .columnView(a)
                .lookup(3)
                .columnView(b)
                .lookup(1)
                .size()
        ).isEqualTo(0);
    }

    @Test
    @Tag(INTEGRATION_TEST)
    public void test_nested_lookup_adaption_with_multiple_lines() {
        final var a = attribute(Integer.class, "a");
        final var b = attribute(Integer.class, "b");
        final var testSubject = Databases.database(a, b);
        {
            assertThat(testSubject
                    .columnView(a)
                    .lookup(1)
                    .columnView(b)
                    .lookup(1)
                    .size()
            ).isEqualTo(0);
            assertThat(testSubject
                    .columnView(a)
                    .lookup(1)
                    .columnView(b)
                    .lookup(2)
                    .size()
            ).isEqualTo(0);
            assertThat(testSubject.columnView(a).lookup(1).columnView(b).lookup(3).size()).isEqualTo(0);
            assertThat(testSubject.columnView(a).lookup(1).columnView(b).lookup(4).size()).isEqualTo(0);
            assertThat(testSubject.columnView(a).lookup(3).columnView(b).lookup(2).size()).isEqualTo(0);
            assertThat(testSubject.columnView(a).lookup(3).columnView(b).lookup(1).size()).isEqualTo(0);
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
            assertThat(testSubject.columnView(a).lookup(1).size()).isEqualTo(3);
            assertThat(testSubject.columnView(a).lookup(1).columnView(b).lookup(2).size()).isEqualTo(2);
            assertThat(testSubject.columnView(a).lookup(1).columnView(b).lookup(2).rawLinesView().get(0)).isNotNull();
            assertThat(testSubject.columnView(a).lookup(1).columnView(b).lookup(2).rawLinesView().get(1)).isNotNull();
            assertThat(testSubject.columnView(a).lookup(1).columnView(b).lookup(3).size()).isEqualTo(1);
            assertThat(testSubject.columnView(a).lookup(1).columnView(b).lookup(4).size()).isEqualTo(0);
            assertThat(testSubject.columnView(a).lookup(3).columnView(b).lookup(2).size()).isEqualTo(1);
            assertThat(testSubject.columnView(a).lookup(3).columnView(b).lookup(1).size()).isEqualTo(0);
        }
    }

    @Test
    @Tag(INTEGRATION_TEST)
    public void testLookupByPredicate() {
        final var a = attribute(Integer.class, "a");
        final var b = attribute(Integer.class, "b");
        final var testSubject = new DatabaseI(a, b);
        testSubject.addTranslated(list(1, 2));
        final Predicate<Integer> predicate = aa -> aa == 1;
        assertThat(testSubject.columnView(a).lookup(1).size()).isEqualTo(1);
        assertThat(testSubject.columnView(a).lookup(3).size()).isEqualTo(0);
        assertThat(testSubject.columnView(a).lookup(predicate).size()).isEqualTo(1);
    }

    @Test
    public void testLookupAdaptationToRemoval() {
        final var a = attribute(Integer.class, "a");
        final var b = attribute(Integer.class, "b");
        final var testSubject = Databases.database(list(a, b), list());
        final Supplier<Table> pārbaudesPriekšmetsVeidotajs
                = () -> testSubject.columnView(a)
                .lookup(1)
                .columnView(b)
                .lookup(2);
        assertThat(pārbaudesPriekšmetsVeidotajs.get().getLines()).isEmpty();
        final var firstLine = testSubject.addTranslated(list(1, 1));
        assertThat(pārbaudesPriekšmetsVeidotajs.get().getLines()).isEmpty();
        final var secondLine = testSubject.addTranslated(list(1, 2));
        assertThat(pārbaudesPriekšmetsVeidotajs.get().getLines()).hasSize(1);
        testSubject.remove(firstLine);
        assertThat(pārbaudesPriekšmetsVeidotajs.get().getLines()).hasSize(1);
        testSubject.remove(secondLine);
        assertThat(pārbaudesPriekšmetsVeidotajs.get().getLines()).isEmpty();
    }
}
