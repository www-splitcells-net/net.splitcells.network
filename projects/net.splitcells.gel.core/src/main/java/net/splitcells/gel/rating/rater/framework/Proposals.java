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
package net.splitcells.gel.rating.rater.framework;

import net.splitcells.gel.data.allocation.Allocations;
import net.splitcells.gel.solution.Solution;

public class Proposals implements Proposal {

    public static Proposal proposal(Solution subject, Allocations proposedAllocations) {
        return new Proposals(subject, proposedAllocations);
    }

    private final Solution subject;
    private final Allocations proposedAllocations;

    private Proposals(Solution subject, Allocations proposedAllocations) {
        this.subject = subject;
        this.proposedAllocations = proposedAllocations;
    }

    @Override
    public Allocations proposedAllocations() {
        return proposedAllocations;
    }

    @Override
    public Solution subject() {
        return subject;
    }
}
