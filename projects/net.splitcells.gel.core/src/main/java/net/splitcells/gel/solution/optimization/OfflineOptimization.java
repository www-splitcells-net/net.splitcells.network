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
package net.splitcells.gel.solution.optimization;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.solution.SolutionView;

/**
 * <p>Perfect is the enemy of good - Voltaire</p>
 * <p>Prefer {@link OnlineOptimization} over this,
 * when complex queries are needed to be done on the {@link SolutionView} in order to determine the current state after applying all interim {@link OptimizationEvent}s.
 * Also, most of the time it is easier to implement an {@link OnlineOptimization}
 * than an {@link OfflineOptimization}.</p>
 */
@FunctionalInterface
public interface OfflineOptimization {

	List<OptimizationEvent> optimize(SolutionView solution);

}