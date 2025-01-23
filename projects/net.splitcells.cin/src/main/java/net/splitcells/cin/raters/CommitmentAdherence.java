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
package net.splitcells.cin.raters;

import net.splitcells.dem.data.order.Comparators;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.data.view.Line;
import net.splitcells.gel.data.view.View;
import net.splitcells.gel.data.view.attribute.Attribute;
import net.splitcells.gel.proposal.Proposal;
import net.splitcells.gel.rating.framework.Rating;
import net.splitcells.gel.rating.rater.framework.Rater;
import net.splitcells.gel.rating.rater.framework.RatingEvent;
import net.splitcells.gel.solution.Solution;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.environment.config.StaticFlags.ENFORCING_UNIT_CONSISTENCY;
import static net.splitcells.dem.utils.ExecutionException.executionException;
import static net.splitcells.gel.constraint.Constraint.INCOMING_CONSTRAINT_GROUP;
import static net.splitcells.gel.constraint.Constraint.LINE;
import static net.splitcells.gel.proposal.Proposal.PROPOSE_UNCHANGED;
import static net.splitcells.gel.rating.framework.LocalRatingI.localRating;
import static net.splitcells.gel.rating.rater.framework.RatingEventI.ratingEvent;
import static net.splitcells.gel.rating.type.Cost.cost;
import static net.splitcells.gel.rating.type.Cost.noCost;

/**
 * This {@link Rater} can not prevent {@link Line} removals.
 */
public class CommitmentAdherence implements Rater {

    public static Rater commitmentAdherence(Attribute<Integer> time) {
        return new CommitmentAdherence(time);
    }

    private final Attribute<Integer> time;
    private int committedTime = -1;


    private CommitmentAdherence(Attribute<Integer> time) {
        this.time = time;
    }

    @Override
    public void init(Solution solution) {
        committedTime = solution.allocations().unorderedLinesStream2()
                .map(a -> a.value(time))
                .max(Comparators.ASCENDING_INTEGERS)
                .orElse(-1);
    }

    @Override
    public List<Domable> arguments() {
        return list(time);
    }

    /**
     * TODO HACK The implementation is not really present.
     *
     * @return
     */
    @Override
    public RatingEvent ratingAfterAddition(View lines, Line addition, List<Constraint> children, View lineProcessing) {
        if (ENFORCING_UNIT_CONSISTENCY && committedTime == -1) {
            throw executionException();
        }
        final Rating rating;
        if (addition.value(LINE).value(time) <= committedTime) {
            rating = cost(1);
        } else {
            rating = noCost();
        }
        return ratingEvent().addRating_viaAddition(addition, localRating()
                .withPropagationTo(children)
                .withResultingGroupId(addition.value(INCOMING_CONSTRAINT_GROUP))
                .withRating(rating));
    }

    @Override
    public String toSimpleDescription(Line line, View groupsLineProcessing, GroupId incomingGroup) {
        return "Lines are committed up to " + committedTime + " " + time.name() + ".";
    }

    /**
     * TODO IDEA This method should propose for free demands of a committed time,
     * the corresponding supply,
     * that was present during {@link #init(Solution)}.
     *
     * @param proposal Already present proposal.
     * @return
     */
    @Override
    public Proposal propose(Proposal proposal) {
        if (ENFORCING_UNIT_CONSISTENCY && committedTime == -1) {
            throw executionException();
        }
        proposal.subject().columnView(time)
                .stream()
                .filter(t -> t != null)
                .filter(t -> t <= committedTime)
                .distinct()
                .forEach(t ->
                        proposal.proposedAssignments().addTranslated(list(PROPOSE_UNCHANGED, null, null, null)
                                .withAppended(proposal.subject().headerView2().flow()
                                        .map(attribute -> {
                                            if (time.equals(attribute)) {
                                                return t;
                                            }
                                            return null;
                                        }).toList())));
        return proposal;
    }
}
