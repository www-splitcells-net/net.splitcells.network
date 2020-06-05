package net.splitcells.dem;

import net.splitcells.dem.environment.config.DependencyManager;
import net.splitcells.dem.environment.config.DependencyManagerI;
import net.splitcells.dem.environment.config.DependencyManagerV;
import net.splitcells.dem.resource.host.interaction.MessageFilter;

import java.util.function.Consumer;

import static net.splitcells.dem.resource.host.interaction.LogLevel.UNKNOWN_ERROR;
import static net.splitcells.dem.utils.reflection.ClassesRelated.callerClass;

/**
 * This is the starting point of all process definitions.
 * For a process it defines the program that is executed and the environment in which it is executed.
 */
public final class Dem {
    /**
     * Currently it would be enough to use a static variable.
     * Thread locals are required in order to implement a tree of programs as a cactus stack (https://wiki.c2.com/?CactusStack).
     * It generally allows to execute multiple instances of a Dem program, without having interference between them.
     */
    private static final InheritableThreadLocal<DependencyManager> CURRENT = new InheritableThreadLocal<DependencyManager>();

    public static void process(Runnable program) {
        process(program, m -> {
            // Default configured is not changed.
        });
    }

    /**
     * Defines and executes a program.
     * <p>
     * TODO Support stacking.
     * <p>
     * TODO Support cactus stacking.
     */
    public static void process(Runnable program, Consumer<DependencyManager> configurator) {
        Thread root = new Thread(() -> {
            initializeProcess(program.getClass(), configurator);
            try {
                // TOFIX Does not write log file on short programs that throws an exception.
                program.run();
            } finally {
                m().close();
                CURRENT.remove();
            }
        });
        // A thread is used in order to not contaminate the current context/process.
        root.start();
        try {
            root.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * REFACTOR name
     */
    private static DependencyManager initializeProcess(Class<?> programRepresentative,
                                                       Consumer<DependencyManager> configurator) {
        final var rVal = new DependencyManagerI(programRepresentative);
        // IDEA Invalidate write access to configuration through down casting after configuration via a wrapper.
        configurator.accept(rVal);
        CURRENT.set(rVal);
        return rVal;
    }

    @Deprecated
    public static DependencyManagerV ensuredInitialized(Consumer<DependencyManager> configurator) {
        DependencyManagerV rVal;
        if (CURRENT.get() == null) {
            rVal = initializeProcess(callerClass(1), configurator);
        } else {
            rVal = CURRENT.get();
        }
        return rVal;
    }

    private static void configureByEnvironment(DependencyManager dem) {
        if ("true".equals(System.getProperty("net.splitcells.mode.build"))) {
            dem.withConfigValue(MessageFilter.class, logMessage -> logMessage.priority().greaterThanOrEqual(UNKNOWN_ERROR));
        }
    }

    public static DependencyManagerV ensuredInitialized() {
        DependencyManagerV rVal;
        if (CURRENT.get() == null) {
            rVal = initializeProcess(callerClass(1), dem -> {
                configureByEnvironment(dem);
            });
        } else {
            rVal = CURRENT.get();
        }
        return rVal;
    }

    /**
     * TODO If the user does not care, how it is initialized he does not care about
     * output. But this only is true for certain output. Logging level should be
     * WARNING by default.
     *
     */
    public static DependencyManagerV m() {
        if (null == CURRENT.get()) {
            return ensuredInitialized();
        }
        return CURRENT.get();
    }
}
