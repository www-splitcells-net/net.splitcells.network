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
package net.splitcells.gel.rating.rater.classification;

import static net.splitcells.dem.data.set.Sets.toSetOfUniques;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.rating.rater.RatingEventI.ratingEvent;
import static net.splitcells.gel.rating.type.Cost.noCost;
import static net.splitcells.gel.rating.framework.LocalRatingI.localRating;

import net.splitcells.dem.data.set.Set;
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

    private RaterBasedOnGrouping(Rater classifier) {
        this.grouping = classifier;
    }

    @Override
    public RatingEvent ratingAfterAddition(Table lines, Line addition, List<Constraint> children, Table ratingsBeforeAddition) {
        return stripCosts(grouping.ratingAfterAddition(lines, addition, children, ratingsBeforeAddition));
    }

    @Override
    public RatingEvent rating_before_removal(Table lines, Line removal, List<Constraint> children, Table ratingsBeforeRemoval) {
        return stripCosts(grouping.rating_before_removal(lines, removal, children, ratingsBeforeRemoval));
    }

    private static RatingEvent stripCosts(RatingEvent arg) {
        final var ratingEvent = ratingEvent();
        ratingEvent.removal().addAll(arg.removal());
        arg.additions().forEach((line, localRating) ->
                ratingEvent.additions()
                        .put(line
                                , localRating()
                                        .withPropagationTo(localRating.propagateTo())
                                        .withRating(noCost())
                                        .withResultingGroupId
                                                (localRating.resultingConstraintGroupId())));
        arg.complexAdditions().forEach((line, ratings) ->
                ratings.forEach(rating -> ratingEvent.extendComplexRating(line
                        , localRating()
                                .withPropagationTo(rating.propagateTo())
                                .withRating(noCost())
                                .withResultingGroupId
                                        (rating.resultingConstraintGroupId()))));
        return ratingEvent;
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
    public Set<List<String>> paths() {
        return contexts.stream().map(Discoverable::path).collect(toSetOfUniques());
    }

    @Override
    public Class<? extends Rater> type() {
        return RaterBasedOnGrouping.class;
    }

    public Rater classifier() {
        return grouping;
    }
}
