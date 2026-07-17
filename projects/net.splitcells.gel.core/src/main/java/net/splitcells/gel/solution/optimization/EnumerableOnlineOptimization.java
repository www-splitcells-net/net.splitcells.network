/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.solution.optimization;

import net.splitcells.gel.solution.Solution;

/**
 * Provides a parameterized optimization interface,
 * where all possible options are numbered.
 */
public interface EnumerableOnlineOptimization extends ParameterizedOnlineOptimization<Integer> {
    /**
     * Returns the number of possible parameters for {@link #optimize(Solution, Object)}
     * for a given {@link Solution}.
     * The parameter ranges from 0 to {@link #numberOfBranches(Solution)} - 1.
     *
     * @param solution The Solution For Which The Branches Are Calculated
     * @return Number Of Branches
     */
    int numberOfBranches(Solution solution);
}
