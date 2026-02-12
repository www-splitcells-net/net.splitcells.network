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

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.data.view.Line;
import net.splitcells.gel.data.view.View;
import net.splitcells.gel.proposal.Proposal;
import net.splitcells.gel.rating.framework.Rating;
import net.splitcells.gel.rating.rater.framework.Rater;
import net.splitcells.gel.rating.rater.framework.RatingEvent;

import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.gel.constraint.Constraint.LINE;
import static net.splitcells.gel.rating.rater.framework.RatingEventI.ratingEvent;

/**
 * <p>This {@link Rater} makes it easy to rate groups with interdependent {@link Line}s.
 * Every {@link Line} has the same {@link Rating} in the group.
 * Keep in mind, that during a change in the group, every {@link Line}s' {@link Rating} in the group is updated,
 * which makes the performance worse.</p>
 * <p>TODO {@link #ratingAfterAddition(View, Line, List, View)} and {@link #ratingAfterRemoval(View, List, View)}
 * always update all {@link Line} regardless if the {@link Rating} changed or not.
 * This is causes a lot of recalculations at {@link Constraint#childrenView()}.</p>
 *
 */
public class LineGroupRater implements Rater {

    public static Rater lineGroupRater(GroupingRater baseRater) {
        return new LineGroupRater(baseRater);
    }

    private final GroupingRater baseRater;

    private LineGroupRater(GroupingRater argBaseRater) {
        baseRater = argBaseRater;
    }

    @Override
    public RatingEvent ratingAfterAddition(View lines, Line addition, List<Constraint> children, View lineProcessing) {
        final var ratingEvent = baseRater.rating(lines, children);
        lines.unorderedLinesStream().filter(l -> !l.equalsTo(addition))
                .forEach(i -> ratingEvent.removal().add(i));
        return ratingEvent;
    }

    @Override public RatingEvent rating_before_removal(View lines, Line removal, List<Constraint> children, View lineProcessingBeforeRemoval) {
        return ratingEvent();
    }

    @Override
    public RatingEvent ratingAfterRemoval(View lines, List<Constraint> children, View lineProcessing) {
        final var ratingEvent = baseRater.rating(lines, children);
        lines.unorderedLinesStream().forEach(i -> ratingEvent.removal().add(i));
        return ratingEvent;
    }

    private RatingEvent translateReratingToUpdate(View lines, RatingEvent ratingEvent, View lineProcessing) {
        lineProcessing.unorderedLinesStream().forEach(i -> ratingEvent.removal().add(i));
        return ratingEvent;
    }

    @Override
    public String toSimpleDescription(Line line, View groupsLineProcessing, GroupId incomingGroup) {
        return baseRater.toSimpleDescription(line, groupsLineProcessing, incomingGroup);
    }

    @Override
    public List<Domable> arguments() {
        return baseRater.arguments();
    }

    @Override
    public Proposal propose(Proposal proposal) {
        proposal.subject().demandsFree().unorderedLinesStream()
                .forEach(df -> {

                });
        return baseRater.propose(proposal);
    }

    @Override
    public Tree toTree() {
        return tree("Group of").withChild(baseRater.toTree());
    }
}
