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
package net.splitcells.dem;

import net.splitcells.dem.environment.Environment;
import net.splitcells.dem.environment.EnvironmentI;
import net.splitcells.dem.environment.EnvironmentV;
import net.splitcells.dem.environment.config.EndTime;
import net.splitcells.dem.environment.config.framework.Option;
import net.splitcells.dem.resource.communication.log.Domsole;
import net.splitcells.dem.resource.communication.log.MessageFilter;
import net.splitcells.dem.source.code.SourceCodeCheck;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.function.Consumer;

import static net.splitcells.dem.ProcessResult.processResult;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.resource.communication.interaction.LogLevel.UNKNOWN_ERROR;
import static net.splitcells.dem.utils.reflection.ClassesRelated.callerClass;

/**
 * This is the starting point of all process definitions.
 * For a process it defines the program that is executed and the environment in which it is executed.
 * <p/>
 * One of the main goals is to make the environment of a program as side effect free as possible.
 * This framework does not try to enforce freedom of side effects on the language level,
 * as this is not possible.
 * It just gives tools in order to minimize side effects.
 * This is done, by having 1 and only one variable representing the state of the environment
 * and passing it through everywhere.
 */
public final class Dem {
    /**
     * Currently it would be enough to use a static variable.
     * Thread locals are required in order to implement a tree of programs as a cactus stack (https://wiki.c2.com/?CactusStack).
     * It generally allows to execute multiple instances of a Dem program, without having interference between them.
     */
    private static final InheritableThreadLocal<Environment> CURRENT = new InheritableThreadLocal<Environment>();

    public static ProcessResult process(Runnable program) {
        return process(program, m -> {
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
    public static ProcessResult process(Runnable program, Consumer<Environment> configurator) {
        ProcessResult processResult = processResult();
        Thread root = new Thread(() -> {
            final var processEnvironment = initializeProcess(program.getClass(), configurator);
            try {
                // TOFIX Does not write log file on short programs that throws an exception.
                program.run();
            } catch (Throwable t) {
                // TODO Somehow mark this error specially, so its clear, that this error caused execution failure.
                Domsole.domsole().appendError(t);
                processResult.hasError(true);
                throw t;
            } finally {
                processEnvironment.config().withConfigValue(EndTime.class, Optional.of(ZonedDateTime.now()));
                environment().close();
                CURRENT.remove();
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
        configurator.accept(rVal);
        CURRENT.set(rVal);
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

    public static <T> T configValue(Class<? extends Option<T>> key) {
        return environment().config().configValue(key);
    }

    public static void checkSourceCode() {
        SourceCodeCheck.main();
    }
}
