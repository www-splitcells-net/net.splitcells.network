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
package net.splitcells.dem.testing;

import net.splitcells.dem.utils.ConstructorIllegal;

import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;

/**
 * <p>Avoid tag names with dots, because they cause problems regarding
 * command line.</p>
 * <p>TODO IDEA Use verification or validation instead of testing for tag names
 * in order to clear their meaning.</p>
 */
public final class TestTypes {

    public static final String UNIT_TEST = "testing_unit";
    public static final String INTEGRATION_TEST = "testing_integration";
    public static final String FUNCTIONAL_TEST = "testing_functional";
    /**
     * Such tests may take a lot of time, compared to other test types.
     */
    public static final String CAPABILITY_TEST = "testing_capabilities";
    public static final String BENCHMARK_RUNTIME = "benchmarking_runtime";
    public static final String BENCHMARK_MEMORY_USAGE = "benchmarking_memoryUsage";
    public static final String PROFILING_RUNTIME = "profiling_runtime";
    public static final String PROFILING_MEMORY_USAGE = "profiling_memoryUsage";

    private TestTypes() {
        throw constructorIllegal();
    }

}
