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
import net.splitcells.gel.rating.framework.LocalRating;
import net.splitcells.gel.rating.rater.Rater;
import net.splitcells.gel.rating.rater.RatingEvent;

import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.gel.constraint.Constraint.LINE;
import static net.splitcells.gel.constraint.GroupId.group;
import static net.splitcells.gel.rating.framework.LocalRatingI.localRating;
import static net.splitcells.gel.rating.rater.RatingEventI.ratingEvent;
import static net.splitcells.gel.rating.type.Cost.noCost;

public class TimeSteps implements Rater {

    public static Rater timeSteps(Attribute<Integer> timeAttribute) {
        return new TimeSteps(timeAttribute);
    }

    private final Attribute<Integer> timeAttribute;
    private final Map<Integer, GroupId> timeToPreviousTimeGroup = map();

    private TimeSteps(Attribute<Integer> timeAttribute) {
        this.timeAttribute = timeAttribute;
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
    public RatingEvent ratingAfterAddition(Table lines, Line addition, List<Constraint> children, Table ratingsBeforeAddition) {
        final RatingEvent rating = ratingEvent();
        final var timeValue = addition.value(LINE).value(timeAttribute);
        final var firstTimeAddition = lines
                .columnView(LINE)
                .lookup(l -> l.value(timeAttribute).equals(timeValue))
                .size() > 1;
        if (firstTimeAddition) {
            ratingAfterFirstAddition(lines, children, timeValue, rating);
            ratingAfterFirstAddition(lines, children, timeValue + 1, rating);
        } else {
            ratingAfterConsecutiveAddition(lines, addition, children, timeValue, rating);
            ratingAfterConsecutiveAddition(lines, addition, children, timeValue + 1, rating);
        }
        return rating;
    }

    private void ratingAfterConsecutiveAddition(Table lines, Line addition, List<Constraint> children, int timeValue, RatingEvent rating) {
        final var isPreviousGroupPresent = timeToPreviousTimeGroup.containsKey(timeValue);
        if (isPreviousGroupPresent) {
            final var previousGroup = timeToPreviousTimeGroup.get(timeValue);
            final List<LocalRating> localRatings = listWithValuesOf(localRating()
                    .withPropagationTo(children)
                    .withRating(noCost())
                    .withResultingGroupId(previousGroup));
            rating.complexAdditions().put(addition, localRatings);
        }
    }

    private void ratingAfterFirstAddition(Table lines, List<Constraint> children, int timeValue, RatingEvent rating) {
        final var previousTimePresent = lines.columnView(LINE)
                .lookup(l -> l.value(timeAttribute).equals(timeValue - 1))
                .isPresent();
        if (previousTimePresent) {
            final var previousGroup = timeToPreviousTimeGroup.computeIfAbsent(timeValue, x -> group((x - 1) + " -> " + x));
            final List<LocalRating> localRatings = listWithValuesOf(localRating()
                    .withPropagationTo(children)
                    .withRating(noCost())
                    .withResultingGroupId(previousGroup));
            lines.columnView(LINE)
                    .lookup(l -> l.value(timeAttribute).equals(timeValue))
                    .linesStream()
                    .forEach(l -> {
                        if (rating.complexAdditions().containsKey(l)) {
                            rating.complexAdditions().get(l).addAll(localRatings);
                        } else {
                            rating.complexAdditions().put(l, localRatings);
                        }
                    });
        }
    }
}
