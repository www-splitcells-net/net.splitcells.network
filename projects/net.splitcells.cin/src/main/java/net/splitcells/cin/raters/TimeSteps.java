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
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.rating.rater.Rater;
import net.splitcells.gel.rating.rater.RatingEvent;

import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.environment.config.StaticFlags.ENFORCING_UNIT_CONSISTENCY;
import static net.splitcells.dem.lang.perspective.PerspectiveI.perspective;
import static net.splitcells.dem.utils.MathUtils.absolute;
import static net.splitcells.dem.utils.MathUtils.modulus;
import static net.splitcells.dem.utils.MathUtils.sign;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.gel.constraint.Constraint.LINE;
import static net.splitcells.gel.constraint.GroupId.group;
import static net.splitcells.gel.rating.framework.LocalRatingI.localRating;
import static net.splitcells.gel.rating.rater.RatingEventI.ratingEvent;
import static net.splitcells.gel.rating.type.Cost.noCost;

public class TimeSteps implements Rater {
    public static final String NO_TIME_STEP_GROUP = "no-time-step-group";

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

    public static Rater timeSteps(Attribute<Integer> timeAttribute, boolean isEven) {
        return new TimeSteps(timeAttribute, isEven);
    }

    private final Attribute<Integer> timeAttribute;
    /**
     * The keys are the start time of each group.
     */
    private final Map<Integer, GroupId> startTimeToTimeStepGroup = map();

    private final GroupId noTimeStepGroup = group(NO_TIME_STEP_GROUP);
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
        return list(timeAttribute, perspective("" + isStartTimeEven));
    }

    @Override
    public RatingEvent ratingAfterAddition(Table linesOfGroup, Line addition, List<Constraint> children, Table ratingsBeforeAddition) {
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
        final var afterFirstTimeAddition = startTimeToTimeStepGroup.containsKey(startTime);
        if (afterFirstTimeAddition) {
            final var localRating = localRating()
                    .withPropagationTo(children)
                    .withRating(noCost())
                    .withResultingGroupId(startTimeToTimeStepGroup.get(startTime));
            rating.additions().put(addition, localRating);
        } else {
            final var startTimePresent = linesOfGroup.columnView(LINE)
                    .lookup(l -> l.value(timeAttribute).equals(startTime))
                    .isPresent();
            final var endTimePresent = linesOfGroup.columnView(LINE)
                    .lookup(l -> l.value(timeAttribute).equals(endTime))
                    .isPresent();
            if (startTimePresent && endTimePresent) {
                final var timeStep = startTimeToTimeStepGroup.computeIfAbsent(startTime, x -> group(timeStepId(x, x + 1)));
                final var localRating = localRating()
                        .withPropagationTo(children)
                        .withRating(noCost())
                        .withResultingGroupId(timeStep);
                rating.additions().put(addition, localRating);
                linesOfGroup.columnView(LINE)
                        .lookup(l -> l.value(timeAttribute).equals(startTime))
                        .linesStream()
                        .filter(l -> l.index() != addition.index())
                        .forEach(l -> {
                            rating.updateRating_withReplacement(l, localRating);
                        });
                linesOfGroup.columnView(LINE)
                        .lookup(l -> l.value(timeAttribute).equals(endTime))
                        .linesStream()
                        .filter(l -> l.index() != addition.index())
                        .forEach(l -> {
                            rating.updateRating_withReplacement(l, localRating);
                        });
            } else {
                rating.additions().put(addition, localRating()
                        .withPropagationTo(children)
                        .withRating(noCost())
                        .withResultingGroupId(noTimeStepGroup));
            }
        }
        return rating;
    }

    @Override
    public RatingEvent rating_before_removal(Table linesOfGroup, Line removal, List<Constraint> children, Table ratingsBeforeRemoval) {
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
                .lookup(l -> l.value(timeAttribute).equals(timeValue))
                .size() == 1;
        if (removalOfLastTimeElement) {
            startTimeToTimeStepGroup.remove(startTime);
            linesOfGroup
                    .columnView(LINE)
                    .lookup(l -> l.value(timeAttribute).equals(startTime))
                    .linesStream()
                    .forEach(l -> rating.updateRating_withReplacement(l, localRating()
                            .withPropagationTo(children)
                            .withRating(noCost())
                            .withResultingGroupId(noTimeStepGroup)));
            linesOfGroup
                    .columnView(LINE)
                    .lookup(l -> l.value(timeAttribute).equals(startTime + 1))
                    .linesStream()
                    .forEach(l -> rating.updateRating_withReplacement(l, localRating()
                            .withPropagationTo(children)
                            .withRating(noCost())
                            .withResultingGroupId(noTimeStepGroup)));
        }
        if (ENFORCING_UNIT_CONSISTENCY && linesOfGroup.size() == 1) {
            startTimeToTimeStepGroup.requireEmpty();
        }
        return rating;
    }
}
