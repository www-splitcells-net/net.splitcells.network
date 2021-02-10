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
