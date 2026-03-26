/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.constraint.intermediate.data;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.rating.framework.Rating;


import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.map.Maps.map;

/**
 * Lists all {@link Rating} of a {@link Constraint}'s {@link GroupId} and
 * also states which {@link GroupId} are propagated to which {@link Constraint#childrenView()}.
 */
public class RatingRouting {
    public static RatingRouting ratingRouting() {
        return new RatingRouting();
    }

    private RatingRouting() {

    }

    /**
     * All
     */
    private final List<Rating> ratings = list();
    private final Map<Constraint, Set<GroupId>> childrenToGroups = map();

    public List<Rating> ratings() {
        return ratings;
    }

    public Map<Constraint, Set<GroupId>> children_to_groups() {
        return childrenToGroups;
    }
}