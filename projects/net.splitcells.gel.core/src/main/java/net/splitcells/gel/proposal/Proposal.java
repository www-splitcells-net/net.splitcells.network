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
package net.splitcells.gel.proposal;

import net.splitcells.dem.data.set.list.ListView;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.data.assignment.Assignments;
import net.splitcells.gel.data.assignment.AssignmentsLiveView;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.data.view.Line;
import net.splitcells.gel.solution.Solution;

/**
 * {@link #proposedAllocations} proposes {@link net.splitcells.gel.data.view.Line}s for {@link Assignments}.
 * Any one of {@link Assignments#demands()} can have a number of {@link Assignments#supplies()} elements and
 * therefore one of {@link Assignments#demands()} can have multiple elements of {@link Assignments#supplies()},
 * that are part of a plausible {@link Solution} according to this {@link Proposal}.
 * {@link Assignments#demands()} and {@link Assignments#supplies()} of {@link #proposedAllocations} are corresponding
 * subsets of the {@link #subject()}'s {@link Assignments#demands()} and {@link Assignments#supplies()}.
 */
public interface Proposal {

    /**
     * @return Set of {@link Assignments} proposed for the given {@link #subject()},
     * in order to get a better {@link Solution}.
     * With this often the domain of the demands is represented,
     * when one compares this concept to the constraint satisfaction problem.
     * The {@link Assignments#headerView()} consists of {@link Solution#demands()} and {@link Solution#supplies()}.
     */
    Assignments proposedAllocations();

    /**
     * @return <p>Set of {@link Assignments} proposed for the given {@link #subject()},
     * in order to get a better {@link Solution}.
     * With this often the domain of the demands is represented,
     * when one compares this concept to the constraint satisfaction problem.</p>
     * <p>The {@link Assignments#headerView()} consists of {@link Solution#demands()} and
     * {@link Solution#headerView()} of {@link Solution#supplies()}.
     * Such {@link Solution#supplies()} do not exist and have to be created with methods like
     * {@link AssignmentsLiveView#addTranslatedSupply(ListView)}.</p>
     *
     */
    Table proposedAllocationsWithNewSupplies();

    /**
     * @return Set of {@link Assignments}, for which not the proposals are generated,
     * but which provide context for the demands,
     * that need supplies proposed in {@link #proposedAllocations()}.
     * Often the {@link Line}s of {@link #proposedAllocations()} are in the same {@link GroupId}
     * as the lines of {@link #contextAllocations()},
     * This makes it easier to implement {@link Proposal}s for {@link net.splitcells.gel.rating.rater.framework.Rater}s.
     */
    Assignments contextAllocations();

    Solution subject();
}
