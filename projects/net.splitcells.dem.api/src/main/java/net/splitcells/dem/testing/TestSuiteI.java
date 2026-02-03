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
package net.splitcells.dem.testing;

import net.splitcells.dem.Dem;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.annotations.JavaLegacy;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.ThrowingConsumer;
import org.junit.platform.engine.reporting.ReportEntry;

import java.util.Collection;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;

/**
 * TODO Support multi threaded test execution.
 */
@Deprecated
@JavaLegacy
public class TestSuiteI implements TestSuite {

    @BeforeAll
    public static void setupEnvironment() {
        // TODO Create and use test Dem which is derived from current Provider and isolates state changes.
        Dem.ensuredInitialized();
    }

    @BeforeEach
    public void prepareTest(TestReporter reporter) {
        reporter.publishEntry(ReportEntryTimeKey.START_TIME.keyString(), ReportEntryTimeKey.START_TIME.currentValue());
    }

    @Override
    public ReportEntry test() {
        throw notImplementedYet();
    }

    @AfterEach
    public void endTest(TestReporter reporter) {
        reporter.publishEntry(ReportEntryTimeKey.END_TIME.keyString(), ReportEntryTimeKey.END_TIME.currentValue());
    }

    public void test(Stream<DynamicTest> tests) {
        tests.forEach(test -> {
            try {
                test.getExecutable().execute();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * RENAME
     *
     * IDEA Make this method static.
     */
    public <T> Stream<DynamicTest> dynamicTests2(ThrowingConsumer<T> test, List<T> inputs) {
        return dynamicTests(test, inputs.stream().map(input -> ImmutablePair.of(input.getClass().getName(), input)));
    }

    /**
     * IDEA Make this method static.
     */
    public <T> Stream<DynamicTest> dynamicTests(ThrowingConsumer<T> test,
                                                   @SuppressWarnings("unchecked") T... inputs) {
        return dynamicTests2(test, list(inputs));
    }

    /**
     * IDEA Make this method static.
     */
    public <T> Stream<DynamicTest> dynamicTests(ThrowingConsumer<T> test,
                                                   @SuppressWarnings("unchecked") Pair<String, T>... inputs) {
        return dynamicTests(test, asList(inputs));
    }

    /**
     * IDEA Make this method static.
     */
    public <T> Stream<DynamicTest> dynamicTests(ThrowingConsumer<T> test, Collection<Pair<String, T>> inputs) {
        return dynamicTests(test, inputs.stream());
    }

    /**
     * IDEA Make this method static.
     */
    public <T> Stream<DynamicTest> dynamicTests(ThrowingConsumer<T> test, Stream<Pair<String, T>> inputs) {
        return inputs.map(input -> DynamicTest.dynamicTest(input.getKey(), () -> {
            test.accept(input.getValue());
        }));
    }

}
