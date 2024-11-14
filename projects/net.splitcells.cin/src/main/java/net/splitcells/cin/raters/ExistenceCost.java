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
package net.splitcells.cin.raters;

import net.splitcells.cin.EntityManager;
import net.splitcells.cin.deprecated.raters.deprecated.PositionClustersCenterX;
import net.splitcells.cin.deprecated.raters.deprecated.PositionClustersCenterY;
import net.splitcells.dem.data.order.Comparators;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.data.view.Line;
import net.splitcells.gel.data.view.View;
import net.splitcells.gel.data.view.attribute.Attribute;
import net.splitcells.gel.rating.rater.framework.Rater;
import net.splitcells.gel.rating.rater.framework.RatingEvent;

import java.util.Optional;

import static net.splitcells.cin.EntityManager.*;
import static net.splitcells.dem.data.order.Comparators.ASCENDING_INTEGERS;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.utils.ExecutionException.executionException;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.gel.constraint.Constraint.INCOMING_CONSTRAINT_GROUP;
import static net.splitcells.gel.constraint.Constraint.LINE;
import static net.splitcells.gel.rating.framework.LocalRatingI.localRating;
import static net.splitcells.gel.rating.rater.framework.RatingEventI.ratingEvent;
import static net.splitcells.gel.rating.rater.lib.RaterBasedOnLineGroup.groupRouter;
import static net.splitcells.gel.rating.type.Cost.cost;
import static net.splitcells.gel.rating.type.Cost.noCost;

/**
 * Decrements a {@link EntityManager#PLAYER_ENERGY} at every {@link EntityManager#TIME} by one.
 */
public class ExistenceCost {


    public static Rater existenceCost() {
        return groupRouter((lines, children) -> {
            /* TODO Lines is used incorrectly, as it does not contain the allocation attributes
             * It contains only the constraint meta data, so this draft is not correct.
             */
            final var ratingEvent = ratingEvent();
            final var incomingConstraintGroup = lines.unorderedLinesStream().findFirst().orElseThrow()
                    .value(INCOMING_CONSTRAINT_GROUP);
            final var times = lines.columnView(LINE).values().stream().map(l -> l.value(TIME))
                    .distinct()
                    .sorted(ASCENDING_INTEGERS)
                    .collect(toList());
            if (times.size() < 2) {
                return ratingEvent;
            }
            final int endTime = times.get(1);
            lines.columnView(LINE).stream().map(l -> l.value(PLAYER)).distinct().forEach(player -> {
                final var playerValues = lines.unorderedLinesStream()
                        .filter(l -> player.equals(l.value(LINE).value(PLAYER)))
                        .collect(toList());
                final var energyUpdate = playerValues.stream()
                        .filter(l -> l.value(LINE).value(PLAYER_ATTRIBUTE) == PLAYER_ENERGY)
                        .filter(l -> l.value(LINE).value(EVENT_TYPE) == ADD_VALUE)
                        .filter(l -> l.value(LINE).value(TIME) == endTime)
                        .filter(l -> l.value(LINE).value(EVENT_SOURCE) == EXISTENCE_COST_EVENT_SOURCE)
                        .filter(l -> l.value(LINE).value(PLAYER_VALUE) == -1f)
                        .collect(toList());
                if (energyUpdate.hasElements()) {
                    if (energyUpdate.size() > 1) {
                        throw executionException("Only one player energy result is valid.");
                    }
                    ratingEvent.additions().put(energyUpdate.get(0)
                            , localRating()
                                    .withPropagationTo(children)
                                    .withResultingGroupId(incomingConstraintGroup)
                                    .withRating(noCost()));
                } else {
                    ratingEvent.additions().put(playerValues.get(0)
                            , localRating()
                                    .withPropagationTo(children)
                                    .withResultingGroupId(incomingConstraintGroup)
                                    .withRating(cost(1)));
                }
            });
            return ratingEvent;
        });
    }

    private ExistenceCost() {
    }
}
