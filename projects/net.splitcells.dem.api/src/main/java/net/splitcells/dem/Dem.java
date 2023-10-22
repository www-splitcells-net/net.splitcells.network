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
package net.splitcells.dem;

import net.splitcells.dem.environment.Environment;
import net.splitcells.dem.environment.EnvironmentI;
import net.splitcells.dem.environment.EnvironmentV;
import net.splitcells.dem.environment.config.EndTime;
import net.splitcells.dem.environment.config.framework.ConfigurationV;
import net.splitcells.dem.environment.config.framework.Option;
import net.splitcells.dem.lang.annotations.JavaLegacyArtifact;
import net.splitcells.dem.resource.communication.log.Domsole;
import net.splitcells.dem.resource.communication.log.MessageFilter;

import java.security.Permission;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.concurrent.Semaphore;
import java.util.function.Consumer;

import static net.splitcells.dem.ProcessResult.processResult;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.environment.config.StaticFlags.logStaticFlags;
import static net.splitcells.dem.resource.communication.log.Domsole.domsole;
import static net.splitcells.dem.resource.communication.log.LogLevel.UNKNOWN_ERROR;
import static net.splitcells.dem.utils.ExecutionException.executionException;
import static net.splitcells.dem.utils.reflection.ClassesRelated.callerClass;

/**
 * <p>This is the starting point of all process definitions.
 * For a process it defines the program that is executed and the environment in which it is executed.
 * <p/>
 * <p>One of the main goals is to control side effects caused by the program to the environment.
 * This is done, by having 1 and only one variable representing the state of the environment
 * and passing it through everywhere.
 * </p>
 * <p>TODO Introduce dependency injection for documentation.</p>
 */
@JavaLegacyArtifact
public class Dem {
    /**
     * Currently it would be enough to use a static variable instead.
     * Thread locals are required in order to implement a tree of programs as a cactus stack (https://wiki.c2.com/?CactusStack).
     * It generally allows to execute multiple instances of a Dem program, without having interference between them.
     */
    private static final InheritableThreadLocal<Environment> CURRENT = new InheritableThreadLocal<Environment>();

