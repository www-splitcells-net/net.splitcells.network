/*
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
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

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.gel.constraint.Constraint.*;
import static net.splitcells.gel.constraint.type.Then.then;
import static net.splitcells.gel.data.table.Tables.table;
import static net.splitcells.gel.rating.framework.LocalRatingI.localRating;
import static net.splitcells.gel.rating.rater.framework.RatingEventI.ratingEvent;
import static net.splitcells.gel.rating.type.Cost.cost;
import static net.splitcells.gel.rating.type.Cost.noCost;

public class ThenAtLeastRater implements Rater {
    public static Rater thenAtLeastRater(int argLeastCount, Rater argRater) {
        return new ThenAtLeastRater(argLeastCount, argRater);
    }

    private final int leastCount;
    private final Constraint constraint;
    private final Rater rater;

    private ThenAtLeastRater(int argLeastCount, Rater argRater) {
        leastCount = argLeastCount;
        constraint = then(argRater);
        rater = argRater;
    }

    @Override public RatingEvent ratingAfterAddition(View linesOfGroup, Line addition, List<Constraint> children, View lineProcessingBeforeAddition) {
        val incomingGroup = addition.value(INCOMING_CONSTRAINT_GROUP);
        constraint.registerAdditions(incomingGroup, addition.value(LINE));
        val linesRating = ratingEvent();
        val cost = groupElementCost(incomingGroup);
        ratingEventUpdate(linesRating, cost, incomingGroup, linesOfGroup, addition, children);
        linesRating.additions().put(addition
                , localRating()
                        .withPropagationTo(children)
                        .withRating(cost)
                        .withResultingGroupId(addition.value(Constraint.INCOMING_CONSTRAINT_GROUP)));
        return linesRating;
    }

    private Rating groupElementCost(GroupId incomingGroup) {
        val incomingTable = constraint.lineProcessing().lookup(INCOMING_CONSTRAINT_GROUP, incomingGroup);
        double numberOfCompliant = incomingTable
                .columnView(RATING)
                .lookup(r -> r.equalz(noCost()))
                .size();
        Rating cost;
        if (numberOfCompliant >= leastCount) {
            cost = noCost();
        } else {
            cost = cost((leastCount - numberOfCompliant) / incomingTable.size());
        }
        return cost;
    }

    private void ratingEventUpdate(RatingEvent ratingEvent, Rating cost, GroupId incomingGroup, View linesOfGroup, Line changed, List<Constraint> children) {
        linesOfGroup.rawLinesView().stream()
                .filter(e -> e != null)
                .filter(e -> e.index() != changed.index())
                .forEach(e ->
                        ratingEvent.updateRating_withReplacement(e,
                                localRating().
                                        withPropagationTo(children).
                                        withRating(cost).
                                        withResultingGroupId(incomingGroup)));
    }

    @Override public RatingEvent rating_before_removal(View lines, Line removal, List<Constraint> children, View lineProcessingBeforeRemoval) {
        val incomingGroup = removal.value(INCOMING_CONSTRAINT_GROUP);
        constraint.registerBeforeRemoval(incomingGroup, removal.value(LINE));
        val linesRating = ratingEvent();
        val cost = groupElementCost(incomingGroup);
        ratingEventUpdate(linesRating, cost, incomingGroup, lines, removal, children);
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
}
