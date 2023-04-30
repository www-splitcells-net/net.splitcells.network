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
package net.splitcells.gel.solution.optimization.primitive.repair;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.solution.optimization.OnlineOptimization;

import java.util.function.Function;

/**
 * Assigns supplies to given free demands,
 * which are grouped by {@link GroupId}.
 */
@FunctionalInterface
public interface SupplySelector extends Function<Map<GroupId, Set<Line>>, OnlineOptimization> {
    /**
     *
     * @param freeDemandGroups These are free demands grouped according to some unknown function.
     * @return This is an optimization function that can be applied to the {@link net.splitcells.gel.solution.Solution},
     * that contains the {@link Line}s of the {@param freeDemandGroups}.
     */
    OnlineOptimization apply(Map<GroupId, Set<Line>> freeDemandGroups);
}
