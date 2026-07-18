/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.testing;

import net.splitcells.dem.lang.annotations.JavaLegacy;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;

import static net.splitcells.dem.resource.communication.log.Logs.logs;
import static org.junit.platform.engine.TestExecutionResult.Status.SUCCESSFUL;

/**
 * TODO Create and use generic stack trace to String method.
 */
@JavaLegacy
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
            testExecutionResult.getThrowable().ifPresent(t -> {
                logs().fail(t);
                if (t.getCause() != null) {
                    logs().fail(t.getCause());
                }
            });
        }
    }
}
