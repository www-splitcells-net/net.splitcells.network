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
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.testing.TestSuiteI;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.attribute.Attribute;
import org.junit.jupiter.api.Disabled;
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
import static net.splitcells.gel.data.database.DatabaseIFactory.databaseFactory;
import static net.splitcells.gel.data.database.Databases.database;
import static net.splitcells.gel.data.database.linebased.LineBasedDatabaseFactory.lineBasedDatabaseFactory;
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

    public static List<DatabaseFactory> databaseFactories() {
        final var databaseFactories = list(databaseFactory(), lineBasedDatabaseFactory());
        databaseFactories.forEach(df -> df.withAspect(DatabaseMetaAspect::databaseIRef));
        return databaseFactories;
    }

    @Test
    public void testToReformattedTableWithEmptyInput() {
        final var day = attribute(Integer.class);
        final var task = attribute(String.class);
        final var testSubject = database(list(day, task));
        testSubject.toReformattedTable(list(task), list(day)).requireEqualityTo(list());
    }

    @Test
    public void testToReformattedTableWithAdvancedInput() {
        final var day = attribute(Integer.class, "day");
        final var timeSlot = attribute(Integer.class, "time slot");
        final var room = attribute(String.class, "room");
        final var testTopic = attribute(String.class, "test topic");
        final var examiner = attribute(String.class, "examiner");
        final var testSubject = database(list(day, timeSlot, room, testTopic, examiner));
        testSubject.addTranslated(list(1, 1, "69", "Algebra", "Lindemann"));
        testSubject.addTranslated(list(1, 1, "70", "Programming", "Linus"));
        testSubject.addTranslated(list(1, 2, "69", "Biology", "Lindemann"));
        testSubject.toReformattedTable(list(room), list(day, timeSlot)).requireEqualityTo(
                list(list("day", "time slot", "room", "69", "", "70", "")
                        , list("1", "1", "", "Algebra", "Lindemann", "Programming", "Linus")
                        , list("", "2", "", "Biology", "Lindemann", "", "")));
    }

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
    @Disabled
    public Stream<DynamicTest> incorrectly_typed_values_addition_tests() {
        return dynamicTests2(this::test_incorrectly_typed_values_addition_test, databaseFactories());
    }

    public void test_incorrectly_typed_values_addition_test(DatabaseFactory databaseFactory) {
        final Attribute<Integer> testAttribute = attribute(Integer.class);
        final Database testSubject = databaseFactory.database(list(testAttribute));
        assertThrows(Throwable.class, () -> testSubject.addTranslated(list("")));
    }

    @Tag(UNIT_TEST)
    @TestFactory
    public Stream<DynamicTest> incorrectly_sized_values_addition_tests() {
        return dynamicTests2(this::test_incorrectly_sized_values_addition_test, databaseFactories());
    }

    public void test_incorrectly_sized_values_addition_test(DatabaseFactory databaseFactory) {
        assertThrows(Throwable.class, () -> databaseFactory.database().addTranslated(listWithValuesOf(anyObject())));
    }

    @Tag(UNIT_TEST)
    @TestFactory
    public Stream<DynamicTest> single_addition_and_removal_tests() {
        return dynamicTests2(this::test_single_addition_and_removal, databaseFactories());
    }

    public void test_single_addition_and_removal(DatabaseFactory databaseFactory) {
        List<?> lineValues = list();
        final var voidDatabase = databaseFactory.database();
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
        return dynamicTests2(this::test_index_preservation_by_add, databaseFactories());
    }

    public void test_index_preservation_by_add(DatabaseFactory databaseFactory) {
        final var line = mock(Line.class);
        final var voidDatabase = databaseFactory.database();
        when(line.context()).thenReturn(voidDatabase);
        when(line.value(any(Attribute.class))).thenReturn(1);
        when(line.index()).thenReturn(2);
        requireEquals(voidDatabase.addTranslated(list()).index(), 0);
        requireEquals(voidDatabase.add(line).index(), 2);
        requireEquals(voidDatabase.addTranslated(list()).index(), 1);
        requireEquals(voidDatabase.addTranslated(list()).index(), 3);
    }

    @Tag(UNIT_TEST)
    @TestFactory
    public Stream<DynamicTest> subscription_tests() {
        return dynamicTests2(this::test_subscriptions, databaseFactories());
    }

    public void test_subscriptions(DatabaseFactory databaseFactory) {
        final var additionCounter = list(0);// Mutable integer Object required.
        final var removalCounter = list(0);// Mutable integer Object required.
        final var voidDatabase = databaseFactory.database();
        voidDatabase.subscribeToAfterAdditions(additionOf -> additionCounter.set(0, additionCounter.get(0) + 1));
        voidDatabase.subscribeToBeforeRemoval(removalOf -> removalCounter.set(0, removalCounter.get(0) + 1));
        final List<?> lineValues = list();
        voidDatabase.unorderedLines().requireEmpty();
        final var addedLine = voidDatabase.addTranslated(lineValues);
        voidDatabase.unorderedLines().requireSizeOf(1);
        voidDatabase.remove(addedLine);
        voidDatabase.unorderedLines().requireEmpty();
        requireNull(voidDatabase.rawLinesView().get(0));
        additionCounter.requireContentsOf(1);
        removalCounter.requireContentsOf(1);
    }

    @Tag(UNIT_TEST)
    @TestFactory
    public Stream<DynamicTest> addTranslated_with_too_many_values_tests() {
        return dynamicTests2(this::test_addTranslated_with_too_many_values, databaseFactories());
    }

    public void test_addTranslated_with_too_many_values(DatabaseFactory databaseFactory) {
        assertThrows(Throwable.class, () -> {
            databaseFactory.database().addTranslated(list(1));
        });
    }
}
