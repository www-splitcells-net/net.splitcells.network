/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.testing;

import net.splitcells.dem.lang.annotations.JavaLegacy;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;

import static org.junit.platform.engine.TestExecutionResult.Status.SUCCESSFUL;

@JavaLegacy
public class FailureDetector implements TestExecutionListener {
    public static FailureDetector failureDetector() {
        return new FailureDetector();
    }

    private boolean watchedErrors = false;

    private FailureDetector() {

    }

    @Override
    public void executionFinished(TestIdentifier testIdentifier, TestExecutionResult testExecutionResult) {
        if (SUCCESSFUL != testExecutionResult.getStatus()) {
            watchedErrors = true;
        }
    }

    public boolean hasWatchedErrors() {
        return watchedErrors;
    }
}
