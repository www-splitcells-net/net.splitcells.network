/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.solution.optimization;

import net.splitcells.gel.solution.Solution;

@FunctionalInterface
public interface ParameterizedOnlineOptimization<T> {
    void optimize(Solution solution, T parameter);
}
