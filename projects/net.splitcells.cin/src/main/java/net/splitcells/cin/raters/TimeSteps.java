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

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.utils.MathUtils;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.data.view.Line;
import net.splitcells.gel.data.view.View;
import net.splitcells.gel.data.view.attribute.Attribute;
import net.splitcells.gel.rating.framework.Rating;
import net.splitcells.gel.rating.rater.framework.Rater;
import net.splitcells.gel.rating.rater.framework.RatingEvent;

import java.util.Optional;

import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.environment.config.StaticFlags.ENFORCING_UNIT_CONSISTENCY;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.utils.MathUtils.absolute;
import static net.splitcells.dem.utils.MathUtils.modulus;
import static net.splitcells.dem.utils.MathUtils.sign;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.gel.constraint.Constraint.INCOMING_CONSTRAINT_GROUP;
import static net.splitcells.gel.constraint.Constraint.LINE;
import static net.splitcells.gel.constraint.Constraint.PROPAGATION_TO;
import static net.splitcells.gel.constraint.Constraint.RATING;
import static net.splitcells.gel.constraint.Constraint.RESULTING_CONSTRAINT_GROUP;
import static net.splitcells.gel.constraint.GroupId.group;
import static net.splitcells.gel.rating.framework.LocalRatingI.localRating;
import static net.splitcells.gel.rating.rater.framework.RatingEventI.ratingEvent;
import static net.splitcells.gel.rating.type.Cost.noCost;

/**
 * TODO {@link #ratingAfterAddition(View, Line, List, View)} and {@link #rating_before_removal(View, Line, List, View)}
 * always update all {@link Line} regardless if the {@link Rating} changed or not.
 * This is causes a lot of recalculations at {@link Constraint#childrenView()}.
 */
public class TimeSteps implements Rater {
    public static final String NO_TIME_STEP_GROUP = "no-time-step-group";
    public static final String WITH_UNEVEN_START_TIME = " with uneven start time";
    public static final String WITH_EVEN_START_TIME = " with even start time";
    /**
     * Option that tries to improve the performance,
     * when set to true,
     * but does not do achieve a better runtime in practice.
     */
    private static final boolean AVOID_LOOKUPS = false;

    public static String timeStepId(int startTime, int endTime) {
        return startTime + " -> " + endTime;
    }

    public static Rater timeSteps(Attribute<Integer> timeAttribute) {
        return new TimeSteps(timeAttribute, true);
    }

    public static List<Rater> overlappingTimeSteps(Attribute<Integer> timeAttribute) {
        return list(new TimeSteps(timeAttribute, true),
                new TimeSteps(timeAttribute, false));
    }

    public static Rater timeSteps(Attribute<Integer> timeAttribute, boolean isStartTimeEven) {
        return new TimeSteps(timeAttribute, isStartTimeEven);
    }

    private final Attribute<Integer> timeAttribute;

    /**
     * Determines whether the start time of each time step {@link GroupId} is even or odd.
     */
    private final boolean isStartTimeEven;

    private TimeSteps(Attribute<Integer> timeAttribute, boolean isStartTimeEven) {
        this.timeAttribute = timeAttribute;
        this.isStartTimeEven = isStartTimeEven;
    }

    @Override
    public List<Domable> arguments() {
        return list(timeAttribute, tree("" + isStartTimeEven));
    }

