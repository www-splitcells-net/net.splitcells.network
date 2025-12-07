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
package net.splitcells.dem.environment.config;

import lombok.val;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.dem.resource.communication.log.LogLevel;

import java.util.Optional;

import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.resource.communication.log.Logs.logs;
import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;
import static net.splitcells.dem.utils.ExecutionException.execException;

/**
 * <p>These static flags provide a simple and centrally controllable way,
 * in order to enable or to disable code via static if blocks.
 * The main goal of these static flags is,
 * to avoid any performance penalties caused by disabled features and program code.</p>
 * <p>This class provides settings, that are set during program initialization.
 * These values are never changed.
 * These settings are used in order to enable or disable optional features.
 * Features that are disabled, do not cause a performance penalty.
 * </p>
 * <p>Performance and reasonable signaling for warnings and errors
 * are prioritized by default settings of the static flags.</p>
 * <p>If one relates this to compiled languages,
 * this can be viewed as a kind of compile time arguments.
 * </p>
 * <p>IDEA Get values out of properties file.</p>
 */
public final class StaticFlags {
    /**
     * This flag is used, in order to disable if blocks in a way,
     * that is not triggering static code analyzers noticing unreachable blocks of code.
     * This is done, by providing a static final variable with a descriptive name,
     * instead of using the constant {@code false}.
     */
    public static final boolean DISABLED_FUNCTIONALITY = false;
    public static final String ENFORCING_UNIT_CONSISTENCY_KEY = "net.splitcells.dem.environment.config.StaticFlags.ENFORCING_UNIT_CONSISTENCY";
    /**
     * TODO Make this private and accessible via a function, in order to have most control.
     * The idea is to change this value during a test, in order to get full unit test coverage.
     */
    @Deprecated
    public static boolean ENFORCING_UNIT_CONSISTENCY
            = Boolean.parseBoolean(System.getProperty(ENFORCING_UNIT_CONSISTENCY_KEY, "true"));
    @Deprecated
    public static final boolean FUZZING = false;
    @Deprecated
    public static final boolean ENFORCING_INTEGRATION_CONSISTENCY = false;
    public static final boolean PROFILING_METHOD_STATISTICS = false;
    @Deprecated
    public static final boolean PROFILING_MEMORY_USAGE = false;
    /**
     * If this is set to true, logging is enabled.
     * This logging tells a story, how the program was executed.
     * So this log enables one, to understand, what was done in general.
     */
    public static final boolean TELLING_STORY
            = Boolean.parseBoolean
            (System.getProperty("net.splitcells.dem.environment.config.StaticFlags.TELLING_STORY", "false"));
    /**
     * <p>This flags determines, if the main code for logging warnings is runnable or not.
     * If set to false, the log system will not even get any warn messages.
     * If set to true, the log system will retrieve warn messages and decide,
     * whether these will be logged or not.
     * This is done via static if blocks and is used in order to maximize performance.
     * </p>
     * <p>TODO Warn, if warnings are disabled.</p>
     */
    public static final boolean WARNING = false;
    /**
     * If this is true, logging is enabled.
     * This logging provides method call information.
     */
    public static final boolean TRACING = false;

    private static final String INLINE_STANDARD_FACTORIES_KEY = "net.splitcells.environment.config.StaticFlags.INLINE_STANDARD_FACTORIES";
    public static final boolean INLINE_STANDARD_FACTORIES = Boolean.parseBoolean(System.getProperty(INLINE_STANDARD_FACTORIES_KEY, "false"));

    private StaticFlags() {
        throw constructorIllegal();
    }

    private static Optional<Tree> warningIfNotMostPerformant() {
        if (ENFORCING_UNIT_CONSISTENCY || TELLING_STORY || WARNING || TRACING || !INLINE_STANDARD_FACTORIES) {
            return Optional.of(tree("The most performant settings are not enabled").withChildren(
                    tree("ENFORCING_UNIT_CONSISTENCY = " + ENFORCING_UNIT_CONSISTENCY)
                    , tree("TELLING_STORY = " + TELLING_STORY)
                    , tree("WARNING = " + WARNING)
                    , tree("INLINE_STANDARD_FACTORIES = " + INLINE_STANDARD_FACTORIES)));
        }
        return Optional.empty();
    }

    /**
     * This method logs, whether the settings have some notable bad side effects.
     */
    public static void logStaticFlags() {
        final var staticFlagsOverridden = tree("static-flags-overridden");
        if (!ENFORCING_UNIT_CONSISTENCY) {
            staticFlagsOverridden.withText("`" + ENFORCING_UNIT_CONSISTENCY_KEY + "` set to `false` and therefore simple errors are not checked.");
        }
        warningIfNotMostPerformant().ifPresent(staticFlagsOverridden::withChild);
        if (staticFlagsOverridden.children().hasElements()) {
            logs().append(staticFlagsOverridden, LogLevel.WARNING);
        }
    }

    /**
     * Sets {@link #ENFORCING_UNIT_CONSISTENCY} during the execution of run to a given value.
     * This is used, in order to test {@link #ENFORCING_UNIT_CONSISTENCY}.
     *
     * @param runValue
     * @param run
     */
    public static void runWithEnforcingUnityConsistency(boolean runValue, Runnable run) {
        try {
            val oldValue = ENFORCING_UNIT_CONSISTENCY;
            ENFORCING_UNIT_CONSISTENCY = runValue;
            run.run();
            ENFORCING_UNIT_CONSISTENCY = oldValue;
        } catch (Exception e) {
            throw execException(e);
        }
    }
}