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
import net.splitcells.dem.utils.MathUtils;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.data.view.Line;
import net.splitcells.gel.data.view.View;
import net.splitcells.gel.proposal.Proposal;
import net.splitcells.gel.rating.framework.Rating;
import net.splitcells.gel.rating.rater.framework.Rater;
import net.splitcells.gel.rating.rater.framework.RatingEvent;
import net.splitcells.gel.rating.rater.lib.GroupingRater;

import static net.splitcells.cin.EntityManager.ADD_VALUE;
import static net.splitcells.cin.EntityManager.EVENT_TYPE;
import static net.splitcells.cin.EntityManager.PLAYER_ATTRIBUTE;
import static net.splitcells.cin.EntityManager.PLAYER_VALUE;
import static net.splitcells.cin.EntityManager.SET_VALUE;
import static net.splitcells.cin.EntityManager.TIME;
import static net.splitcells.dem.data.order.Comparators.ASCENDING_INTEGERS;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.dem.utils.ExecutionException.executionException;
import static net.splitcells.dem.utils.MathUtils.distance;
import static net.splitcells.gel.constraint.Constraint.INCOMING_CONSTRAINT_GROUP;
import static net.splitcells.gel.constraint.Constraint.LINE;
import static net.splitcells.gel.rating.framework.LocalRatingI.localRating;
import static net.splitcells.gel.rating.rater.framework.RatingEventI.ratingEvent;
import static net.splitcells.gel.rating.rater.lib.LineGroupRater.lineGroupRater;
import static net.splitcells.gel.rating.type.Cost.cost;
import static net.splitcells.gel.rating.type.Cost.noCost;

public class ValueUpdater implements GroupingRater {
    public static Rater valueAdder(int playerAttribute) {
        return lineGroupRater(new ValueUpdater(playerAttribute));
    }

    private record ValueCalc(int actualValue, int targetValue, boolean isDeleted, boolean shouldBeDeleted) {
    }

    private final int playerAttribute;

    private ValueUpdater(int argPlayerAttribute) {
        playerAttribute = argPlayerAttribute;
    }

    @Override
    public RatingEvent rating(View lines, List<Constraint> children) {
        final var ratingEvent = ratingEvent();
        if (lines.isEmpty()) {
            return ratingEvent;
        }
        final var incomingConstraintGroup = lines.unorderedLinesStream().findFirst().orElseThrow()
                .value(INCOMING_CONSTRAINT_GROUP);
        final var valueCalc = calculateValue(lines);
        final Rating rating;
        final List<Constraint> propagationTo;
        if (valueCalc.isDeleted != valueCalc.shouldBeDeleted) {
            rating = cost(1);
            propagationTo = listWithValuesOf();
        } else if (valueCalc.isDeleted && valueCalc.shouldBeDeleted) {
            rating = noCost();
            propagationTo = children;
        } else if (valueCalc.actualValue != valueCalc.targetValue) {
            rating = cost(distance(valueCalc.actualValue, valueCalc.targetValue));
            propagationTo = listWithValuesOf();
        } else {
            rating = noCost();
            propagationTo = children;
        }
        lines.unorderedLinesStream().forEach(line ->
                ratingEvent.additions().put(line
                        , localRating()
                                .withPropagationTo(propagationTo)
                                .withResultingGroupId(incomingConstraintGroup)
                                .withRating(rating)));
        return ratingEvent;
    }

