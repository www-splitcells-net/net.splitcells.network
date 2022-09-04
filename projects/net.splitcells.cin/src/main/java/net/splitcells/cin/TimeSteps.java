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
    private final Map<Integer, GroupId> timeToFirstGroup = map();
    private final Map<Integer, GroupId> timeToSecondGroup = map();

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
        final var firstGroup = timeToFirstGroup.computeIfAbsent(timeValue, x -> group("" + x));
        final var secondGroup = timeToSecondGroup.computeIfAbsent(timeValue + 1, x -> group("" + x));
        final List<LocalRating> localRatings = list();
        localRatings.add(localRating()
                .withPropagationTo(children)
                .withRating(noCost())
                .withResultingGroupId(firstGroup));
        localRatings.add(localRating()
                .withPropagationTo(children)
                .withRating(noCost())
                .withResultingGroupId(secondGroup));
        rating.complexAdditions().put(addition, localRatings);
        return rating;
    }
}
