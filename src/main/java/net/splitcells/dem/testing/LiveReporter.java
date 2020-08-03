package net.splitcells.dem.testing;

import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;

import static org.junit.platform.engine.TestExecutionResult.Status.SUCCESSFUL;

/**
 * RENAME
 */
public class LiveReporter implements TestExecutionListener {
    public static LiveReporter liveReporter() {
        return new LiveReporter();
    }

    private LiveReporter() {

    }

    @Override
    public void executionSkipped(TestIdentifier testIdentifier, String reason) {
        System.out.print("Skipped: " + testIdentifier.getUniqueId());
    }

    @Override
    public void executionFinished(TestIdentifier testIdentifier, TestExecutionResult testExecutionResult) {
        if (SUCCESSFUL == testExecutionResult.getStatus()) {
        	if ("true".equals(System.getProperty("net.splitcells.dem.testing.debug"))) {
				System.out.println("Succeeded: " + testIdentifier.getUniqueId());
			}
        } else {
            if (testExecutionResult.getThrowable().isPresent()) {
                testExecutionResult.getThrowable().get().printStackTrace();
            }
            System.out.println("Failed: " + testIdentifier.getUniqueId());
        }
    }
}
