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

import net.splitcells.dem.utils.random.Randomness;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;
import static net.splitcells.gel.constraint.Constraint.incomingGroupsOfConstraintPath;

public class GroupSelectors {
    private GroupSelectors() {
        throw constructorIllegal();
    }

    /**
     * This helper method, makes it easier to create a {@link GroupSelector} instead of a {@link FluentGroupSelector}
     * with a lambda and without casting.
     * 
     * @param groupSelector This is the {@link GroupSelector}, which is typically defined in a lambda.
     * @return This is the given argument.
     */
    public static GroupSelector groupSelector(GroupSelector groupSelector) {
        return groupSelector;
    }

    public static GroupSelector groupSelector(Randomness randomness, int minimumConstraintGroupPath
            , int numberOfGroupsSelectedPerDefiance) {
        return allocationsGroups -> {
            final var candidates = allocationsGroups
                    .stream()
                    .filter(allocationGroupsPath ->
                            {
                                if (allocationGroupsPath.size() < minimumConstraintGroupPath) {
                                    return false;
                                }
                                return incomingGroupsOfConstraintPath(allocationGroupsPath.shallowCopy())
                                        .stream()
                                        .map(group -> !allocationGroupsPath
                                                .lastValue()
                                                .get()
                                                .defying(group)
                                                .isEmpty())
                                        .reduce((a, b) -> a || b)
                                        .orElse(false);
                            }
                    )
                    .collect(toList());
            if (candidates.isEmpty()) {
                return list();
            }
            return randomness.chooseAtMostMultipleOf(numberOfGroupsSelectedPerDefiance, candidates);
        };
    }
}
