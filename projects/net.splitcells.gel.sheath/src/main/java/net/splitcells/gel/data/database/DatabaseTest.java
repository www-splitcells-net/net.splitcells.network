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
package net.splitcells.gel.data.database;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.testing.TestSuiteI;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.attribute.Attribute;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.IntStream.rangeClosed;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.testing.Assertions.assertThrows;
import static net.splitcells.dem.testing.Assertions.requireEquals;
import static net.splitcells.dem.testing.Assertions.requireNull;
import static net.splitcells.dem.testing.Mocking.anyObject;
import static net.splitcells.dem.testing.Mocking.anyString;
import static net.splitcells.dem.testing.TestTypes.UNIT_TEST;
import static net.splitcells.gel.constraint.Constraint.LINE;
import static net.splitcells.gel.data.database.Databases.database;
import static net.splitcells.gel.data.table.attribute.AttributeI.attribute;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * TODO Test incorrect types added to the content of a table.
 * <p>
 * TODO Test consistency, when adding or removing many things
 */
public class DatabaseTest extends TestSuiteI {
    @Test
    public void testQueryInitialization() {
        final var index = attribute(Integer.class);
        final var testSubject = database(listWithValuesOf(index));
        rangeClosed(1, 10).forEach(i -> {
            testSubject.addTranslated(listWithValuesOf(i));
        });
        testSubject
                .query()
                .forAllCombinationsOf(listWithValuesOf(index))
                .currentConstraint()
                .lines()
                .columnView(LINE)
                .values()
                .requireSizeOf(10);
        rangeClosed(1, 10).forEach(i -> {
            testSubject.addTranslated(listWithValuesOf(i));
        });
        testSubject
                .query()
                .forAllCombinationsOf(listWithValuesOf(index))
                .currentConstraint()
                .lines()
                .columnView(LINE)
                .values()
                .requireSizeOf(20);
    }

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
        voidDatabase.unorderedLines().requireEmpty();
        final var addedLine = voidDatabase.addTranslated(lineValues);
        voidDatabase.unorderedLines().requireSizeOf(1);
        voidDatabase.remove(addedLine);
        voidDatabase.unorderedLines().requireEmpty();
        requireNull(voidDatabase.rawLinesView().get(0));
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
        requireEquals(voidDatabase.addTranslated(list()).index(), 0);
        requireEquals(voidDatabase.add(line).index(), 2);
        requireEquals(voidDatabase.addTranslated(list()).index(), 1);
        requireEquals(voidDatabase.addTranslated(list()).index(), 3);
    }

    @Tag(UNIT_TEST)
    @TestFactory
    public Stream<DynamicTest> subscription_tests() {
        return dynamicTests2(this::test_subscriptions, DatabaseSF.emptyDatabase());
    }

    public void test_subscriptions(Database voidDatabase) {
        final var additionCounter = list(0);// Mutable integer Object required.
        final var removalCounter = list(0);// Mutable integer Object required.
        voidDatabase.subscribeToAfterAdditions(additionOf -> additionCounter.set(0, additionCounter.get(0) + 1));
        voidDatabase.subscribeToBeforeRemoval(removalOf -> removalCounter.set(0, removalCounter.get(0) + 1));
        test_single_addition_and_removal(voidDatabase);
        additionCounter.requireContentsOf(1);
        removalCounter.requireContentsOf(1);
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
