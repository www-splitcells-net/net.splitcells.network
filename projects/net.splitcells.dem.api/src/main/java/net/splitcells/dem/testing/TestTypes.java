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
package net.splitcells.dem.testing;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.utils.ConstructorIllegal;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;

/**
 * <p>Avoid tag names with dots, because they cause problems regarding
 * command line.</p>
 * <p>TODO Clean up test types.</p>
 * <p>TODO IDEA Use verification or validation instead of testing for tag names
 * in order to clear their meaning.</p>
 */
public final class TestTypes {

    public static final String UNIT_TEST = "testing_unit";
    /**
     * Test the integration with external dependencies.
     * These may be tricky on certain platforms and operating systems as things like browser integration are required.
     */
    public static final String INTEGRATION_TEST = "testing_integration";
    /**
     * Such tests may take a lot of time, compared to other test types.
     */
    public static final String CAPABILITY_TEST = "testing_capabilities";
    public static final String BENCHMARK_RUNTIME = "benchmarking_runtime";
    public static final String BENCHMARK_MEMORY_USAGE = "benchmarking_memoryUsage";
    public static final String PROFILING_RUNTIME = "profiling_runtime";
    public static final String PROFILING_MEMORY_USAGE = "profiling_memoryUsage";
    public static final String EXPERIMENTAL_TEST = "experimental_test";

    public static List<String> extensiveTestTags() {
        return list(UNIT_TEST, INTEGRATION_TEST, CAPABILITY_TEST, BENCHMARK_RUNTIME, BENCHMARK_MEMORY_USAGE, PROFILING_RUNTIME, PROFILING_MEMORY_USAGE);
    }

    private TestTypes() {
        throw constructorIllegal();
    }

}
