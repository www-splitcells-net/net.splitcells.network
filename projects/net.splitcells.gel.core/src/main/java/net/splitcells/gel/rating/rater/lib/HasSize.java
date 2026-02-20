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
package net.splitcells.gel.rating.rater.lib;

import static net.splitcells.dem.data.set.Sets.toSetOfUniques;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.utils.MathUtils.absolute;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.rating.rater.framework.RatingEventI.ratingEvent;
import static net.splitcells.gel.rating.type.Cost.cost;
import static net.splitcells.gel.rating.framework.LocalRatingI.localRating;
import static net.splitcells.gel.rating.type.Cost.noCost;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.dem.utils.CommonFunctions;
import net.splitcells.gel.data.view.Line;
import net.splitcells.gel.data.view.View;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.rating.rater.framework.Rater;
import net.splitcells.gel.rating.rater.framework.RatingEvent;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.gel.rating.framework.Rating;


public class HasSize implements Rater {
    public static final String HAS_SIZE_NAME = "hasSize";

    public static HasSize hasSize(int targetSize) {
        return new HasSize(targetSize);
    }

    private final int targetSize;
    private final List<Discoverable> contexts = list();

    private HasSize(int targetSize) {
        this.targetSize = targetSize;
    }

    @Override
    public RatingEvent ratingAfterAddition(View lines, Line addition, List<Constraint> children
            , View ratingsBeforeAddition) {
        final var individualRating = rating(lines, false);
        final var additionalRatings
                = rateLines(lines, addition, children, individualRating);
        additionalRatings.additions().put(addition
                , localRating()
                        .withPropagationTo(children)
                        .withRating(individualRating)
                        .withResultingGroupId(addition.value(Constraint.INCOMING_CONSTRAINT_GROUP))
        );
        return additionalRatings;
    }

    private RatingEvent rateLines(View lines, Line changed, List<Constraint> children, Rating cost) {
        final RatingEvent linesRating = ratingEvent();
        lines.rawLinesView().stream()
                .filter(e -> e != null)
                .filter(e -> e.index() != changed.index())
                .forEach(e -> {
                    linesRating.updateRating_withReplacement(e,
                            localRating().
                                    withPropagationTo(children).
                                    withRating(cost).
                                    withResultingGroupId(changed.value(Constraint.INCOMING_CONSTRAINT_GROUP))
                    );
                });
        return linesRating;
    }

    @Override
    public String toSimpleDescription(Line line, View groupsLineProcessing, GroupId incomingGroup) {
        return "size should be " + targetSize + ", but is " + groupsLineProcessing.size();
    }

    @Override
    public RatingEvent rating_before_removal
            (View lines
                    , Line removal
                    , List<Constraint> children
                    , View ratingsBeforeRemoval) {
        return rateLines(lines, removal, children, rating(lines, true));
    }

    private Rating rating(View lines, boolean beforeRemoval) {
        final Rating rating;
        final int actualSize;
        if (beforeRemoval) {
            actualSize = lines.size() - 1;
        } else {
            actualSize = lines.size();
        }
        if (actualSize == 0) {
            rating = noCost();
        } else if (actualSize > 0) {
            final int difference = absolute(targetSize - actualSize);
            rating = cost(difference / ((double) actualSize));
        } else {
            throw new AssertionError("negative size is: " + actualSize);
        }
        return rating;
    }

    @Override
    public Class<? extends Rater> type() {
        return HasSize.class;
    }

    @Override
    public List<Domable> arguments() {
        return list(tree(HasSize.class.getSimpleName()).withChild(tree("" + targetSize)));
    }

    @Override
    public boolean equals(Object arg) {
        if (arg instanceof HasSize cArg) {
            return this.targetSize == cArg.targetSize;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return CommonFunctions.hashCode(targetSize);
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
    public String toString() {
        return getClass().getSimpleName() + ", " + targetSize;
    }

    @Override
    public Tree toTree() {
        return tree("has-size").withProperty("target-size", targetSize + "");
    }

    @Override public String descriptivePathName() {
        return "has-size-of-" + targetSize;
    }
}
