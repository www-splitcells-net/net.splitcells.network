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

import net.splitcells.dem.object.Discoverable;
import net.splitcells.gel.data.allocation.Allocations;
import net.splitcells.gel.data.allocation.AllocationsI;
import net.splitcells.gel.data.allocation.Allocationss;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.solution.Solution;

import static net.splitcells.gel.data.allocation.Allocationss.allocations;
import static net.splitcells.gel.data.database.Databases.database;

public class Proposals implements Proposal {

    public static Proposal proposal(Solution subject) {
        return new Proposals(subject);
    }

    private final Solution subject;
    private final Allocations proposedAllocations;
    private final Allocations contextAllocations;

    private Proposals(Solution subject) {
        this.subject = subject;
        this.proposedAllocations = allocations("proposed-allocations"
                , database("proposed-demands", subject.demands(), subject.demands().headerView2())
                , database("proposed-supplies", subject.supplies(), subject.supplies().headerView2()));
        this.contextAllocations = allocations("context-allocations"
                , database("proposed-demands", subject.demands(), subject.demands().headerView2())
                , database("proposed-supplies", subject.supplies(), subject.supplies().headerView2()));
    }

    @Override
    public Allocations proposedAllocations() {
        return proposedAllocations;
    }

    @Override
    public Allocations conextAllocations() {
        return contextAllocations;
    }

    @Override
    public Solution subject() {
        return subject;
    }
}
