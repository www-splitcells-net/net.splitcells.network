/*
 * Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License,
 * which is available at https://spdx.org/licenses/MIT.html.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License, version 2
 * or any later versions with the GNU Classpath Exception which is
 * available at https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT OR GPL-2.0-or-later WITH Classpath-exception-2.0
 */
package net.splitcells.gel.data.database;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.testing.TestSuiteI;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.attribute.Attribute;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestFactory;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Stream;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.testing.Mocking.anyObject;
import static net.splitcells.dem.testing.Mocking.anyString;
import static net.splitcells.dem.testing.TestTypes.UNIT_TEST;
import static net.splitcells.gel.data.table.attribute.AttributeI.attribute;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * TODO Test incorrect types added to the content of a table.
 * <p>
 * TODO Test consistency, when adding or removing many things
 */
public class DatabaseTest extends TestSuiteI {
    @Tag(UNIT_TEST)
    @TestFactory
    public Stream<DynamicTest> incorrectly_typed_values_addition_tests() {
        return dynamicTests2(this::test_incorrectly_typed_values_addition_test, DatabaseSF.testDatabaseFactory());
    }

    public void test_incorrectly_typed_values_addition_test(Function<List<Attribute<?>>, Database> databaseConstructor) {
        final Attribute<Integer> testAttribute = attribute(Integer.class, anyString());
        final Database testSubject = databaseConstructor.apply(list(testAttribute));
        assertThrows(Throwable.class, () -> testSubject.addTranslated(list("")));
    }

    @Tag(UNIT_TEST)
    @TestFactory
    public Stream<DynamicTest> incorrectly_sized_values_addition_tests() {
        return dynamicTests2(this::test_incorrectly_sized_values_addition_test, DatabaseSF.emptyDatabase());
    }

    public void test_incorrectly_sized_values_addition_test(Database voidDatabase) {
        assertThrows(Throwable.class, () -> voidDatabase.addTranslated(listWithValuesOf(anyObject())));
    }

    @Tag(UNIT_TEST)
    @TestFactory
    public Stream<DynamicTest> single_addition_and_removal_tests() {
        return dynamicTests2(this::test_single_addition_and_removal, DatabaseSF.emptyDatabase());
    }

    public void test_single_addition_and_removal(Database voidDatabase) {
        List<?> lineValues = list();
        assertThat(voidDatabase.isEmpty());
        final var addedLine = voidDatabase.addTranslated(lineValues);
        assertThat(voidDatabase.size()).isEqualTo(1);
        voidDatabase.remove(addedLine);
        assertThat(voidDatabase.isEmpty());
        assertThat(voidDatabase.rawLinesView().get(0)).isNull();
    }

    @Tag(UNIT_TEST)
    @TestFactory
    public Stream<DynamicTest> index_preservation_by_add_tests() {
        return dynamicTests2(this::test_index_preservation_by_add, DatabaseSF.emptyDatabase());
    }

    public void test_index_preservation_by_add(Database voidDatabase) {
        final var line = mock(Line.class);
        final var context = voidDatabase;
        when(line.context()).thenReturn(context);
        when(line.value(any())).thenReturn(1);
        when(line.index()).thenReturn(2);
        assertThat(voidDatabase.addTranslated(list()).index()).isEqualTo(0);
        assertThat(voidDatabase.add(line).index()).isEqualTo(2);
        assertThat(voidDatabase.addTranslated(list()).index()).isEqualTo(1);
        assertThat(voidDatabase.addTranslated(list()).index()).isEqualTo(3);
    }

    @Tag(UNIT_TEST)
    @TestFactory
    public Stream<DynamicTest> subscription_tests() {
        return dynamicTests2(this::test_subscriptions, DatabaseSF.emptyDatabase());
    }

    public void test_subscriptions(Database voidDatabase) {
        final var additionCounter = new AtomicInteger(0);// Mutable integer Object required.
        final var removalCounter = new AtomicInteger(0);// Mutable integer Object required.
        voidDatabase.subscribeToAfterAdditions(additionOf -> additionCounter.incrementAndGet());
        voidDatabase.subscriberToBeforeRemoval(removalOf -> removalCounter.incrementAndGet());
        test_single_addition_and_removal(voidDatabase);
        assertThat(additionCounter).hasValue(1);
        assertThat(removalCounter).hasValue(1);
    }

    @Tag(UNIT_TEST)
    @TestFactory
    public Stream<DynamicTest> addTranslated_with_too_many_values_tests() {
        return dynamicTests2(this::test_addTranslated_with_too_many_values, DatabaseSF.emptyDatabase());
    }

    public void test_addTranslated_with_too_many_values(Database voidDatabase) {
        assertThrows(Throwable.class, () -> {
            voidDatabase.addTranslated(list(1));
        });
    }
}
