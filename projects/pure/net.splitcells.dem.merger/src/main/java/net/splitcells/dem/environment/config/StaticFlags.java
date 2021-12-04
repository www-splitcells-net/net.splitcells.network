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
package net.splitcells.dem.environment.config;

import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;

/**
 * <p>Provides settings, that are set during program initialization.
 * These values are never changed.
 * These settings are used in order to enable or disable optional features.
 * Features that are disabled do not cause a performance penalty.
 * </p>
 * <p>If one relates this to compiled languages,
 * this can be viewed as a kind of compile time arguments.
 * </p>
 * <p>IDEA Get values out of properties file.</p>
 */
public final class StaticFlags {
    /**
     *
     */
    public static final boolean ENFORCING_UNIT_CONSISTENCY
            = Boolean.parseBoolean
            (System.getProperty("net.splitcells.dem.environment.config.StaticFlags.ENFORCING_UNIT_CONSISTENCY", "true"));
    @Deprecated
    public static final boolean FUZZING = true;
    @Deprecated
    public static final boolean ENFORCING_INTEGRATION_CONSISTENCY = true;
    public static final boolean PROFILING_METHOD_STATISTICS = true;
    @Deprecated
    public static final boolean PROFILING_MEMORY_USAGE = true;
    /**
     * If this is set to true, logging is enabled.
     * This logging tells a story, how the program was executed.
     * So this log enables one, to understand, what was done in general.
     */
    public static final boolean TELLING_STORY
            = Boolean.parseBoolean
            (System.getProperty("net.splitcells.dem.environment.config.StaticFlags.TELLING_STORY", "true"));
    @Deprecated
    public static final boolean WARNING = true;
    /**
     * If this is true, logging is enabled.
     * This logging provides method call information.
     */
    public static final boolean TRACING = true;

    private StaticFlags() {
        throw constructorIllegal();
    }
}