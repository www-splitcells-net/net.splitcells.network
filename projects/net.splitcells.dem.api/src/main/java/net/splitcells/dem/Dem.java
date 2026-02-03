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

import net.splitcells.dem.environment.Cell;
import net.splitcells.dem.environment.Environment;
import net.splitcells.dem.environment.EnvironmentI;
import net.splitcells.dem.environment.EnvironmentV;
import net.splitcells.dem.environment.config.EndTime;
import net.splitcells.dem.environment.config.ProgramName;
import net.splitcells.dem.environment.config.framework.ConfigurationV;
import net.splitcells.dem.environment.config.framework.Option;
import net.splitcells.dem.lang.annotations.JavaLegacy;
import net.splitcells.dem.resource.communication.log.LogLevel;
import net.splitcells.dem.resource.communication.log.Logs;
import net.splitcells.dem.resource.communication.log.MessageFilter;
import net.splitcells.dem.utils.ExecutionException;

import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.concurrent.Semaphore;
import java.util.function.Consumer;

import static net.splitcells.dem.ProcessResult.processResult;
import static net.splitcells.dem.data.set.list.Lists.*;
import static net.splitcells.dem.environment.config.StaticFlags.logStaticFlags;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.resource.communication.log.LogLevel.INFO;
import static net.splitcells.dem.resource.communication.log.Logs.logs;
import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;
import static net.splitcells.dem.utils.ExecutionException.execException;
import static net.splitcells.dem.utils.StringUtils.throwableToString;

/**
 * <p>This is the starting point of all process definitions.
 * For a process it defines the program that is executed and the environment in which it is executed.
 * </p>
 * <p>One of the main goals is to control side effects caused by the program to the environment.
 * This is done, by having 1 and only one variable representing the state of the environment
 * and passing it through everywhere.
 * </p>
 * <p>TODO Introduce dependency injection for documentation.</p>
 * <p>TODO Support communication to a running {@link #process(Runnable)}. This could be used,
 * in order to close the process by another process.</p>
 * <p>TODO New threads should have process thread, should have appropriate names,
 * so that debuggers and profiles are easier to use.</p>
 * <p>Maven's Surefire plugin for JUnit tests does not support {@link System#exit(int)}.
 * {@link System#exit(int)} may also cause resource issues.
 * Therefore, it can make sense to disallow such calls.
 * Unfortunately, {@link System#setSecurityManager(SecurityManager)} is deprecated and
 * there are no other means to prevent calls to {@link System#exit(int)} except for code scanning.
 * End processes via exceptions or main method returns instead,
 * in order to ensure to cleanly end the program.</p>
 */
@JavaLegacy
public class Dem {
    public static final String MAVEN_GROUP_ID = "net.splitcells";
    /**
     * Currently it would be enough to use a static variable instead.
     * Thread locals are required in order to implement a tree of programs as a cactus stack (https://wiki.c2.com/?CactusStack).
     * It generally allows to execute multiple instances of a Dem program, without having interference between them.
     */
    private static final InheritableThreadLocal<Environment> CURRENT = new InheritableThreadLocal<>();

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

    public static void executeThread(Class<?> representer, Runnable program) {
        executeThread(representer.getName(), program);
    }

    /**
     * <p>It is assumed, that {@link #initializeProcess(Class, Consumer)} or
     * similar is already called before on the current thread.</p>
     * <p>This thread is set to be a daemon thread.
     * This prevents a system exit code of none 0, when being executed in Maven's exec:java goal.</p>
     *
     * @param program This is the code, that the thread executes.
     */
    public static void executeThread(String name, Runnable program) {
        final var thread = new Thread(program);
        thread.setDaemon(true);
        thread.setName(name);
        thread.setUncaughtExceptionHandler((throwingThread, throwable) -> {
            /**
             * TODO Sometimes {@link Logs#logs()} does not work. Maybe logging is currently not thread safe?
             */
            throwable.printStackTrace();
            logs().fail(throwable);
        });
        thread.start();
    }

    public static ProcessResult process(Runnable program, Class<? extends Cell>... cells) {
        return process(program, env -> listWithValuesOf(cells).forEach(env::withCell));
    }

    /**
     * <p>Provides {@link net.splitcells.dem.environment.resource.Service}s as described by the configurator.
     * In other words, this method executes a program decoratively,
     * by configuring the {@link Environment} and waiting indefinitely,
     * so that {@link net.splitcells.dem.environment.resource.Service}s can provide functionality.</p>
     * <p>TODO Provide any mechanism for each {@link Option} to
     * declare all other {@link Option}s, that are required for its initialization.
     * Currently, this is solved by manually initializing the {@link Option}s in the correct order.</p>
     *
     * @param cells This describes the program's configuration.
     */
    public static void serve(Class<? extends Cell>... cells) {
        final Runnable process;
        try {
            process = lastValueOf(cells).getConstructor().newInstance();
        } catch (Throwable e) {
            throw execException(tree("Could not create runnable for process.")
                            .withProperty("cells", listWithValuesOf(cells).toString())
                    , e);
        }
        process(process, cells);

    }

