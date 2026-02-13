/*
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.data.table;

import net.splitcells.dem.Dem;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.testing.TestSuiteI;
import net.splitcells.gel.constraint.type.framework.ConstraintMultiThreading;
import net.splitcells.gel.data.view.Line;
import net.splitcells.gel.data.view.attribute.Attribute;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;

import static java.util.stream.IntStream.rangeClosed;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.lang.tree.XmlConfig.xmlConfig;
import static net.splitcells.dem.testing.Assertions.requireThrow;
import static net.splitcells.dem.testing.Assertions.requireEquals;
import static net.splitcells.dem.testing.Assertions.requireNull;
import static net.splitcells.dem.testing.Mocking.anyObject;
import static net.splitcells.dem.testing.TestTypes.UNIT_TEST;
import static net.splitcells.gel.constraint.Constraint.LINE;
import static net.splitcells.gel.data.table.TableIFactory.databaseFactory;
import static net.splitcells.gel.data.table.Tables.table;
import static net.splitcells.gel.data.table.linebased.LineBasedTableFactory.lineBasedDatabaseFactory;
import static net.splitcells.gel.data.view.attribute.AttributeI.attribute;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * TODO Test incorrect types added to the content of a table.
 * <p>
 * TODO Test consistency, when adding or removing many things
 */
public class TableTest extends TestSuiteI {

    public static List<TableFactory> databaseFactories() {
        final var databaseFactories = list(databaseFactory(), lineBasedDatabaseFactory());
        databaseFactories.forEach(df -> df.withAspect(TableMetaAspect::databaseIRef));
        return databaseFactories;
    }

    @Test public void testToFods() {
        final var testSubject = table(listWithValuesOf(attribute(Integer.class)));
        rangeClosed(1, 10).forEach(i -> {
            testSubject.addTranslated(listWithValuesOf(i));
        });
        testSubject.toFods().toXmlString(xmlConfig());
    }

    @Test public void testToReformattedTableWithEmptyInput() {
        final var day = attribute(Integer.class);
        final var task = attribute(String.class);
        final var testSubject = table(list(day, task));
        testSubject.toReformattedTable(list(task), list(day)).requireEqualityTo(list());
    }

    @Test public void testToReformattedTableWithAdvancedInput() {
        final var day = attribute(Integer.class, "day");
        final var timeSlot = attribute(Integer.class, "time slot");
        final var room = attribute(String.class, "room");
        final var testTopic = attribute(String.class, "test topic");
        final var examiner = attribute(String.class, "examiner");
        final var testSubject = table(list(day, timeSlot, room, testTopic, examiner));
        testSubject.addTranslated(list(1, 1, "69", "Algebra", "Lindemann"));
        testSubject.addTranslated(list(1, 1, "70", "Programming", "Linus"));
        testSubject.addTranslated(list(1, 2, "69", "Biology", "Lindemann"));
        testSubject.toReformattedTable(list(room), list(day, timeSlot)).requireEqualityTo(
                list(list("", "", "room", "69", "", "70", "")
                        , list("day", "time slot", "", "test topic", "examiner", "test topic", "examiner")
                        , list("1", "1", "", "Algebra", "Lindemann", "Programming", "Linus")
                        , list("", "2", "", "Biology", "Lindemann", "", "")));
    }

    @Test public void testQueryInitialization() {
        final var index = attribute(Integer.class);
        final var testSubject = table(listWithValuesOf(index));
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

    @Test public void testMultiThreadedQueryInitialization() {
        Dem.process(() -> {
            final var index = attribute(Integer.class);
            final var testSubject = table(listWithValuesOf(index));
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
        }, env -> {
            env.config().withConfigValue(ConstraintMultiThreading.class, true);
        });
    }

    @Tag(UNIT_TEST) @TestFactory @Disabled public Stream<DynamicTest> incorrectly_typed_values_addition_tests() {
        return dynamicTests2(this::test_incorrectly_typed_values_addition_test, databaseFactories());
    }

    public void test_incorrectly_typed_values_addition_test(TableFactory tableFactory) {
        final Attribute<Integer> testAttribute = attribute(Integer.class);
        final Table testSubject = tableFactory.table(list(testAttribute));
        requireThrow(Throwable.class, () -> testSubject.addTranslated(list("")));
    }

    @Tag(UNIT_TEST) @TestFactory public Stream<DynamicTest> incorrectly_sized_values_addition_tests() {
        return dynamicTests2(this::test_incorrectly_sized_values_addition_test, databaseFactories());
    }

    public void test_incorrectly_sized_values_addition_test(TableFactory tableFactory) {
        requireThrow(Throwable.class, () -> tableFactory.table().addTranslated(listWithValuesOf(anyObject())));
    }

    @Tag(UNIT_TEST) @TestFactory public Stream<DynamicTest> single_addition_and_removal_tests() {
        return dynamicTests2(this::test_single_addition_and_removal, databaseFactories());
    }

    public void test_single_addition_and_removal(TableFactory tableFactory) {
        List<?> lineValues = list();
        final var voidDatabase = tableFactory.table();
        voidDatabase.unorderedLines().requireEmpty();
        final var addedLine = voidDatabase.addTranslated(lineValues);
        voidDatabase.unorderedLines().requireSizeOf(1);
        voidDatabase.remove(addedLine);
        voidDatabase.unorderedLines().requireEmpty();
        requireNull(voidDatabase.rawLinesView().get(0));
    }

    @Tag(UNIT_TEST) @TestFactory public Stream<DynamicTest> index_preservation_by_add_tests() {
        return dynamicTests2(this::test_index_preservation_by_add, databaseFactories());
    }

    public void test_index_preservation_by_add(TableFactory tableFactory) {
        final var line = mock(Line.class);
        final var voidDatabase = tableFactory.table();
        when(line.context()).thenReturn(voidDatabase);
        when(line.value(any(Attribute.class))).thenReturn(1);
        when(line.index()).thenReturn(2);
        requireEquals(voidDatabase.addTranslated(list()).index(), 0);
        requireEquals(voidDatabase.add(line).index(), 2);
        requireEquals(voidDatabase.addTranslated(list()).index(), 1);
        requireEquals(voidDatabase.addTranslated(list()).index(), 3);
    }

    @Tag(UNIT_TEST) @TestFactory public Stream<DynamicTest> subscription_tests() {
        return dynamicTests2(this::test_subscriptions, databaseFactories());
    }

    public void test_subscriptions(TableFactory tableFactory) {
        final var additionCounter = list(0);// Mutable integer Object required.
        final var removalCounter = list(0);// Mutable integer Object required.
        final var voidDatabase = tableFactory.table();
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

    @Tag(UNIT_TEST) @TestFactory public Stream<DynamicTest> addTranslated_with_too_many_values_tests() {
        return dynamicTests2(this::test_addTranslated_with_too_many_values, databaseFactories());
    }

    public void test_addTranslated_with_too_many_values(TableFactory tableFactory) {
        requireThrow(Throwable.class, () -> {
            tableFactory.table().addTranslated(list(1));
        });
    }
}
