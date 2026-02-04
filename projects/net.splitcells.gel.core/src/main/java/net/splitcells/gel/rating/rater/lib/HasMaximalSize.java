/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.rating.rater.lib;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.dem.utils.CommonFunctions;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.data.view.Line;
import net.splitcells.gel.data.view.View;
import net.splitcells.gel.rating.framework.Rating;
import net.splitcells.gel.rating.rater.framework.Rater;
import net.splitcells.gel.rating.rater.framework.RatingEvent;

import static net.splitcells.dem.data.set.Sets.toSetOfUniques;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.resource.communication.log.Logs.logs;
import static net.splitcells.gel.rating.framework.LocalRatingI.localRating;
import static net.splitcells.gel.rating.rater.framework.RatingEventI.ratingEvent;
import static net.splitcells.gel.rating.type.Cost.cost;
import static net.splitcells.gel.rating.type.Cost.noCost;


public class HasMaximalSize implements Rater {
    public static HasMaximalSize hasMaximalSize(int argMaximalSize) {
        return new HasMaximalSize(argMaximalSize);
    }

    private final int maximalSize;
    private final List<Discoverable> contexts = list();

    private HasMaximalSize(int argMaximalSize) {
        maximalSize = argMaximalSize;
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
        return "Group size should be at most " + maximalSize + ", but is " + groupsLineProcessing.size() + " instead.";
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
            logs().warn(getClass().getName() + " should not be run on empty groups.");
            rating = noCost();
        } else if (actualSize > maximalSize) {
            final int difference = actualSize - maximalSize;
            rating = cost(difference / ((double) actualSize));
        } else {
            rating = noCost();
        }
        return rating;
    }

    @Override
    public Class<? extends Rater> type() {
        return HasMaximalSize.class;
    }

    @Override
    public List<Domable> arguments() {
        return list(() -> tree(HasMaximalSize.class.getSimpleName()).withChild(tree("" + maximalSize)));
    }

    @Override
    public boolean equals(Object arg) {
        if (arg instanceof HasMaximalSize cArg) {
            return this.maximalSize == cArg.maximalSize;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return CommonFunctions.hashCode(maximalSize);
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
        return getClass().getSimpleName() + ", " + maximalSize;
    }
}
