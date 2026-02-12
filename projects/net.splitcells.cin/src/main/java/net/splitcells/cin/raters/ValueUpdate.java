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
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.dem.utils.ExecutionException;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.data.view.Line;
import net.splitcells.gel.data.view.View;
import net.splitcells.gel.proposal.Proposal;
import net.splitcells.gel.rating.framework.Rating;
import net.splitcells.gel.rating.rater.framework.Rater;
import net.splitcells.gel.rating.rater.framework.RatingEvent;
import net.splitcells.gel.rating.rater.lib.GroupingRater;

import java.util.Optional;

import static net.splitcells.cin.EntityManager.*;
import static net.splitcells.dem.data.order.Comparators.ASCENDING_INTEGERS;
import static net.splitcells.dem.data.set.list.Lists.*;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.utils.ExecutionException.execException;
import static net.splitcells.dem.utils.MathUtils.distance;
import static net.splitcells.gel.constraint.Constraint.INCOMING_CONSTRAINT_GROUP;
import static net.splitcells.gel.constraint.Constraint.LINE;
import static net.splitcells.gel.proposal.Proposal.CONTEXT_ASSIGNMENT;
import static net.splitcells.gel.rating.framework.LocalRatingI.localRating;
import static net.splitcells.gel.rating.rater.framework.RatingEventI.ratingEvent;
import static net.splitcells.gel.rating.rater.lib.LineGroupRater.lineGroupRater;
import static net.splitcells.gel.rating.type.Cost.cost;
import static net.splitcells.gel.rating.type.Cost.noCost;

public class ValueUpdate implements GroupingRater {
    public static Rater valueUpdate(int playerAttribute) {
        return lineGroupRater(new ValueUpdate(playerAttribute));
    }

    private record ValueCalc(int actualValue, int targetValue, boolean isDeleted, boolean shouldBeDeleted) {
    }

    private final int playerAttribute;

    private ValueUpdate(int argPlayerAttribute) {
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
        final var analysis = analyse(lines);
        final Rating rating;
        final List<Constraint> propagationTo;
        if (analysis.isDeleted != analysis.shouldBeDeleted) {
            rating = cost(1);
            propagationTo = listWithValuesOf();
        } else if ((analysis.isDeleted && analysis.shouldBeDeleted)
                || analysis.actualValue == analysis.targetValue) {
            rating = noCost();
            propagationTo = children;
        } else if (analysis.actualValue != analysis.targetValue) {
            rating = cost(distance(analysis.actualValue, analysis.targetValue));
            propagationTo = listWithValuesOf();
        } else {
            throw ExecutionException.execException();
        }
        lines.unorderedLinesStream().forEach(line ->
                ratingEvent.additions().put(line
                        , localRating()
                                .withPropagationTo(propagationTo)
                                .withResultingGroupId(incomingConstraintGroup)
                                .withRating(rating)));
        return ratingEvent;
    }