    @Override
    public RatingEvent ratingAfterAddition(View linesOfGroup, Line addition, List<Constraint> children, View ratingsBeforeAddition) {
        final RatingEvent rating = ratingEvent();
        final var timeValue = addition.value(LINE).value(timeAttribute);
        final var timeSpanModulus = sign(timeValue) * modulus(absolute(timeValue), 2);
        final int startTime;
        final boolean isTimeValueStart;
        if (isStartTimeEven) {
            isTimeValueStart = timeSpanModulus == 0;
        } else {
            isTimeValueStart = timeSpanModulus == 1;
        }
        if (isTimeValueStart) {
            startTime = timeValue;
        } else {
            startTime = timeValue - 1;
        }
        final var endTime = startTime + 1;
        final var incomingGroup = addition.value(INCOMING_CONSTRAINT_GROUP);
        final var startTimeLine = ratingsBeforeAddition
                .unorderedLinesStream()
                .filter(l -> l.value(LINE).value(timeAttribute).equals(startTime))
                .findFirst();
        final var endTimeLine = ratingsBeforeAddition
                .unorderedLinesStream()
                .filter(l -> l.value(LINE).value(timeAttribute).equals(endTime))
                .findFirst();
        final var afterFirstTimeAddition = startTimeLine.isPresent()
                && endTimeLine.isPresent();
        if (afterFirstTimeAddition) {
            final var localRating = localRating()
                    .withPropagationTo(children)
                    .withRating(noCost())
                    .withResultingGroupId(startTimeLine.orElseThrow().value(RESULTING_CONSTRAINT_GROUP));
            rating.additions().put(addition, localRating);
        } else {
            final var startTimePresent = linesOfGroup.columnView(LINE)
                    .stream()
                    .anyMatch(l -> l.value(timeAttribute).equals(startTime));
            final var endTimePresent = linesOfGroup.columnView(LINE)
                    .stream()
                    .anyMatch(l -> l.value(timeAttribute).equals(endTime));
            if (startTimePresent && endTimePresent) {
                final var timeStep = group(incomingGroup, timeStepId(startTime, endTime));
                final var localRating = localRating()
                        .withPropagationTo(children)
                        .withRating(noCost())
                        .withResultingGroupId(timeStep);
                rating.additions().put(addition, localRating);
                if (AVOID_LOOKUPS) {
                    ratingsBeforeAddition.unorderedLinesStream()
                            .filter(l -> l.value(LINE).value(timeAttribute).equals(startTime))
                            .filter(l -> l.value(LINE).index() != addition.value(LINE).index())
                            .filter(l -> !l.value(RATING).equalz(noCost())
                                    || !l.value(PROPAGATION_TO).equals(children)
                                    || !l.value(RESULTING_CONSTRAINT_GROUP).equals(timeStep))
                            .forEach(l -> rating.updateRating_withReplacement(
                                    linesOfGroup.persistedLookup(LINE, l.value(LINE)).orderedLine(0)
                                    , localRating));
                    ratingsBeforeAddition.unorderedLinesStream()
                            .filter(l -> l.value(LINE).value(timeAttribute).equals(endTime))
                            .filter(l -> l.value(LINE).index() != addition.value(LINE).index())
                            .filter(l -> !l.value(RATING).equalz(noCost())
                                    || !l.value(PROPAGATION_TO).equals(children)
                                    || !l.value(RESULTING_CONSTRAINT_GROUP).equals(timeStep))
                            .forEach(l -> rating.updateRating_withReplacement(
                                    linesOfGroup.persistedLookup(LINE, l.value(LINE)).orderedLine(0)
                                    , localRating));
                } else {
                    linesOfGroup.columnView(LINE)
                            .persistedLookup(l -> l.value(timeAttribute).equals(startTime))
                            .unorderedLinesStream()
                            .filter(l -> l.index() != addition.index())
                            .forEach(l -> {
                                rating.updateRating_withReplacement(l, localRating);
                            });
                    linesOfGroup.columnView(LINE)
                            .persistedLookup(l -> l.value(timeAttribute).equals(endTime))
                            .unorderedLinesStream()
                            .filter(l -> l.index() != addition.index())
                            .forEach(l -> {
                                rating.updateRating_withReplacement(l, localRating);
                            });
                }
            } else {
                final var noTimeStepGroup = noStepTimeGroup(ratingsBeforeAddition, startTime)
                        .orElseGet(() -> group(incomingGroup, noTimeStepGroupName()));
                rating.additions().put(addition, localRating()
                        .withPropagationTo(list())
                        .withRating(noCost())
                        .withResultingGroupId(noTimeStepGroup));
            }
        }
        return rating;
    }

    private String noTimeStepGroupName() {
        if (isStartTimeEven) {
            return NO_TIME_STEP_GROUP + WITH_EVEN_START_TIME;
        } else {
            return NO_TIME_STEP_GROUP + WITH_UNEVEN_START_TIME;
        }
    }

    private Optional<GroupId> noStepTimeGroup(View ratings, int startTime) {
        return ratings.unorderedLinesStream()
                .filter(l -> l.value(LINE).value(timeAttribute) == startTime)
                .map(l -> l.value(RESULTING_CONSTRAINT_GROUP))
                .findFirst();
    }

