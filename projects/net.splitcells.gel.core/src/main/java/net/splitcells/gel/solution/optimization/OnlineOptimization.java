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
package net.splitcells.gel.solution.optimization;

import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.solution.Solution;

/**
 * <p>What benefit is there in reviewing past sufferings, and in being unhappy, just because once you were unhappy?
 * - Seneca</p>
 * <p>Prefer {@link OnlineOptimization} over {@link OfflineOptimization},
 * because these are easier to write.
 * The reason for this, is the fact, that one often needs to organize an
 * {@link OptimizationEvent} {@link net.splitcells.dem.data.set.list.List}
 * and oftentimes need to write queries for these {@link OptimizationEvent}s.
 * There are no ready to go data structure for this,
 * which makes this harder.
 * During the usage of {@link OnlineOptimization} one has access to an up to
 * date {@link Solution}, which provides organizational structures
 * for the current state.
 * On the other hand, {@link OfflineOptimization} provides organizational
 * data structures via a {@link net.splitcells.gel.solution.SolutionView}
 * for the initial state,
 * which can be useful as well.
 * </p>
 * <p>TODO IDEA Implement random forest.</p>
 * <p>TODO IDEA Create a single {@link Table} representation for {@link Solution#SOLUTION_TREE}.
 * This maybe would allow simple {@link OnlineOptimization} to work on complex problems.</p>
 */
@FunctionalInterface
public interface OnlineOptimization {
    void optimize(Solution solution);
}
