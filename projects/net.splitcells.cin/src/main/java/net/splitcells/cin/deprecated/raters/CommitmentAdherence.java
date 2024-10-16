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
package net.splitcells.cin.deprecated.raters;

import net.splitcells.dem.data.order.Comparators;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.proposal.Proposal;
import net.splitcells.gel.rating.rater.framework.Rater;
import net.splitcells.gel.rating.rater.framework.RatingEvent;
import net.splitcells.gel.solution.Solution;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.gel.rating.rater.framework.RatingEventI.ratingEvent;

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
        committedTime = solution.allocations().unorderedLinesStream()
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
    public RatingEvent ratingAfterAddition(Table lines, Line addition, List<Constraint> children, Table lineProcessing) {
        return ratingEvent();
    }

    @Override
    public String toSimpleDescription(Line line, Table groupsLineProcessing, GroupId incomingGroup) {
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
        final var invalidDemands = proposal.proposedAllocations()
                .demands()
                .unorderedLinesStream()
                .filter(a -> a.value(time) <= committedTime)
                .collect(toList());
        invalidDemands.forEach(a -> proposal.proposedAllocations().demands().remove(a));
        return proposal;
    }
}