    /**
     * <p>Pauses/freezes the current thread.</p>
     * <p>TODO This is a hack.</p>
     */
    public static void waitIndefinitely() {
        try {
            new Semaphore(1).acquire(2);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    public static ProcessResult process(Runnable program) {
        return process(program, m -> {
            // Default configured is not changed.
        });
    }

    /**
     * It is assumed, that {@link #initializeProcess(Class, Consumer)} )} or
     * similar is already called before on the current thread.
     *
     * @param program This is the code, that the thread executes.
     */
    public static void executeThread(Runnable program) {
        final var thread = new Thread(program);
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Defines and executes a program.
     * <p>
     * TODO Support stacking.
     * <p>
     * TODO Support cactus stacking.
     */
    public static ProcessResult process(Runnable program, Consumer<Environment> configurator) {
        ProcessResult processResult = processResult();
        /* TODO This does not work with Maven:
         * disallowSystemExit();
         */
        Thread root = new Thread(() -> {
            final var processEnvironment = initializeProcess(program.getClass(), configurator);
            processEnvironment.start();
            try {
                // TOFIX Does not write log file on short programs that throws an exception.
                program.run();
            } catch (Throwable t) {
                // TODO Somehow mark this error specially, so its clear, that this error caused execution failure.
                domsole().appendError(t);
                processResult.hasError(true);
                /** Note that thread should handle all exceptions,
                 * because some are using {@link Thread#getThreadGroup()} and {@link ThreadGroup#},
                 * in order to interpret any {@link Thread} with an uncaught exception,
                 * as a program failure.
                 * This can cause unwanted problems for program integration for example via shell scripts.
                 *
                 * @see https://github.com/mojohaus/exec-maven-plugin/blob/d97517868b0fc7a70abee9eb36d43fca6451766d/src/main/java/org/codehaus/mojo/exec/ExecJavaMojo.java#L351
                 * where Maven exec:java can cause exit code != 0,
                 * if any thread has uncaught exceptions.
                 */
            } finally {
                processEnvironment.config().withConfigValue(EndTime.class, Optional.of(ZonedDateTime.now()));
                processEnvironment.close();
                CURRENT.remove();
            }
            try {
                /**
                 * In some cases the last print operation of {@link Domsole} is not executed, when java exits after this process.
                 * This happened on Ubuntu 20.04.1 LTS, but may not be exclusive to this OS.
                 * This hack is used in order to get the last print.
                 */
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                processResult.hasError(true);
                throw new RuntimeException(e);
            }
        });
        // A thread is used in order to not contaminate the current context/process.
        root.start();
        try {
            root.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return processResult;
    }

    /**
     * REFACTOR name
     */
    private static Environment initializeProcess(Class<?> programRepresentative,
                                                 Consumer<Environment> configurator) {
        final var rVal = EnvironmentI.create(programRepresentative);
        // IDEA Invalidate write access to configuration through down casting after configuration via a wrapper.
        CURRENT.set(rVal);
        configurator.accept(rVal);
        rVal.init();
        // Logging is done, when the logging is set up.
        logStaticFlags();
        return rVal;
    }

    @Deprecated
    public static EnvironmentV ensuredInitialized(Consumer<Environment> configurator) {
        EnvironmentV rVal;
        if (CURRENT.get() == null) {
            rVal = initializeProcess(callerClass(1), configurator);
        } else {
            rVal = CURRENT.get();
        }
        return rVal;
    }

    private static void configureByEnvironment(Environment dem) {
        if ("true".equals(System.getProperty("net.splitcells.mode.build"))) {
            dem
                    .config()
                    .withConfigValue
                            (MessageFilter.class
                                    , logMessage -> logMessage.priority().greaterThanOrEqual(UNKNOWN_ERROR));
        }
    }

    public static EnvironmentV ensuredInitialized() {
        EnvironmentV rVal;
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
     */
    public static EnvironmentV environment() {
        if (null == CURRENT.get()) {
            return ensuredInitialized();
        }
        return CURRENT.get();
    }

    /**
     * This function makes the explicit usage of {@link Dem} easier,
     * by providing a name for the most regularly used functionality,
     * that can be guessed.
     *
     * @return The Configuration Of The Current Environment
     */
    public static ConfigurationV config() {
        return environment().config();
    }

    public static <T> T configValue(Class<? extends Option<T>> key) {
        return environment().config().configValue(key);
    }

    /**
     * <p>TODO Remove calls to {@link System#exit(int)}, because it creates problems.
     * See {@link #disallowSystemExit}.</p>
     * <p>{@link System#exit(int)} should not be used directly.
     * Use this method instead, which notice such calls via appropriate logging.</p>
     *
     * @param exitCode
     */
    @Deprecated
    public static void systemExit(int exitCode) {
        final var exception = executionException("Exiting system.");
        /**
         * The {@link Exception#printStackTrace()} is a mehtod of last resort,
         * in order to get a stack trace,
         * even if {@link Dem} is not initialized or working correctly.
         */
        exception.printStackTrace();
        domsole().appendError(exception);
        System.exit(exitCode);
    }

    /**
     * <p>Maven's Surefire plugin for JUnit tests does not support {@link System#exit(int)}.
     * {@link System#exit(int)} may also cause resource issues.
     * Therefore, it can make sense to disallow such calls.</p>
     * <p>End processes via exceptions or main method returns instead,
     * in order to ensure to cleanly end the program.</p>
     */
    private static void disallowSystemExit() {
        System.setSecurityManager(new SecurityManager() {
            @Override
            public void checkPermission(Permission perm) {
                // Allow everything, as Surefire will have otherwise a problem.
            }

            @Override
            public void checkPermission(Permission perm, Object context) {
                // Allow everything, as Surefire will have otherwise a problem.
            }

            @Override
            public void checkExit(int status) {
                super.checkExit(status);
                throw executionException("System exit is disallowed and not supported: " + status);
            }
        });
    }
}
