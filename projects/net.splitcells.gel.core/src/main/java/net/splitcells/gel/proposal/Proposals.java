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

import net.splitcells.gel.data.assignment.Assignments;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.data.view.Line;
import net.splitcells.gel.data.view.attribute.Attribute;
import net.splitcells.gel.solution.Solution;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.data.assignment.Assignmentss.assignments;
import static net.splitcells.gel.data.table.Tables.table;
import static net.splitcells.gel.data.table.Tables.table2;
import static net.splitcells.gel.data.view.attribute.AttributeI.attribute;
import static net.splitcells.gel.data.view.attribute.AttributeI.attributeObject;

public class Proposals implements Proposal {

    public static Attribute<Object> NEW_SUPPLY = attributeObject(Line.class, "proposal-for-new-supply");

    public static Proposal proposal(Solution subject) {
        return new Proposals(subject);
    }

    private final Solution subject;
    private final Assignments proposedAssignments;
    private final Assignments contextAssignments;
    private final Table proposedAllocationsWithNewSupplies;

    private Proposals(Solution subject) {
        this.subject = subject;
        this.proposedAssignments = assignments("proposed-allocations"
                , table("proposed-demands", subject.demands(), subject.demands().headerView2())
                , table("proposed-supplies", subject.supplies(), subject.supplies().headerView2()));
        this.contextAssignments = assignments("context-allocations"
                , table("proposed-demands", subject.demands(), subject.demands().headerView2())
                , table("proposed-supplies", subject.supplies(), subject.supplies().headerView2()));
        proposedAllocationsWithNewSupplies = table2("proposed-allocations-with-new-supplies"
                , subject.demands()
                , list(NEW_SUPPLY).withAppended(subject.supplies().headerView())
        );
    }

    /**
     * @return These {@link Assignments} are proposed in order to get a better {@link Solution}.
     */
    @Override
    public Assignments proposedAllocations() {
        return proposedAssignments;
    }

    @Override
    public Table proposedAllocationsWithNewSupplies() {
        return proposedAllocationsWithNewSupplies;
    }

    @Override
    public Assignments contextAllocations() {
        return contextAssignments;
    }

    /**
     * @return The {@link Solution} for which this is generated for.
     */
    @Override
    public Solution subject() {
        return subject;
    }
}
