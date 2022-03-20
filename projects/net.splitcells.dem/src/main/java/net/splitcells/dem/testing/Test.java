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
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.TagFilter;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.testing.FailureDetector.failureDetector;
import static net.splitcells.dem.testing.LiveReporter.liveReporter;
import static net.splitcells.dem.testing.TestTypes.FUNCTIONAL_TEST;
import static net.splitcells.dem.testing.TestTypes.INTEGRATION_TEST;
import static net.splitcells.dem.testing.TestTypes.UNIT_TEST;
import static org.junit.platform.engine.discovery.ClassNameFilter.includeClassNamePatterns;
import static org.junit.platform.engine.discovery.DiscoverySelectors.*;
import static org.junit.platform.launcher.TagFilter.includeTags;

/**
 * <p>Executes any test by default.
 * This does not filter tests by names like Maven by default.
 * If a developer wrote a test, than it most likely wants to also execute the test, if it is not stated otherwise.
 * Tests are also not filtered, because the developer might not know, that such a thing happens.
 * He needs at least a warning for such things during test execution.</p>
 * <p>TODO Find tests with incorrect tags.</p>
 * <p>TODO Simplify code by removing duplicate code.</p>
 * <p>TODO Testing without Maven seems to be faster.</p>
 */
public class Test {
    public static void main(String... arg) {
        if (!test()) {
            System.exit(1);
        }
    }

    public static boolean testFunctionality() {
        return testFunctionality(list());
    }

    public static boolean testFunctionality(List<TestExecutionListener> executionListeners) {
        // TODO REMOVE
        System.setProperty("net.splitcells.mode.build", "true");
        Dem.ensuredInitialized();
        final var testDiscovery = LauncherDiscoveryRequestBuilder.request()
                .selectors(selectPackage(""))
                .filters(includeTags(FUNCTIONAL_TEST, UNIT_TEST))
                .build();
        final var testExecutor = LauncherFactory.create();
        final var failureDetector = failureDetector();
        testExecutor.discover(testDiscovery);
        testExecutor.execute(testDiscovery
                , executionListeners
                        .withAppended(liveReporter(), failureDetector)
                        .toArray(new TestExecutionListener[0]));
        return !failureDetector.hasWatchedErrors();
    }

    public static boolean testIntegration() {
        // TODO REMOVE
        System.setProperty("net.splitcells.mode.build", "true");
        Dem.ensuredInitialized();
        final var testDiscovery = LauncherDiscoveryRequestBuilder.request()
                .selectors(selectPackage(""))
                .filters(includeTags(INTEGRATION_TEST))
                .build();
        final var testExecutor = LauncherFactory.create();
        final var failureDetector = failureDetector();
        testExecutor.discover(testDiscovery);
        testExecutor.execute(testDiscovery, liveReporter(), failureDetector);
        return !failureDetector.hasWatchedErrors();
    }

    public static boolean testUnits() {
        // TODO REMOVE
        System.setProperty("net.splitcells.mode.build", "true");
        Dem.ensuredInitialized();
        final var testDiscovery = LauncherDiscoveryRequestBuilder.request()
                .selectors(selectPackage(""))
                .filters(includeTags("none() | " + UNIT_TEST))
                .build();
        final var testExecutor = LauncherFactory.create();
        final var failureDetector = failureDetector();
        testExecutor.discover(testDiscovery);
        testExecutor.execute(testDiscovery, liveReporter(), failureDetector);
        return !failureDetector.hasWatchedErrors();
    }

    public static boolean test() {
        // TODO REMOVE
        System.setProperty("net.splitcells.mode.build", "true");
        Dem.ensuredInitialized();
        final var testDiscovery = LauncherDiscoveryRequestBuilder.request()
                .selectors(selectPackage(""))
                .build();
        final var testExecutor = LauncherFactory.create();
        final var failureDetector = failureDetector();
        testExecutor.discover(testDiscovery);
        testExecutor.execute(testDiscovery, liveReporter(), failureDetector);
        return !failureDetector.hasWatchedErrors();
    }

    public static boolean test(Class<?> type) {
        System.setProperty("net.splitcells.mode.build", "true");
        Dem.ensuredInitialized();
        final var testDiscovery = LauncherDiscoveryRequestBuilder.request()
                .selectors(selectClass(type))
                .build();
        final var testExecutor = LauncherFactory.create();
        final var failureDetector = failureDetector();
        testExecutor.discover(testDiscovery);
        testExecutor.execute(testDiscovery, liveReporter(), failureDetector);
        return !failureDetector.hasWatchedErrors();
    }

    public static boolean testMethod(Class<?> type, String methodeName) {
        System.setProperty("net.splitcells.mode.build", "true");
        Dem.ensuredInitialized();
        final var testDiscovery = LauncherDiscoveryRequestBuilder.request()
                .selectors(selectMethod(type, methodeName))
                .build();
        final var testExecutor = LauncherFactory.create();
        final var failureDetector = failureDetector();
        testExecutor.discover(testDiscovery);
        testExecutor.execute(testDiscovery, liveReporter(), failureDetector);
        return !failureDetector.hasWatchedErrors();
    }
}
