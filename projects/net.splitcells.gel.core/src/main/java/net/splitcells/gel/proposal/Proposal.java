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

import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.data.allocation.Allocations;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.solution.Solution;

/**
 * {@link #proposedAllocations} proposes {@link net.splitcells.gel.data.table.Line}s for {@link Allocations}.
 * Any one of {@link Allocations#demands()} can have a number of {@link Allocations#supplies()} elements and
 * therefore one of {@link Allocations#demands()} can have multiple elements of {@link Allocations#supplies()},
 * that are part of a plausible {@link Solution} according to this {@link Proposal}.
 * {@link Allocations#demands()} and {@link Allocations#supplies()} of {@link #proposedAllocations} are corresponding
 * subsets of the {@link #subject()}'s {@link Allocations#demands()} and {@link Allocations#supplies()}.
 */
public interface Proposal {

    /**
     * @return Set of {@link Allocations} proposed for the given {@link #subject()},
     * in order to get a better {@link Solution}.
     * With this often the domain of the demands is represented,
     * when one compares this concept to the constraint satisfaction problem.
     */
    Allocations proposedAllocations();

    /**
     * @return Set of {@link Allocations}, for which not the proposals are generated,
     * but which provide context for the demands,
     * that need supplies proposed in {@link #proposedAllocations()}.
     * Often the {@link Line}s of {@link #proposedAllocations()} are in the same {@link GroupId}
     * as the lines of {@link #conextAllocations()},
     * This makes it easier to implement {@link Proposal}s for {@link net.splitcells.gel.rating.rater.framework.Rater}s.
     */
    Allocations conextAllocations();

    Solution subject();
}