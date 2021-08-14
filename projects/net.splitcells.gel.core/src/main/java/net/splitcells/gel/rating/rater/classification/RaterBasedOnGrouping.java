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
package net.splitcells.gel.rating.rater.classification;

import static java.util.stream.Collectors.toList;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.rating.rater.RatingEventI.ratingEvent;
import static net.splitcells.gel.rating.type.Cost.noCost;
import static net.splitcells.gel.rating.framework.LocalRatingI.localRating;

import java.util.Collection;

import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.constraint.Constraint;
import org.w3c.dom.Node;
import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.gel.rating.rater.Rater;
import net.splitcells.gel.rating.rater.RatingEvent;

public class RaterBasedOnGrouping implements Rater {
    public static RaterBasedOnGrouping raterBasedGrouping(Rater classifier) {
        return new RaterBasedOnGrouping(classifier);
    }

    private final Rater grouping;
    private final List<Discoverable> contexts = list();

    protected RaterBasedOnGrouping(Rater classifier) {
        this.grouping = classifier;
    }

    @Override
    public RatingEvent ratingAfterAddition(Table lines, Line addition, List<Constraint> children, Table ratingsBeforeAddition) {
        final var ratingEvent = ratingEvent();
        final var rBase = grouping.ratingAfterAddition(lines, addition, children, ratingsBeforeAddition);
        ratingEvent.removal().addAll(rBase.removal());
        rBase.additions().forEach((line, localRating) ->
                ratingEvent.additions()
                        .put(line
                                , localRating()
                                        .withPropagationTo(localRating.propagateTo())
                                        .withRating(noCost())
                                        .withResultingGroupId
                                                (localRating.resultingConstraintGroupId())));
        return ratingEvent;
    }

    @Override
    public RatingEvent rating_before_removal(Table lines, Line removal, List<Constraint> children, Table ratingsBeforeRemoval) {
        return ratingEvent();
    }

    @Override
    public Node argumentation(GroupId group, Table allocations) {
        final var reasoning = Xml.elementWithChildren("group-by");
        reasoning.appendChild(grouping.argumentation(group, allocations));
        return reasoning;
    }

    @Override
    public String toSimpleDescription(Line line, Table groupsLineProcessing, GroupId incomingGroup) {
        return grouping.toSimpleDescription(line, groupsLineProcessing, incomingGroup);
    }

    @Override
    public List<Domable> arguments() {
        return list(grouping);
    }

    @Override
    public void addContext(Discoverable context) {
        contexts.add(context);
    }

    @Override
    public Collection<List<String>> paths() {
        return contexts.stream().map(Discoverable::path).collect(toList());
    }

    @Override
    public Class<? extends Rater> type() {
        return RaterBasedOnGrouping.class;
    }

    public Rater classifier() {
        return grouping;
    }
}