    /**
     * <p>Defines and executes a program.
     * Any throwable of the program is also logged to {@link System#err},
     * as in some deployments it is unreasonably hard to distinguish between
     * the program crashing or the program not even start.</p>
     * <p>Note that the thread executing the process handles all {@link Throwable},
     * because some programs are using {@link Thread#getThreadGroup()} and {@link ThreadGroup},
     * in order to interpret any {@link Thread} with an uncaught exception,
     * as a program failure.
     * This can cause unwanted problems for program integration for example via shell scripts.
     * See https://github.com/mojohaus/exec-maven-plugin/blob/d97517868b0fc7a70abee9eb36d43fca6451766d/src/main/java/org/codehaus/mojo/exec/ExecJavaMojo.java#L351
     * where Maven exec:java can cause exit code != 0,
     * if any thread has uncaught exceptions.</p>
     * <p>TODO Support stacking instead of {@link #CURRENT}.</p>
     * <p>TODO Support cactus stacking instead of {@link #CURRENT}.</p>
     */
    public static ProcessResult process(Runnable program, Consumer<Environment> configurator) {
        ProcessResult processResult = processResult();
        /* TODO This does not work with Maven:
         * disallowSystemExit();
         */
        Thread root = new Thread(() -> {
            try {
                final var processEnvironment = initializeProcess(program.getClass(), env -> {
                    if (program instanceof Cell programCell) {
                        env.config().withConfigValue(ProgramName.class, programCell.programName());
                    }
                    configurator.accept(env);
                });
                processEnvironment.start();
                try {
                    logs().append("Executing `" + configValue(ProgramName.class) + "`.");
                    // TOFIX Does not write log file on short programs that throws an exception.
                    program.run();
                } catch (Throwable t) {
                    // TODO Somehow mark this error specially, so its clear, that this error caused execution failure.
                    logs().append(tree("`" + configValue(ProgramName.class) + "` has an error.")
                                    .withProperty("Error Message", t.getMessage())
                                    .withProperty("Stack Trace", throwableToString(t))
                            , LogLevel.ERROR);
                    processResult.hasError(true);
                    t.printStackTrace();
                } finally {
                    logs().append("Stopping `" + configValue(ProgramName.class) + "`.");
                    processEnvironment.config().withConfigValue(EndTime.class, Optional.of(ZonedDateTime.now()));
                    processEnvironment.close();
                    CURRENT.remove();
                }
            } catch (Throwable th) {
                processResult.hasError(true);
                throw new RuntimeException(th);
            }
            try {
                /**
                 * In some cases the last print operation of {@link Logs} is not executed, when java exits after this process.
                 * This happened on Ubuntu 20.04.1 LTS, but may not be exclusive to this OS.
                 * This hack is used in order to get the last print.
                 */
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                processResult.hasError(true);
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        });
        root.setName(program.getClass().getPackageName() + "." + program.getClass().getSimpleName());
        // A thread is used in order to not contaminate the current context/process.
        root.start();
        try {
            root.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
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
            rVal = initializeProcess(Dem.class, configurator);
        } else {
            rVal = CURRENT.get();
        }
        return rVal;
    }

    /**
     * During builds, every warning is logged as this is something, that should be fixed by the developer.
     * In other words, everything should be logged, that should be reacted to by the test user,
     * which is a developer by default.
     *
     * @param dem
     */
    private static void configureByEnvironment(Environment dem) {
        if ("true".equals(System.getProperty("net.splitcells.mode.build"))) {
            dem
                    .config()
                    .withConfigValue
                            (MessageFilter.class
                                    , logMessage -> logMessage.priority().greaterThanOrEqual(INFO));
        }
    }

    public static EnvironmentV ensuredInitialized() {
        EnvironmentV rVal;
        if (CURRENT.get() == null) {
            rVal = initializeProcess(Dem.class, dem -> {
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
     * <p>TODO Remove calls to {@link System#exit(int)}, because it creates problems.</p>
     * <p>{@link System#exit(int)} should not be used directly.
     * Use this method instead, which notice such calls via appropriate logging.</p>
     *
     * @param exitCode
     */
    @Deprecated
    public static void systemExit(int exitCode) {
        final var exception = ExecutionException.execException("Exiting system.");
        /**
         * The {@link Exception#printStackTrace()} is a mehtod of last resort,
         * in order to get a stack trace,
         * even if {@link Dem} is not initialized or working correctly.
         */
        exception.printStackTrace();
        logs().fail(exception);
        System.exit(exitCode);
    }

    public static void sleepABit() {
        sleepAtLeast(500);
    }

    /**
     * TODO This method should probably be placed somewhere else.
     *
     * @param milliSeconds
     */
    public static void sleepAtLeast(long milliSeconds) {
        try {
            final var plannedEnd = Instant.now().plusMillis(milliSeconds);
            Instant currentEnd;
            long timeLeft;
            Thread.sleep(milliSeconds);
            do {
                currentEnd = Instant.now();
                if (currentEnd.isAfter(plannedEnd)) {
                    return;
                }
                timeLeft = milliSeconds - Duration.between(currentEnd, plannedEnd).toMillis();
                Thread.sleep(timeLeft);
            } while (timeLeft > 0);
        } catch (Throwable t) {
            Thread.currentThread().interrupt();
            throw execException(t);
        }
    }

    private Dem() {
        throw constructorIllegal();
    }
}
