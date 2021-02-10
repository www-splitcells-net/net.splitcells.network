package net.splitcells.dem.testing;

import net.splitcells.dem.Dem;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;

import static net.splitcells.dem.testing.FailureDetector.failureDetector;
import static net.splitcells.dem.testing.LiveReporter.liveReporter;
import static org.junit.platform.engine.discovery.ClassNameFilter.includeClassNamePatterns;
import static org.junit.platform.engine.discovery.DiscoverySelectors.*;

/**
 * Executes any test by default.
 * This does not filter tests by names like Maven by default.
 * If a developer wrote a test, than it most likely wants to also execute the test, if it is not stated otherwise.
 * Tests are also not filtered, because the developer might not know, that such a thing happens.
 * He needs at least a warning for such things during test execution.
 *
 * TODO Simplify code by removing duplicate code.
 */
public class Test {
    public static void main(String... arg) {
        if (!test()) {
            System.exit(1);
        }
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
