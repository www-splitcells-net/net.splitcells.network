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
