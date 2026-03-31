/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.rating.rater.lib.classification;

import lombok.val;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.data.view.Line;
import net.splitcells.gel.data.view.View;
import net.splitcells.gel.rating.framework.Rating;
import net.splitcells.gel.rating.rater.framework.Rater;
import net.splitcells.gel.rating.rater.framework.RatingEvent;

import java.util.Optional;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.gel.constraint.Constraint.*;
import static net.splitcells.gel.constraint.type.Then.then;
import static net.splitcells.gel.rating.framework.LocalRatingI.localRating;
import static net.splitcells.gel.rating.rater.framework.RatingEventI.ratingEvent;
import static net.splitcells.gel.rating.type.Cost.cost;
import static net.splitcells.gel.rating.type.Cost.noCost;

public class ThenAtLeastFastRater implements Rater {
    public static Rater thenAtLeastFastRater(int argLeastCount, Rater argRater) {
        return new ThenAtLeastFastRater(argLeastCount, argRater);
    }

    private final int leastCount;
    private final Constraint constraint;
    private final Rater rater;

    private ThenAtLeastFastRater(int argLeastCount, Rater argRater) {
        leastCount = argLeastCount;
        constraint = then(argRater);
        rater = argRater;
    }

    @Override public RatingEvent ratingAfterAddition(View linesOfGroup, Line addition, List<Constraint> children, View lineProcessingBeforeAddition) {
        val incomingGroup = addition.value(INCOMING_CONSTRAINT_GROUP);
        val previousCost = groupElementCost(incomingGroup, Optional.empty(), Optional.of(addition));
        constraint.registerAdditions(incomingGroup, addition.value(LINE));
        val linesRating = ratingEvent();
        val nextCost = groupElementCost(incomingGroup, Optional.empty(), Optional.empty());
        ratingEventUpdate(linesRating, previousCost, nextCost, incomingGroup, linesOfGroup, Optional.of(addition), Optional.empty(), children);
        linesRating.additions().put(addition
                , localRating()
                        .withPropagationTo(children)
                        .withRating(nextCost)
                        .withResultingGroupId(addition.value(Constraint.INCOMING_CONSTRAINT_GROUP)));
        return linesRating;
    }

    /**
     * TODO This maybe could be speed up by using a lookup, instead of going over all values.
     *
     * @param incomingGroup
     * @param beforeRemoval
     * @return
     */
    private Rating groupElementCost(GroupId incomingGroup, Optional<Line> beforeRemoval, Optional<Line> afterAddition) {
        val incomingTable = constraint.lineProcessing().lookup(INCOMING_CONSTRAINT_GROUP, incomingGroup);
        var ratingLookup = incomingTable
                .columnView(RATING)
                .lookup(r -> r.equalz(noCost()));
        if (beforeRemoval.isPresent()) {
            ratingLookup = ratingLookup.columnView(LINE).lookup(l -> !l.equalsTo(beforeRemoval.orElseThrow()));
        }
        if (afterAddition.isPresent()) {
            ratingLookup = ratingLookup.columnView(LINE).lookup(l -> !l.equalsTo(afterAddition.orElseThrow()));
        }
        double numberOfCompliant = ratingLookup.size();
        Rating cost;
        if (numberOfCompliant >= leastCount) {
            cost = noCost();
        } else {
            cost = cost((leastCount - numberOfCompliant) / incomingTable.size());
        }
        return cost;
    }

    private void ratingEventUpdate(RatingEvent ratingEvent, Rating previousCost, Rating nextCost, GroupId incomingGroup, View linesOfGroup, Optional<Line> afterAddition, Optional<Line> beforeRemoval, List<Constraint> children) {
        if (previousCost.equalz(nextCost)) {
            return;
        }
        linesOfGroup.rawLinesView().stream()
                .filter(e -> e != null)
                .filter(e -> {
                    final boolean isAddition;
                    if (afterAddition.isPresent()) {
                        isAddition = e.index() == afterAddition.orElseThrow().index();
                    } else {
                        isAddition = false;
                    }
                    final boolean isRemoval;
                    if (beforeRemoval.isPresent()) {
                        isRemoval = e.index() == beforeRemoval.orElseThrow().index();
                    } else {
                        isRemoval = false;
                    }
                    return !isAddition && !isRemoval;
                })
                .forEach(e ->
                        ratingEvent.updateRating_withReplacement(e
                                , localRating().
                                        withPropagationTo(children).
                                        withRating(nextCost).
                                        withResultingGroupId(incomingGroup)));
    }

    @Override public RatingEvent rating_before_removal(View lines, Line removal, List<Constraint> children, View lineProcessingBeforeRemoval) {
        val incomingGroup = removal.value(INCOMING_CONSTRAINT_GROUP);
        val previousCost = groupElementCost(incomingGroup, Optional.of(removal), Optional.empty());
        constraint.registerBeforeRemoval(incomingGroup, removal.value(LINE));
        val linesRating = ratingEvent();
        val nextCost = groupElementCost(incomingGroup, Optional.empty(), Optional.empty());
        ratingEventUpdate(linesRating, previousCost, nextCost, incomingGroup, lines, Optional.empty(), Optional.of(removal), children);
        return linesRating;
    }

    @Override public String toSimpleDescription(Line line, View groupsLineProcessing, GroupId incomingGroup) {
        return "At least "
                + leastCount
                + " assignments should comply with "
                + rater.toSimpleDescription(line, groupsLineProcessing, incomingGroup);
    }

    @Override public List<Domable> arguments() {
        return list(tree(leastCount + ""), constraint);
    }

    @Override public String descriptivePathName() {
        return "at-least-" + leastCount + "-comply-with-" + rater.descriptivePathName();
    }
}
