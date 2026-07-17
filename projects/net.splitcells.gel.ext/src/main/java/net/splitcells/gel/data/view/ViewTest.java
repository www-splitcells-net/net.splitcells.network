/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.data.view;

import net.splitcells.dem.testing.TestSuiteI;
import net.splitcells.gel.data.assignment.Assignments;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.testing.TestTypes.UNIT_TEST;
import static net.splitcells.dem.utils.ExecutionException.execException;
import static net.splitcells.gel.data.table.TableI.tableI;
import static net.splitcells.gel.data.view.attribute.AttributeI.attribute;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public final class ViewTest extends TestSuiteI {

    /**
     * TODO Use {@link Assignments}.
     */
    @Tag(UNIT_TEST)
    @TestFactory
    public Stream<DynamicTest> emptyTableTest() {
        return dynamicTests(this::testEmptyTable, tableI());
    }

    /**
     * TODO Use {@link Assignments}.
     */
    @Tag(UNIT_TEST)
    @TestFactory
    public Stream<DynamicTest> invalid_content_write_access_tests() {
        return dynamicTests(this::test_invalid_content_write_access, tableI());
    }

    private void testEmptyTable(View emptyView) {
        assertThat(emptyView.headerView().isEmpty());
        assertThat(emptyView.rawLinesView().isEmpty());
    }

    private void test_invalid_content_write_access(View emptyView) {
        try {
            emptyView.rawLinesView().add(mock(Line.class));
        } catch (RuntimeException e) {
            return;
        }
        throw execException("Content view should allow write access to content data.");
    }

}