    private ValueCalc calculateValue(View lines) {
        final List<Integer> times;
        final int startTime;
        final int endTime;
        final List<Line> endResults;
        final int endResult;
        final List<Line> startResults;
        final int startResult;
        final List<Line> valueAdds;
        final int valueAdd;
        final List<Line> valueSets;
        final int valueSet;
        final boolean shouldBeDeleted;
        final boolean isDeleted;
        times = lines.columnView(LINE).values().stream()
                .map(l -> l.value(TIME))
                .distinct()
                .sorted(ASCENDING_INTEGERS)
                .collect(toList());
        if (times.size() > 2) {
            throw executionException();
        }
        if (times.isEmpty()) {
            return new ValueCalc(0, 0, true, false);
        }
        if (times.size() == 1) {
            final var actual = lines.columnView(LINE).stream()
                    .filter(line -> line.value(PLAYER_ATTRIBUTE) == playerAttribute
                            && line.value(EVENT_TYPE) == EntityManager.RESULT_VALUE)
                    .map(l -> l.value(PLAYER_VALUE))
                    .reduce(Integer::sum)
                    .orElse(0);
            return new ValueCalc(actual, actual, true, false);
        }
        times.requireSizeOf(2);
        startTime = times.get(0);
        endTime = times.get(1);
        shouldBeDeleted = lines.columnView(LINE).stream()
                .anyMatch(line -> line.value(PLAYER_ATTRIBUTE) == playerAttribute
                        && line.value(EVENT_TYPE) == EntityManager.DELETE_VALUE
                        && line.value(PLAYER_VALUE) == 1);
        endResults = lines.columnView(LINE).stream()
                .filter(line -> line.value(PLAYER_ATTRIBUTE) == playerAttribute
                        && line.value(EVENT_TYPE) == EntityManager.RESULT_VALUE
                        && line.value(TIME) == endTime)
                .collect(toList());
        if (endResults.size() > 1) {
            throw executionException();
        }
        startResults = lines.columnView(LINE).stream()
                .filter(line -> line.value(PLAYER_ATTRIBUTE) == playerAttribute
                        && line.value(EVENT_TYPE) == EntityManager.RESULT_VALUE
                        && line.value(TIME) == startTime)
                .collect(toList());
        if (startResults.isEmpty()) {
            startResult = 0;
        } else {
            startResult = startResults.stream()
                    .map(l -> l.value(PLAYER_VALUE))
                    .reduce(Integer::sum)
                    .orElse(0)
                    / startResults.size();
        }
        if (endResults.isEmpty()) {
            endResult = 0;
            isDeleted = true;
        } else {
            isDeleted = false;
            endResult = endResults.stream()
                    .map(line -> line.value(PLAYER_VALUE))
                    .reduce(Integer::sum)
                    .orElse(0) / endResults.size();
        }
        valueAdds = lines.columnView(LINE).stream()
                .filter(line -> line.value(PLAYER_ATTRIBUTE) == playerAttribute
                        && line.value(EVENT_TYPE) == ADD_VALUE
                        && line.value(TIME) == endTime)
                .collect(toList());
        valueSets = lines.columnView(LINE).stream()
                .filter(line -> line.value(PLAYER_ATTRIBUTE) == playerAttribute
                        && line.value(EVENT_TYPE) == SET_VALUE
                        && line.value(TIME) == endTime)
                .collect(toList());
        valueAdd = valueAdds.stream().map(line -> line.value(PLAYER_VALUE)).reduce(Integer::sum).orElse(0);
        if (valueSets.isEmpty()) {
            return new ValueCalc(endResult, startResult + valueAdd, isDeleted, shouldBeDeleted);
        } else {
            valueSet = valueSets.stream()
                    .map(line -> line.value(PLAYER_VALUE))
                    .reduce(Integer::sum)
                    .orElse(0)
                    / valueSets.size();
            if (valueAdds.isEmpty()) {
                return new ValueCalc(endResult, valueSet, isDeleted, shouldBeDeleted);
            } else {
                return new ValueCalc(endResult, (valueSet + valueAdd) / 2, isDeleted, shouldBeDeleted);
            }
        }
    }

    @Override
    public String toSimpleDescription(Line line, View groupsLineProcessing, GroupId incomingGroup) {
        return "";
    }

    @Override
    public List<Domable> arguments() {
        return listWithValuesOf();
    }

    @Override
    public Proposal propose(Proposal proposal) {
        return proposal;
    }
}
