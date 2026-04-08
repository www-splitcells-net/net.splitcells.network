/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.rating.rater.lib;

import lombok.val;
import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.dem.utils.CommonFunctions;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.data.view.Line;
import net.splitcells.gel.data.view.View;
import net.splitcells.gel.rating.framework.Rating;
import net.splitcells.gel.rating.rater.framework.Rater;
import net.splitcells.gel.rating.rater.framework.RatingEvent;
import net.splitcells.gel.rating.rater.lib.classification.ThenAtLeastRater;
import net.splitcells.gel.rating.type.Cost;

import java.util.Optional;

import static net.splitcells.dem.data.set.Sets.toSetOfUniques;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.utils.MathUtils.absolute;
import static net.splitcells.dem.utils.MathUtils.distance;
import static net.splitcells.gel.constraint.Constraint.*;
import static net.splitcells.gel.rating.framework.LocalRatingI.localRating;
import static net.splitcells.gel.rating.rater.framework.RatingEventI.ratingEvent;
import static net.splitcells.gel.rating.type.Cost.cost;
import static net.splitcells.gel.rating.type.Cost.noCost;

/**
 * <p>This is an incorrect, but faster implementation of {@link HasSize},
 * as it only updates up to 2 {@link Line} per change.
 * {@link HasSize} is significantly slower than this implementation.
 * </p><p>{@link HasSize} changes all {@link Line}, if the {@link Rating} is not {@link Cost#noCost()}.
 * The {@link Cost#noCost()} part {@link Rating} of an {@link Constraint#INCOMING_CONSTRAINT_GROUP}
 * is only stored at one {@link Line} and is the basis for the speed-up.
 * Therefore, the propagation of {@link Line} does not work correctly and individual {@link Rating} are incorrect,
 * whereas the {@link Rating} of a {@link GroupId} is correct.</p>
 */
public class HasSizeFast implements Rater {
    public static final String HAS_SIZE_NAME = "hasSizeFast";

    public static HasSizeFast hasSizeFast(int targetSize) {
        return new HasSizeFast(targetSize);
    }

    private final int targetSize;
    private final List<Discoverable> contexts = list();

    private HasSizeFast(int targetSize) {
        this.targetSize = targetSize;
    }

    @Override
    public RatingEvent ratingAfterAddition(View lines, Line addition, List<Constraint> children
            , View ratingsBeforeAddition) {
        val previousGroupRating = previousGroupRating(lines, false);
        val nextGroupRating = nextGroupRating(lines, false);
        val ratingEvent = ratingEvent();
        if (previousGroupRating.equalz(noCost()) || ratingsBeforeAddition.isEmpty()) {
            val localRating = localRating()
                    .withRating(nextGroupRating)
                    .withResultingGroupId(addition.value(INCOMING_CONSTRAINT_GROUP));
            if (nextGroupRating.equalz(noCost())) {
                localRating.withPropagationTo(children);
            } else {
                localRating.withPropagationTo(list());
            }
            ratingEvent.additions().put(addition, localRating);
        } else {
            val previousRatingLineProcessing = previousRatingLine(ratingsBeforeAddition, Optional.empty())
                    .orElseThrow()
                    .value(LINE);
            val localRating = localRating()
                    .withRating(nextGroupRating)
                    .withResultingGroupId(addition.value(INCOMING_CONSTRAINT_GROUP));
            if (nextGroupRating.equalz(noCost())) {
                localRating.withPropagationTo(children);
            } else {
                localRating.withPropagationTo(list());
            }
            ratingEvent.additions().put(addition, localRating);
            val previousRatingLine = lines.unorderedLinesStream2()
                    .filter(l -> l.value(LINE).equalsTo(previousRatingLineProcessing))
                    .findFirst()
                    .orElseThrow();
            ratingEvent.updateRating_withReplacement(previousRatingLine,
                    localRating().
                            withPropagationTo(children).
                            withRating(noCost()).
                            withResultingGroupId(addition.value(INCOMING_CONSTRAINT_GROUP))
            );
        }
        return ratingEvent;
    }

