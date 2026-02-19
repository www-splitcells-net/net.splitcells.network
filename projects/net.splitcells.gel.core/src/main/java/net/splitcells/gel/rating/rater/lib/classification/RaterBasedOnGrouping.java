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
package net.splitcells.gel.rating.rater.lib.classification;

import static net.splitcells.dem.data.set.Sets.toSetOfUniques;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.rating.rater.framework.RatingEventI.ratingEvent;
import static net.splitcells.gel.rating.type.Cost.noCost;
import static net.splitcells.gel.rating.framework.LocalRatingI.localRating;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.gel.data.view.Line;
import net.splitcells.gel.data.view.View;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.gel.rating.rater.framework.Rater;
import net.splitcells.gel.rating.rater.framework.RatingEvent;

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
    public RatingEvent ratingAfterAddition(View lines, Line addition, List<Constraint> children, View ratingsBeforeAddition) {
        return stripCosts(grouping.ratingAfterAddition(lines, addition, children, ratingsBeforeAddition));
    }

    @Override
    public RatingEvent rating_before_removal(View lines, Line removal, List<Constraint> children, View ratingsBeforeRemoval) {
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
    public String toSimpleDescription(Line line, View groupsLineProcessing, GroupId incomingGroup) {
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

    @Override
    public Tree toTree() {
        return tree("Rater based on grouping of").withChild(grouping.toTree());
    }

    /**
     *
     * @return As this {@link Rater} is only used for {@link ForAll}, there is no need to state, that it is a grouping based rater.
     */
    @Override public String descriptiveName() {
        return grouping.name();
    }
}
