/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.solution.optimization;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.solution.SolutionView;

/**
 * <p>Perfect is the enemy of good - Voltaire</p>
 * <p>Prefer {@link OnlineOptimization} over this,
 * when complex queries are needed to be done on the {@link SolutionView} in order to determine the current state after applying all interim {@link OptimizationEvent}s.
 * Also, most of the time it is easier to implement an {@link OnlineOptimization}
 * than an {@link OfflineOptimization}.</p>
 * 
 * @deprecated This is deprecated and will be removed 2027, if no real use case is found in the meantime.
 *             If such a case is found, it needs to noted here.
 *             This way it will be kown, why this class is still required.
 *             If this class is removed, arguments againts {@link OfflineOptimization} should be noted at {@link OnlineOptimization}. 
 */
@FunctionalInterface
@Deprecated
public interface OfflineOptimization {

	List<OptimizationEvent> optimize(SolutionView solution);

}
