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

/**
 * This is a helper interface, in order to create a {@link Rater} based via simplified
 * {@link Rater#ratingAfterAddition(View, Line, List, View)}.
 * and {@link Rater#rating_before_removal(View, Line, List, View)}
 * via {@link #rating(View, List)}.
 * This is primarily provided for {@link LineGroupRater}.
 */
public interface GroupingRater {
    /**
     * The {@link Rating} of an Event, has to be applied to all {@link Line}s of a group.
     *
     * @param lines    The {@link Line} from {@link Constraint#lineProcessing()} of an incoming {@link GroupId},
     *                 that need to be rated.
     * @param children This list contains every child {@link Constraint},
     *                 to which the incoming {@link Line} can be forwarded to.
     * @return return All lines need a {@link Rating}.
     */
    RatingEvent rating(View lines, List<Constraint> children);

    /**
     * Describes to the user in a natural way, how this is rating given {@link Line}.
     *
     * @param line                 This is the {@link Line}, that needs to be described.
     * @param groupsLineProcessing This is part of the {@link Constraint#lineProcessing()}
     *                             for the incoming {@link GroupId} if the line.
     * @param incomingGroup        This is the incoming {@link GroupId} if the line.
     * @return Prefer using one complete sentence for the description,
     * that starts with lower case and does not end with a dot.
     */
    String toSimpleDescription(Line line, View groupsLineProcessing, GroupId incomingGroup);

    List<Domable> arguments();

    /**
     * It is not required to implement this,
     * as this is only needed for advanced optimizers.
     *
     * @param proposal
     * @return
     */
    default Proposal propose(Proposal proposal) {
        return proposal;
    }
    
    Tree toTree();
}
