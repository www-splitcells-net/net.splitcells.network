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
 * IDEA Get values out of properties file.
 */
public final class StaticFlags {
    public static final boolean ENFORCING_UNIT_CONSISTENCY
            = Boolean.parseBoolean
            (System.getProperty("net.splitcells.dem.environment.config.StaticFlags.ENFORCING_UNIT_CONSISTENCY", "true"));
    @Deprecated
    public static final boolean FUZZING = true;
    @Deprecated
    public static final boolean ENFORCING_INTEGRATION_CONSISTENCY = true;
    @Deprecated
    public static final boolean PROFILING_RUNTIME = true;
    @Deprecated
    public static final boolean PROFILING_MEMORY_USAGE = true;
    public static final boolean TELLING_STORY
            = Boolean.parseBoolean
            (System.getProperty("net.splitcells.dem.environment.config.StaticFlags.TELLING_STORY", "true"));
    @Deprecated
    public static final boolean WARNING = true;
    public static final boolean TRACING = true;

    private StaticFlags() {
        throw constructorIllegal();
    }
}