    /**
     * Part of this is a duplication of {@link #propose(Proposal)}.
     *
     * @param lines
     * @return
     */
    private ValueCalc analyse(View lines) {
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
            throw ExecutionException.execException();
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
            return new ValueCalc(endResult, valueSet + valueAdd, isDeleted, shouldBeDeleted);
        }
    }

    @Override
    public String toSimpleDescription(Line line, View groupsLineProcessing, GroupId incomingGroup) {
        final var analysis = analyse(groupsLineProcessing);
        if (analysis.isDeleted && !analysis.shouldBeDeleted) {
            return "attribute " + playerAttribute + " is deleted but should not be";
        } else if (!analysis.isDeleted && analysis.shouldBeDeleted) {
            return "attribute " + playerAttribute + " is not deleted but should be";
        } else if (analysis.isDeleted && analysis.shouldBeDeleted) {
            return "attribute " + playerAttribute + " is deleted";
        } else if (analysis.actualValue != analysis.targetValue) {
            return "attribute " + playerAttribute + " should be updated to " + analysis.targetValue + ", but was updated to " + analysis.actualValue;
        }
        return "attribute " + playerAttribute + " was updated to " + analysis.actualValue;
    }

    @Override
    public List<Domable> arguments() {
        return listWithValuesOf();
    }

    /**
     * Part of this is a duplication of {@link #analyse(View)}.
     *
     * @param proposal
     * @return
     */
    @Override
    public Proposal propose(Proposal proposal) {
        if (proposal.subject().allowsSuppliesOnDemand()) {
            final int startTime;
            final int endTime;
            final int startResult;
            final boolean isDeleted;
            final List<Line> valueAdds;
            final int valueAdd;
            final int targetValue;
            final int valueSet;
            final List<Line> valueSets;
            final List<Line> startResults;
            final List<Line> endResults;
            final Optional<Line> anyEndTimeEvent;
            final var times = proposal.contextAssignments().referencedValues(CONTEXT_ASSIGNMENT, TIME)
                    .distinct()
                    .sorted(ASCENDING_INTEGERS)
                    .collect(toList());
            if (times.size() > 2) {
                throw ExecutionException.execException();
            }
            if (times.isEmpty()) {
                return proposal;
            }
            if (times.size() == 1) {
                return proposal;
            }
            startTime = times.get(0);
            endTime = times.get(1);
            final var shouldBeDeleted = proposal.contextAssignments()
                    .linesByReference(CONTEXT_ASSIGNMENT)
                    .anyMatch(line -> line.value(PLAYER_ATTRIBUTE) == playerAttribute
                            && line.value(EVENT_TYPE) == EntityManager.DELETE_VALUE
                            && line.value(PLAYER_VALUE) == 1);
            endResults = proposal.contextAssignments()
                    .linesByReference(CONTEXT_ASSIGNMENT)
                    .filter(line -> line.value(PLAYER_ATTRIBUTE) == playerAttribute
                            && line.value(EVENT_TYPE) == EntityManager.RESULT_VALUE
                            && line.value(TIME) == endTime)
                    .toList();
            startResults = proposal.contextAssignments()
                    .linesByReference(CONTEXT_ASSIGNMENT)
                    .filter(line -> line.value(PLAYER_ATTRIBUTE) == playerAttribute
                            && line.value(EVENT_TYPE) == EntityManager.RESULT_VALUE
                            && line.value(TIME) == startTime)
                    .toList();
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
                isDeleted = true;
            } else {
                isDeleted = false;
            }
            valueAdds = proposal.contextAssignments()
                    .linesByReference(CONTEXT_ASSIGNMENT)
                    .filter(line -> line.value(PLAYER_ATTRIBUTE) == playerAttribute
                            && line.value(EVENT_TYPE) == ADD_VALUE
                            && line.value(TIME) == endTime)
                    .toList();
            valueSets = proposal.contextAssignments()
                    .linesByReference(CONTEXT_ASSIGNMENT)
                    .filter(line -> line.value(PLAYER_ATTRIBUTE) == playerAttribute
                            && line.value(EVENT_TYPE) == SET_VALUE
                            && line.value(TIME) == endTime)
                    .toList();
            valueAdd = valueAdds.stream()
                    .map(line -> line.value(PLAYER_VALUE))
                    .reduce(Integer::sum)
                    .orElse(0);
            if (valueSets.isEmpty()) {
                targetValue = startResult + valueAdd;
            } else {
                valueSet = valueSets.stream()
                        .map(line -> line.value(PLAYER_VALUE))
                        .reduce(Integer::sum)
                        .orElse(0)
                        / valueSets.size();
                targetValue = valueSet + valueAdd;
            }
            proposal.contextAssignments()
                    .linesByReference(CONTEXT_ASSIGNMENT)
                    .filter(line -> line.value(PLAYER_ATTRIBUTE) == playerAttribute
                            && line.value(EVENT_TYPE) == SET_VALUE
                            && line.value(TIME) == endTime);
            if (shouldBeDeleted && !isDeleted) {
                endResults.forEach(er -> proposal.proposedDisallocations().addTranslated(list(er)));
            }
            if (endResults.isEmpty()) {
                anyEndTimeEvent = proposal.contextAssignments()
                        .linesByReference(CONTEXT_ASSIGNMENT)
                        .filter(line -> line.value(PLAYER_ATTRIBUTE) == playerAttribute
                                && line.value(TIME) == endTime)
                        .findAny();
                if (anyEndTimeEvent.isPresent()) {
                    proposal.proposedAllocationsWithSupplies().addTranslated(
                            Lists.<Object>list(proposal.subject().demandOfAssignment(anyEndTimeEvent.orElseThrow())
                                            , proposal.subject().supplyOfAssignment(anyEndTimeEvent.orElseThrow()))
                                    .withAppendedValues(proposal.subject().supplies().headerView().stream().map(a -> {
                                        if (a.equals(PLAYER_VALUE)) {
                                            return targetValue;
                                        } else if (a.equals(EVENT_TYPE)) {
                                            return (Object) RESULT_VALUE;
                                        }
                                        return null;
                                    }).toList()));
                }
            } else {
                endResults.forEach(result -> {
                    if (result.value(PLAYER_VALUE) != targetValue) {
                        proposal.proposedDisallocations().addTranslated(list(result));
                        proposal.proposedAllocationsWithSupplies().addTranslated(
                                Lists.<Object>list(proposal.subject().demandOfAssignment(result)
                                                , proposal.subject().supplyOfAssignment(result))
                                        .withAppendedValues(proposal.subject().supplies().headerView().stream().map(a -> {
                                            if (a.equals(PLAYER_VALUE)) {
                                                return (Object) targetValue;
                                            }
                                            return null;
                                        }).toList())
                        );
                    }
                });
            }
        }
        return proposal;
    }

    @Override public Tree toTree() {
        return tree(getClass().getName());
    }
}
