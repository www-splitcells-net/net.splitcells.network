/*
 * Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License,
 * which is available at https://spdx.org/licenses/MIT.html.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License, version 2
 * or any later versions with the GNU Classpath Exception which is
 * available at https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT OR GPL-2.0-or-later WITH Classpath-exception-2.0
 */
package net.splitcells.dem.testing;

import net.splitcells.dem.utils.ConstructorIllegal;

public final class TestTypes {

    public static final String UNIT_TEST = "testing.unit";
    public static final String INTEGRATION_TEST = "testing.integration";
    public static final String FUNCTIONAL_TEST = "testing.functional";
    @Deprecated
    public static final String CAPABILITY_TEST = "testing.capability";
    public static final String BENCHMARK_RUNTIME = "benchmarking.runtime";
    public static final String BENCHMARK_MEMORY_USAGE = "benchmarking.memoryUsage";
    public static final String PROFILING_RUNTIME = "profiling.runtime";
    public static final String PROFILING_MEMORY_USAGE = "profiling.memoryUsage";

    private TestTypes() {
        throw new ConstructorIllegal();
    }

}
