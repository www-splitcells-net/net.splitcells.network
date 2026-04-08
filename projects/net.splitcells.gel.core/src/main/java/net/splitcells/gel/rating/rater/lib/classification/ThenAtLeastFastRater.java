/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.rating.rater.lib.classification;

import lombok.val;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.data.view.Line;
import net.splitcells.gel.data.view.View;
import net.splitcells.gel.rating.framework.Rating;
import net.splitcells.gel.rating.rater.framework.Rater;
import net.splitcells.gel.rating.rater.framework.RatingEvent;
import net.splitcells.gel.rating.type.Cost;

import java.util.Optional;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.gel.constraint.Constraint.*;
import static net.splitcells.gel.constraint.type.Then.then;
import static net.splitcells.gel.rating.framework.LocalRatingI.localRating;
import static net.splitcells.gel.rating.rater.framework.RatingEventI.ratingEvent;
import static net.splitcells.gel.rating.type.Cost.cost;
import static net.splitcells.gel.rating.type.Cost.noCost;

/**
 * This is an incorrect, but faster implementation of {@link ThenAtLeastRater},
 * as it only updates up to 2 {@link Line} per change.
 * {@link ThenAtLeastRater} is significantly slower than this implementation.
 * {@link ThenAtLeastRater} changes all {@link Line}, if the {@link Rating} is not {@link Cost#noCost()}.
 * The {@link Cost#noCost()} part {@link Rating} of an {@link Constraint#INCOMING_CONSTRAINT_GROUP}
 * is only stored at one {@link Line} and is the basis for the speed-up.
 */
public class ThenAtLeastFastRater implements Rater {
    public static Rater thenAtLeastFastRater(int argLeastCount, Rater argRater) {
        return new ThenAtLeastFastRater(argLeastCount, argRater);
    }

    private final int leastCount;
    private final Constraint constraint;
    private final Rater rater;
    private final Map<Line, Line> additionLineToAddition = map();

    private ThenAtLeastFastRater(int argLeastCount, Rater argRater) {
        leastCount = argLeastCount;
        constraint = then(argRater);
        rater = argRater;
    }

    @Override public RatingEvent ratingAfterAddition(View linesOfGroup, Line addition, List<Constraint> children, View lineProcessingBeforeAddition) {
        val incomingGroup = addition.value(INCOMING_CONSTRAINT_GROUP);
        val incomingTable = constraint.lineProcessing().lookup(INCOMING_CONSTRAINT_GROUP, incomingGroup);
        val previousCost = groupElementCost(incomingGroup, Optional.empty(), Optional.of(addition));
        val additionLine = addition.value(LINE);
        additionLineToAddition.put(additionLine, addition);
        constraint.registerAdditions(incomingGroup, additionLine);
        val linesRating = ratingEvent();
        val nextCost = groupElementCost(incomingGroup, Optional.empty(), Optional.empty());
        ratingEventUpdate(linesRating, previousCost, nextCost, incomingGroup, incomingTable, Optional.of(addition), Optional.empty(), children);
        if (linesOfGroup.size() == 1) {
            linesRating.additions().put(addition
                    , localRating()
                            .withPropagationTo(children)
                            .withRating(nextCost)
                            .withResultingGroupId(addition.value(Constraint.INCOMING_CONSTRAINT_GROUP)));
        } else {
            linesRating.additions().put(addition
                    , localRating()
                            .withPropagationTo(children)
                            .withRating(noCost())
                            .withResultingGroupId(addition.value(Constraint.INCOMING_CONSTRAINT_GROUP)));
        }
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
        var compliantLookup = incomingTable
                .columnView(RATING)
                .lookup(r -> r.equalz(noCost()));
        if (beforeRemoval.isPresent()) {
            compliantLookup = compliantLookup.columnView(LINE).lookup(l -> !l.equalsTo(beforeRemoval.orElseThrow()));
        }
        if (afterAddition.isPresent()) {
            compliantLookup = compliantLookup.columnView(LINE).lookup(l -> !l.equalsTo(afterAddition.orElseThrow()));
        }
        double numberOfCompliant = compliantLookup.size();
        Rating cost;
        if (numberOfCompliant >= leastCount) {
            cost = noCost();
        } else {
            cost = cost((leastCount - numberOfCompliant));
        }
        return cost;
    }

    /**
     *
     * @param ratingEvent
     * @param previousCost
     * @param nextCost
     * @param incomingGroup
     * @param incomingTable The {@link Table#headerView()} format corresponds to {@link Constraint#lineProcessing()} of {@link #constraint}.
     * @param afterAddition
     * @param beforeRemoval
     * @param children
     */
    private void ratingEventUpdate(RatingEvent ratingEvent, Rating previousCost, Rating nextCost, GroupId incomingGroup, View incomingTable, Optional<Line> afterAddition, Optional<Line> beforeRemoval, List<Constraint> children) {
        if (previousCost.equalz(nextCost)) {
            return;
        }
        incomingTable.unorderedLinesStream2()
                .filter(e -> {
                    final boolean isAddition;
                    if (afterAddition.isPresent()) {
                        isAddition = e.value(LINE).index() == afterAddition.orElseThrow().index();
                    } else {
                        isAddition = false;
                    }
                    final boolean isRemoval;
                    if (beforeRemoval.isPresent()) {
                        isRemoval = e.value(LINE).index() == beforeRemoval.orElseThrow().index();
                    } else {
                        isRemoval = false;
                    }
                    return !isAddition && !isRemoval;
                })
                .filter(e -> !e.value(RATING).equalz(noCost()))
                .forEach(e ->
                        ratingEvent.updateRating_withReplacement(additionLineToAddition.value(e.value(LINE))
                                , localRating().
                                        withPropagationTo(children).
                                        withRating(noCost()).
                                        withResultingGroupId(incomingGroup)));
        val ratingContainer = additionLineToAddition.value(incomingTable.orderedLine(0).value(LINE));
        ratingEvent.removal().delete(ratingContainer);
        ratingEvent.additions().remove(ratingContainer);
        ratingEvent.updateRating_withReplacement(ratingContainer
                , localRating().
                        withPropagationTo(children).
                        withRating(nextCost).
                        withResultingGroupId(incomingGroup));
    }

    @Override public RatingEvent rating_before_removal(View lines, Line removal, List<Constraint> children, View lineProcessingBeforeRemoval) {
        val incomingGroup = removal.value(INCOMING_CONSTRAINT_GROUP);
        val previousCost = groupElementCost(incomingGroup, Optional.of(removal), Optional.empty());
        val removalLine = removal.value(LINE);
        val linesRating = ratingEvent();
        constraint.registerBeforeRemoval(incomingGroup, removalLine);
        val incomingTable = constraint.lineProcessing().lookup(INCOMING_CONSTRAINT_GROUP, incomingGroup);
        val nextCost = groupElementCost(incomingGroup, Optional.empty(), Optional.empty());
        ratingEventUpdate(linesRating, previousCost, nextCost, incomingGroup, incomingTable, Optional.empty(), Optional.of(removal), children);
        additionLineToAddition.remove(removalLine);
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
