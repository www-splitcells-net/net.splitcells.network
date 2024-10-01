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
package net.splitcells.website.server.test;

import net.splitcells.dem.Dem;
import net.splitcells.dem.environment.resource.ResourceOption;
import net.splitcells.dem.environment.resource.Service;
import net.splitcells.dem.resource.communication.log.LogLevel;

import static java.util.stream.IntStream.range;
import static net.splitcells.dem.Dem.configValue;
import static net.splitcells.dem.Dem.executeThread;
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
                        logs().appendWarning("Aborting UI test thread " + testId + " on error.", t);
                    } finally {
                        Dem.sleepAtLeast(10_000);
                        executeTest(testId);
                    }
                });
            }

            @Override
            public void close() {
            }

            @Override
            public void flush() {
            }
        };
    }
}
