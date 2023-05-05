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
import net.splitcells.dem.data.set.Sets;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.data.set.map.Maps;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.solution.Solution;

import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.data.set.map.Pair.pair;
import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;
import static net.splitcells.gel.constraint.Constraint.*;
import static net.splitcells.gel.rating.type.Cost.noCost;

public class DemandSelectors {
    private DemandSelectors() {
        throw constructorIllegal();
    }

    public static DemandSelector demandSelector(boolean repairCompliants) {
        return (Constraint constraintGrouping, Solution solution) -> {
            final Map<GroupId, Set<Line>> demandGrouping = map();
            final Map<GroupId, Set<Line>> defianceCache = Maps.map();
            constraintGrouping
                    .lineProcessing()
                    .unorderedLines()
                    .stream()
                    /**
                     * TODO HACK This is code duplication.
                     * It reimplements part of {@link ConstraintGroupBasedRepair#freeDefyingGroupOfConstraintGroup}.
                     */
                    .filter(processing -> {
                        final var group = processing.value(INCOMING_CONSTRAINT_GROUP);
                        return !defianceCache.computeIfAbsent(group, g -> constraintGrouping.defying(g)).isEmpty();
                    })
                    /**
                     * TODO HACK This is code duplication.
                     * It reimplements part of {@link ConstraintGroupBasedOfflineRepair#freeDefyingGroupOfConstraintGroup}.
                     */
                    .filter(processing -> {
                        if (!repairCompliants) {
                            return !processing.value(RATING).equalz(noCost());
                        }
                        return true;
                    })
                    .map(processing -> pair(processing.value(Constraint.RESULTING_CONSTRAINT_GROUP)
                            , processing.value(LINE)))
                    .forEach(processing -> {
                        final Set<Line> group;
                        if (!demandGrouping.containsKey(processing.getKey())) {
                            group = Sets.setOfUniques();
                            demandGrouping.put(processing.getKey(), group);
                        } else {
                            group = demandGrouping.get(processing.getKey());
                        }
                        group.with(solution.demandOfAllocation(processing.getValue()));
                    });
            return demandGrouping;
        };
    }


}
