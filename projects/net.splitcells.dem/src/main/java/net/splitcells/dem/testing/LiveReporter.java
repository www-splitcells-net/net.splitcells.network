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

import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;

import static org.junit.platform.engine.TestExecutionResult.Status.SUCCESSFUL;

/**
 * RENAME
 * <p/>
 * TODO Create and use generic stack trace to String method.
 */
public class LiveReporter implements TestExecutionListener {
    public static LiveReporter liveReporter() {
        return new LiveReporter();
    }

    private LiveReporter() {

    }

    @Override
    public void dynamicTestRegistered(TestIdentifier testIdentifier) {
        System.out.println("Executing: " + testIdentifier.getUniqueId());
    }

    /**
     * TODO Make this somehow optional.
     *
     * @param testIdentifier
     */
    @Override
    public void executionStarted(TestIdentifier testIdentifier) {
        System.out.println("Executing: " + testIdentifier.getUniqueId());
    }

    @Override
    public void executionSkipped(TestIdentifier testIdentifier, String reason) {
        System.out.println("Skipped: " + testIdentifier.getUniqueId());
    }

    @Override
    public void executionFinished(TestIdentifier testIdentifier, TestExecutionResult testExecutionResult) {
        if (SUCCESSFUL == testExecutionResult.getStatus()) {
            if ("true".equals(System.getProperty("net.splitcells.dem.testing.debug"))) {
                System.out.println("Succeeded: " + testIdentifier.getUniqueId());
            }
        } else {
            System.out.println("Failed: " + testIdentifier.getUniqueId());
            if (testExecutionResult.getThrowable().isPresent()) {
                testExecutionResult.getThrowable().get().printStackTrace();
                if (testExecutionResult.getThrowable().get().getCause() != null) {
                    testExecutionResult.getThrowable().get().getCause().printStackTrace();
                }
            }
        }
    }
}
