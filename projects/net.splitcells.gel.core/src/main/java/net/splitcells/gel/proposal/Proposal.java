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
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.data.assignment.Assignments;
import net.splitcells.gel.data.assignment.AssignmentsLiveView;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.data.view.Line;
import net.splitcells.gel.data.view.attribute.Attribute;
import net.splitcells.gel.solution.Solution;

import static net.splitcells.gel.data.view.attribute.AttributeI.attribute;

/**
 * <p>{@link #proposedAllocationsOld} proposes new {@link Line}s for {@link Assignments},
 * in order to improve a given {@link #subject()}.
 * There is no guarantee, if the {@link Proposal} actually improves the given {@link #subject()},
 * as the quality depends on the producers.
 * For instance, when a {@link Proposal} is created based on an element of {@link Constraint#childrenView()},
 * the resulting {@link Proposal} may not fit to the parent {@link Constraint}.</p>
 * <p>This concept basically attempts to implement domain pruning in constraint satisfaction problems (CSP).</p>
 * <p>Any one of {@link Assignments#demands()} can have a number of {@link Assignments#supplies()} elements and
 * therefore one of {@link Assignments#demands()} can have multiple elements of {@link Assignments#supplies()},
 * that are part of a plausible {@link Solution} according to this {@link Proposal}.
 * {@link Assignments#demands()} and {@link Assignments#supplies()} of {@link #proposedAllocationsOld} are corresponding
 * subsets of the {@link #subject()}'s {@link Assignments#demands()} and {@link Assignments#supplies()}.</p>
 */
public interface Proposal {

    /**
     * These are {@link Solution#allocations()} elements.
     */
    Attribute<Line> CONTEXT_ASSIGNMENT = attribute(Line.class, "context assignment");
    /**
     * This is an existing {@link Line} of {@link Assignments#supplies()}.
     * It's values are used as default values for the new {@link Line} to be created.
     */
    Attribute<Line> NEW_SUPPLY_BASE = attribute(Line.class, "base for new supply");
    Attribute<Line> EXISTING_DEMAND = attribute(Line.class, "Existing demand");
    Attribute<Line> EXISTING_ASSIGNMENT = attribute(Line.class, "Existing assignment");

    /**
     * TODO This method should return a table with {@link Line} {@link Attribute} only.
     * See {@link Assignments#demands()}, {@link Assignments#supplies()} and {@link Assignments#orderedLines()}.
     * This allows the {@link Assignments} implementation to determine,
     * if the values are stored directly or only referenced, which has distinct performance characteristics.
     * This would also provide more information regarding {@link Line#index()} of the {@link #subject()}.
     *
     * @return Set of {@link Assignments} proposed for the given {@link #subject()},
     * in order to get a better {@link Solution}.
     * With this often the domain of the demands is represented,
     * when one compares this concept to the constraint satisfaction problem.
     * The {@link Assignments#headerView()} consists of {@link Solution#demands()} and {@link Solution#supplies()}.
     */
    @Deprecated
    Assignments proposedAllocationsOld();

    /**
     * @return <p>Set of {@link Assignments} proposed for the given {@link #subject()},
     * in order to get a better {@link Solution}.
     * With this often the domain of the demands is represented,
     * when one compares this concept to the constraint satisfaction problem.</p>
     * <p>The {@link Table#headerView()} consists of {@link Proposal#EXISTING_DEMAND}, {@link Proposal#NEW_SUPPLY_BASE} and
     * {@link Solution#headerView()} of {@link Solution#supplies()}.
     * Such {@link Solution#supplies()} do not have to exist and have to be created with methods like
     * {@link AssignmentsLiveView#addTranslatedSupply(ListView)} by consumers of {@link Proposal}.</p>
     */
    Table proposedAllocationsWithSupplies();

    /**
     * @return This {@link Table} contains all {@link Solution#demands()}, that should be removed from the solution.
     * Note, that this can lead to a situation,
     * where an optimization step can lead to {@link Solution#demands()} without allocated {@link Solution#supplies()}.
     * Most of the time running the optimization should fix the problem.
     * These disallocations make most sense,
     * when it is used in combination with {@link #proposedAllocationsWithSupplies()}.
     */
    Table proposedDisallocations();

    /**
     * TODO This method should return a table with {@link Line} {@link Attribute} only.
     * See {@link #proposedAllocationsOld()}.
     *
     * @return TODO REMOVE The following documentation does not make sense.
     * Set of {@link Assignments}, for which not the proposals are generated,
     * but which provide context for the demands,
     * that need supplies proposed in {@link #proposedAllocationsOld()}.
     * The format of {@link Assignments#headerView()} is {@link Solution#demands()} and {@link Solution#supplies()}.
     * Often the {@link Line}s of {@link #proposedAllocationsOld()} are in the same {@link GroupId}
     * as the lines of {@link #contextAllocationsOld()},
     * This makes it easier to implement {@link Proposal}s for {@link net.splitcells.gel.rating.rater.framework.Rater}s.
     */
    @Deprecated
    Assignments contextAllocationsOld();

    /**
     * @return This contains all assignments of an {@link GroupId},
     * for which proposals are generated.
     * The {@link Table#headerView()} format is {@link #CONTEXT_ASSIGNMENT}.
     */
    Table contextAssignments();

    /**
     * @return This is the {@link Solution}, for which the proposals are done.
     */
    Solution subject();
}
