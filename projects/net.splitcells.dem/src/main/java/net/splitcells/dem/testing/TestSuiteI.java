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
package net.splitcells.dem.testing;

import net.splitcells.dem.Dem;
import net.splitcells.dem.data.set.list.List;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.ThrowingConsumer;
import org.junit.platform.engine.reporting.ReportEntry;

import java.util.Collection;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.testing.ReportEntryKey.END_TIME;
import static net.splitcells.dem.testing.ReportEntryKey.START_TIME;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;

/**
 * TODO Support multi threaded test execution.
 */
public class TestSuiteI implements TestSuite {

    @BeforeAll
    public static void setupEnvironment() {
        // TODO Create and use test Dem which is derived from current Provider and isolates state changes.
        Dem.ensuredInitialized();
    }

    @BeforeEach
    public void prepareTest(TestReporter reporter) {
        reporter.publishEntry(START_TIME.keyString(), START_TIME.currentValue());
    }

    @Override
    public ReportEntry test() {
        throw notImplementedYet();
    }

    @AfterEach
    public void endTest(TestReporter reporter) {
        reporter.publishEntry(END_TIME.keyString(), END_TIME.currentValue());
    }

    protected void test(Stream<DynamicTest> tests) {
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
    protected <T> Stream<DynamicTest> dynamicTests2(ThrowingConsumer<T> test, List<T> inputs) {
        return dynamicTests(test, inputs.stream().map(input -> ImmutablePair.of(input.getClass().getName(), input)));
    }

    /**
     * IDEA Make this method static.
     */
    protected <T> Stream<DynamicTest> dynamicTests(ThrowingConsumer<T> test,
                                                   @SuppressWarnings("unchecked") T... inputs) {
        return dynamicTests2(test, list(inputs));
    }

    /**
     * IDEA Make this method static.
     */
    protected <T> Stream<DynamicTest> dynamicTests(ThrowingConsumer<T> test,
                                                   @SuppressWarnings("unchecked") Pair<String, T>... inputs) {
        return dynamicTests(test, asList(inputs));
    }

    /**
     * IDEA Make this method static.
     */
    protected <T> Stream<DynamicTest> dynamicTests(ThrowingConsumer<T> test, Collection<Pair<String, T>> inputs) {
        return dynamicTests(test, inputs.stream());
    }

    /**
     * IDEA Make this method static.
     */
    protected <T> Stream<DynamicTest> dynamicTests(ThrowingConsumer<T> test, Stream<Pair<String, T>> inputs) {
        return inputs.map(input -> DynamicTest.dynamicTest(input.getKey(), () -> {
            test.accept(input.getValue());
        }));
    }

}
