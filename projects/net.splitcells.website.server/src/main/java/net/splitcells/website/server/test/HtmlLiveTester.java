/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.website.server.test;

import net.splitcells.dem.Dem;
import net.splitcells.dem.environment.resource.ResourceOption;
import net.splitcells.dem.environment.resource.Service;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.dem.resource.communication.log.LogLevel;

import java.util.Optional;

import static java.util.stream.IntStream.range;
import static net.splitcells.dem.Dem.configValue;
import static net.splitcells.dem.Dem.executeThread;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.resource.communication.log.Logs.logs;

/**
 * Executes tests via the GUI of the webserver during {@link Dem#process(Runnable)}.
 * Currently, the testers are never stopped, as there is no need to.
 */
public class HtmlLiveTester implements ResourceOption<Service> {
    @Override
    public Service defaultValue() {
        return new Service() {

            @Override
            public void start() {
                logs().append("Starting `" + HtmlLiveTester.class.getName() + "`.", LogLevel.INFO);
                range(0, configValue(HtmlLiveTesterCount.class)).forEach(i -> {
                    executeTest(i);
                });
            }

            private void executeTest(int testId) {
                executeThread("HtmlLiveTester-" + testId, () -> {
                    try {
                        logs().append("Starting UI test thread " + testId + ".", LogLevel.DEBUG);
                        configValue(HtmlLiveTest.class).run();
                        logs().append("Successfully completing UI test thread " + testId + ".", LogLevel.DEBUG);
                    } catch (Throwable t) {
                        logs().warn("Aborting UI test thread " + testId + " on error.", t);
                        Dem.sleepAtLeast(10_000);
                    } finally {
                        executeTest(testId);
                    }
                });
            }

            @Override
            public void close() {
                // Nothing needs to be done.
            }

            @Override
            public void flush() {
                // Nothing needs to be done.
            }
        };
    }

    @Override public Optional<Tree> serialize(Service currentValue) {
        return Optional.of(tree(getClass() + " is enabled."));
    }
}
