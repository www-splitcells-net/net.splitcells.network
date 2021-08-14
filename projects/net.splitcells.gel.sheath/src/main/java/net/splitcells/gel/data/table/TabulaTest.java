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
package net.splitcells.gel.data.table;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.testing.TestSuiteI;
import net.splitcells.gel.data.allocation.Allocations;
import net.splitcells.gel.data.database.DatabaseI;
import net.splitcells.gel.data.table.attribute.Attribute;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestFactory;

import java.util.function.Function;
import java.util.stream.Stream;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.testing.Mocking.anyInt;
import static net.splitcells.dem.testing.Mocking.anyString;
import static net.splitcells.dem.testing.TestTypes.UNIT_TEST;
import static net.splitcells.gel.data.table.attribute.AttributeI.attribute;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;

public final class TabulaTest extends TestSuiteI {

    /**
     * TODO Use {@link Allocations}.
     */
    @Tag(UNIT_TEST)
    @TestFactory
    public Stream<DynamicTest> emptyTableTest() {
        return dynamicTests(this::testEmptyTable, new DatabaseI());
    }

    @Tag(UNIT_TEST)
    @TestFactory
    public Stream<DynamicTest> invalid_column_write_access_tests() {
        return dynamicTests2(this::test_invalid_column_write_access, TableSF.testTableFactory());
    }

    /**
     * TODO Use {@link Allocations}.
     */
    @Tag(UNIT_TEST)
    @TestFactory
    public Stream<DynamicTest> invalid_content_write_access_tests() {
        return dynamicTests(this::test_invalid_content_write_access, new DatabaseI());
    }

    private void testEmptyTable(Table emptyTable) {
        assertThat(emptyTable.headerView().isEmpty());
        assertThat(emptyTable.rawLinesView().isEmpty());
    }

    private void test_invalid_column_write_access
            (Function<List<Attribute<? extends Object>>, Table> factory) {
        try {
            final var attribute = attribute(Integer.class, anyString());
            final var testSubject = factory.apply(list(attribute));
            testSubject.columnView(attribute).add(anyInt());
        } catch (UnsupportedOperationException e) {
            return;
        }
        fail("Column view should not allow write access to column data.");
    }

    private void test_invalid_content_write_access(Table emptyTable) {
        try {
            emptyTable.rawLinesView().add(mock(Line.class));
        } catch (RuntimeException e) {
            return;
        }
        fail("Content view should allow write access to content data.");
    }

}
