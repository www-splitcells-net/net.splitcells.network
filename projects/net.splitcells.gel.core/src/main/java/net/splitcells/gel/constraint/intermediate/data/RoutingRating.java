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
package net.splitcells.gel.constraint.intermediate.data;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.rating.framework.Rating;

import java.util.Set;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.map.Maps.map;

public class RoutingRating {
    /**
     * TODO RENAME
     * 
     * @return
     */
    public static RoutingRating create() {
        return new RoutingRating();
    }
    private RoutingRating() {

    }
    private final List<Rating> ratings = list();
    private final Map<Constraint, Set<GroupId>> children_to_groups = map();

    public List<Rating> events() {
        return ratings;
    }

    public Map<Constraint, Set<GroupId>> children_to_groups() {
        return children_to_groups;
    }
}