    @Override
    public RatingEvent rating_before_removal(View lines
            , Line removal
            , List<Constraint> children
            , View ratingsBeforeRemoval) {
        val previousGroupRating = previousGroupRating(lines, true);
        val nextGroupRating = nextGroupRating(lines, true);
        val ratingEvent = ratingEvent();
        if (lines.size() > 1 && !previousGroupRating.equalz(nextGroupRating)) {
            if (previousGroupRating.equalz(noCost())) {
                val nextRatingContainer = lines.unorderedLine(0);
                val localRating = localRating()
                        .withRating(nextGroupRating)
                        .withResultingGroupId(nextRatingContainer.value(INCOMING_CONSTRAINT_GROUP));
                if (nextGroupRating.equalz(noCost())) {
                    localRating.withPropagationTo(children);
                } else {
                    localRating.withPropagationTo(list());
                }
                ratingEvent.updateRating_withReplacement(nextRatingContainer, localRating);
            } else {
                val previousRatingLineProcessing = previousRatingLine(ratingsBeforeRemoval
                        , Optional.of(ratingsBeforeRemoval.lookup(LINE, removal.value(LINE)).anyLine()));
                final Line previousRatingLine;
                if (previousRatingLineProcessing.isPresent()) {
                    previousRatingLine = lines.unorderedLinesStream2()
                            .filter(l -> l.value(LINE).equalsTo(previousRatingLineProcessing.get().value(LINE)))
                            .findFirst()
                            .orElseThrow();
                } else {
                    previousRatingLine = lines.unorderedLinesStream2()
                            .filter(l -> !l.value(LINE).equalsTo(removal.value(LINE)))
                            .findFirst()
                            .orElseThrow();
                }
                val localRating = localRating()
                        .withRating(nextGroupRating)
                        .withResultingGroupId(previousRatingLine.value(INCOMING_CONSTRAINT_GROUP));
                if (nextGroupRating.equalz(noCost())) {
                    localRating.withPropagationTo(children);
                } else {
                    localRating.withPropagationTo(list());
                }
                ratingEvent.updateRating_withReplacement(previousRatingLine, localRating);
            }
        }
        return ratingEvent;
    }

    private Optional<Line> previousRatingLine(View lineProcessing, Optional<Line> beforeRemoval) {
        if (beforeRemoval.isPresent()) {
            if (!beforeRemoval.orElseThrow().value(RATING).equalz(noCost())) {
                return Optional.empty();
            }
            val removal = beforeRemoval.get().index();
            return lineProcessing.unorderedLinesStream2().filter(l -> l.value(LINE).index() != removal)
                    .filter(l -> !l.value(RATING).equalz(noCost()))
                    .findFirst();
        }
        return lineProcessing.unorderedLinesStream2()
                .filter(l -> !l.value(RATING).equalz(noCost()))
                .findFirst();
    }

    private Rating nextGroupRating(View lines, boolean beforeRemoval) {
        var newSize = lines.size();
        if (beforeRemoval) {
            --newSize;
        }
        return cost(distance(targetSize, newSize));
    }

    private Rating previousGroupRating(View lines, boolean beforeRemoval) {
        var newSize = lines.size();
        if (!beforeRemoval) {
            --newSize;
        }
        return cost(distance(targetSize, newSize));
    }

    @Override
    public String toSimpleDescription(Line line, View groupsLineProcessing, GroupId incomingGroup) {
        return "size should be " + targetSize + ", but is " + groupsLineProcessing.size();
    }

    @Override
    public Class<? extends Rater> type() {
        return HasSizeFast.class;
    }

    @Override
    public List<Domable> arguments() {
        return list(tree(HasSizeFast.class.getSimpleName()).withChild(tree("" + targetSize)));
    }

    @Override
    public boolean equals(Object arg) {
        if (arg instanceof HasSizeFast cArg) {
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
        return contexts.stream().map(d -> d.path().shallowCopy()).collect(toSetOfUniques());
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
