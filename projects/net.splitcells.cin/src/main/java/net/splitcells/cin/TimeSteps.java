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
package net.splitcells.cin;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.object.Discoverable;
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
import static net.splitcells.dem.utils.MathUtils.modulus;
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

    public static Rater timeSteps(Attribute<Integer> timeAttribute, boolean isEven) {
        return new TimeSteps(timeAttribute, isEven);
    }

    private final Attribute<Integer> timeAttribute;
    /**
     * The keys are the end time of each group.
     */
    private final Map<Integer, GroupId> timeToPreviousTimeGroup = map();

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
    public Set<List<String>> paths() {
        return setOfUniques();
    }

    @Override
    public void addContext(Discoverable context) {

    }

    @Override
    public List<Domable> arguments() {
        return list();
    }

    @Override
    public RatingEvent ratingAfterAddition(Table linesOfGroup, Line addition, List<Constraint> children, Table ratingsBeforeAddition) {
        final RatingEvent rating = ratingEvent();
        final var timeValue = addition.value(LINE).value(timeAttribute);
        final var timeSpanModulus = modulus(timeValue, 2);
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
        final var afterFirstTimeAddition = timeToPreviousTimeGroup.containsKey(startTime);
        if (afterFirstTimeAddition) {
            final var isPreviousGroupPresent = timeToPreviousTimeGroup.containsKey(timeValue);
            if (isPreviousGroupPresent) {
                final var previousGroup = timeToPreviousTimeGroup.get(timeValue);
                final var localRating = localRating()
                        .withPropagationTo(children)
                        .withRating(noCost())
                        .withResultingGroupId(previousGroup);
                rating.additions().put(addition, localRating);
            }
        } else {
            final var startTimePresent = linesOfGroup.columnView(LINE)
                    .lookup(l -> l.value(timeAttribute).equals(startTime))
                    .isPresent();
            final var endTimePresent = linesOfGroup.columnView(LINE)
                    .lookup(l -> l.value(timeAttribute).equals(endTime))
                    .isPresent();
            if (startTimePresent && endTimePresent) {
                final var timeStep = timeToPreviousTimeGroup.computeIfAbsent(startTime, x -> group(timeStepId(x, x + 1)));
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
        final var timeValue = removal.value(LINE).value(timeAttribute);
        final var removalOfLastTimeElement = linesOfGroup
                .columnView(LINE)
                .lookup(l -> l.value(timeAttribute).equals(timeValue))
                .size() == 1;
        if (removalOfLastTimeElement) {
            timeToPreviousTimeGroup.remove(timeValue);
        }
        if (ENFORCING_UNIT_CONSISTENCY && linesOfGroup.size() == 1) {
            timeToPreviousTimeGroup.requireEmpty();
        }
        return ratingEvent();
    }
}
