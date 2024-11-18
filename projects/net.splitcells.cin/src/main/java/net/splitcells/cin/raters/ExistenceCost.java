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
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.data.view.Line;
import net.splitcells.gel.data.view.View;
import net.splitcells.gel.proposal.Proposal;
import net.splitcells.gel.rating.framework.Rating;
import net.splitcells.gel.rating.rater.framework.Rater;
import net.splitcells.gel.rating.rater.framework.RatingEvent;
import net.splitcells.gel.rating.rater.lib.GroupingRater;

import static net.splitcells.cin.EntityManager.*;
import static net.splitcells.dem.data.order.Comparators.ASCENDING_INTEGERS;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.dem.utils.ExecutionException.executionException;
import static net.splitcells.gel.constraint.Constraint.INCOMING_CONSTRAINT_GROUP;
import static net.splitcells.gel.constraint.Constraint.LINE;
import static net.splitcells.gel.rating.framework.LocalRatingI.localRating;
import static net.splitcells.gel.rating.rater.framework.RatingEventI.ratingEvent;
import static net.splitcells.gel.rating.rater.lib.LineGroupRater.lineGroupRater;
import static net.splitcells.gel.rating.type.Cost.cost;
import static net.splitcells.gel.rating.type.Cost.noCost;

/**
 * Decrements a {@link EntityManager#PLAYER_ENERGY} at every {@link EntityManager#TIME} by one.
 * Assumes, that the {@link Line} of any incoming {@link GroupId} relate to only one {@link EntityManager#PLAYER}.
 */
public class ExistenceCost implements GroupingRater {
    public static Rater existenceCost() {
        return lineGroupRater(new ExistenceCost());
    }

    private ExistenceCost() {

    }

    @Override
    public RatingEvent rating(View lines, List<Constraint> children) {
        final var ratingEvent = ratingEvent();
        if (lines.isEmpty()) {
            return ratingEvent;

        }
        final var incomingConstraintGroup = lines.unorderedLinesStream().findFirst().orElseThrow()
                .value(INCOMING_CONSTRAINT_GROUP);
        final var times = lines.columnView(LINE).values().stream().map(l -> l.value(TIME))
                .distinct()
                .sorted(ASCENDING_INTEGERS)
                .collect(toList());
        final Rating rating;
        if (times.size() < 2) {
            rating = noCost();
        } else {
            final int endTime = times.get(1);
            final boolean hasEvent = lines.columnView(LINE).stream()
                    .anyMatch(line -> {
                        return line.value(PLAYER_ATTRIBUTE) == PLAYER_ENERGY
                                && line.value(EVENT_TYPE) == ADD_VALUE
                                && line.value(TIME) == endTime
                                && line.value(EVENT_SOURCE) == EXISTENCE_COST_EVENT_SOURCE
                                && line.value(PLAYER_VALUE) == -1f;
                    });

            if (hasEvent) {
                rating = noCost();
            } else {
                rating = cost(1);
            }
        }
        lines.unorderedLinesStream().forEach(line ->
                ratingEvent.additions().put(line
                        , localRating()
                                .withPropagationTo(children)
                                .withResultingGroupId(incomingConstraintGroup)
                                .withRating(noCost())));
        return ratingEvent;
    }

    @Override
    public String toSimpleDescription(Line line, View groupsLineProcessing, GroupId incomingGroup) {
        return "all players loose one unit of energy every time step";
    }

    @Override
    public List<Domable> arguments() {
        return list();
    }

    @Override
    public Proposal propose(Proposal proposal) {
        return proposal;
    }
}