    @Override
    public RatingEvent rating_before_removal(View linesOfGroup, Line removal, List<Constraint> children, View ratingsBeforeRemoval) {
        final RatingEvent rating = ratingEvent();
        final var timeValue = removal.value(LINE).value(timeAttribute);
        final var timeSpanModulus = sign(timeValue) * modulus(absolute(timeValue), 2);
        final int startTime;
        final boolean isTimeValueStart;
        if (isStartTimeEven) {
            isTimeValueStart = timeSpanModulus == 0;
        } else {
            isTimeValueStart = timeSpanModulus == 1;
        }
        if (isTimeValueStart) {
            startTime = timeValue;
        } else {
            startTime = timeValue - 1;
        }
        final var removalOfLastTimeElement = linesOfGroup
                .columnView(LINE)
                .stream()
                .filter(l -> l.value(timeAttribute).equals(timeValue))
                .count() == 1;
        final var incomingGroup = removal.value(INCOMING_CONSTRAINT_GROUP);
        if (removalOfLastTimeElement) {
            final var resultingGroup = group(incomingGroup, noTimeStepGroupName());
            if (AVOID_LOOKUPS) {
                ratingsBeforeRemoval.unorderedLinesStream()
                        .filter(l -> l.value(LINE).value(timeAttribute).equals(startTime))
                        //.filter(l -> l.value(LINE).index() != removal.value(LINE).index())
                        .filter(l -> !l.value(RATING).equalz(noCost())
                                || !l.value(PROPAGATION_TO).equals(children)
                                || !l.value(RESULTING_CONSTRAINT_GROUP).equals(resultingGroup))
                        .forEach(l -> rating.updateRating_withReplacement(linesOfGroup
                                        .persistedLookup(LINE, l.value(LINE))
                                        .orderedLine(0)
                                , localRating()
                                        .withPropagationTo(children)
                                        .withRating(noCost())
                                        .withResultingGroupId(resultingGroup)));
                ratingsBeforeRemoval.unorderedLinesStream()
                        .filter(l -> l.value(LINE).value(timeAttribute).equals(startTime + 1))
                        //.filter(l -> l.value(LINE).index() != removal.value(LINE).index())
                        .filter(l -> !l.value(RATING).equalz(noCost())
                                || !l.value(PROPAGATION_TO).equals(children)
                                || !l.value(RESULTING_CONSTRAINT_GROUP).equals(resultingGroup))
                        .forEach(l -> rating.updateRating_withReplacement(linesOfGroup
                                        .persistedLookup(LINE, l.value(LINE))
                                        .orderedLine(0)
                                , localRating()
                                        .withPropagationTo(children)
                                        .withRating(noCost())
                                        .withResultingGroupId(resultingGroup)));
            } else {
                linesOfGroup
                        .columnView(LINE)
                        .persistedLookup(l -> l.value(timeAttribute).equals(startTime))
                        .unorderedLinesStream()
                        .forEach(l -> rating.updateRating_withReplacement(l, localRating()
                                .withPropagationTo(children)
                                .withRating(noCost())
                                .withResultingGroupId(resultingGroup)));
                linesOfGroup
                        .columnView(LINE)
                        .persistedLookup(l -> l.value(timeAttribute).equals(startTime + 1))
                        .unorderedLinesStream()
                        .forEach(l -> rating.updateRating_withReplacement(l, localRating()
                                .withPropagationTo(children)
                                .withRating(noCost())
                                .withResultingGroupId(resultingGroup)));
            }
        }
        return rating;
    }

    @Override
    public String toSimpleDescription(Line line, View groupsLineProcessing, GroupId incomingGroup) {
        final var timeValue = line.value(timeAttribute);
        final var isEven = MathUtils.isEven(timeValue);
        final int startTime;
        if (isStartTimeEven) {
            if (isEven) {
                startTime = timeValue;
            } else {
                startTime = timeValue - 1;
            }
        } else {
            if (isEven) {
                startTime = timeValue - 1;
            } else {
                startTime = timeValue;
            }
        }
        return "step of " + timeAttribute.name() + " starting at " + startTime;
    }
}
