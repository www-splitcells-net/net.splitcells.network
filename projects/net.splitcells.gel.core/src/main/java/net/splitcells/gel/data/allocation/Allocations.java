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
package net.splitcells.gel.data.allocation;

import net.splitcells.dem.utils.ExecutionException;
import net.splitcells.gel.data.assignment.Assignments;
import net.splitcells.gel.data.assignment.AssignmentsLiveView;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.data.view.Line;

import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;

/**
 * <p>Allows at most one assignment for each one of {@link #demands()} and for each one of {@link #supplies()}.
 * The assignment API is split into {@link Allocations} and {@link Assignments},
 * also both support the same basic concept.
 * {@link Allocations} is just a stricter version of {@link Assignments},
 * which is not only important for some consumers of such.
 * Dedicated implementations of {@link Allocations} are generally faster and
 * use less memory than {@link Assignments}.
 * Consumers of assignments that do not differentiate between {@link Allocations} and {@link Assignments}
 * can declare the class {@link Allocations} as an argument,
 * to recommend the caller to provide the more efficient {@link Allocations}, if possible.</p>
 */
public interface Allocations extends Table, AssignmentsLiveView {

    /**
     * Allows to assign at most one assignment for each demand and for each supply.
     *
     * @param demand This is the demand of the assignment.
     * @param supply This is the supply of the assignment.
     * @return This is the {@link Line} representing the allocation.
     * @throws ExecutionException Throws an exception,
     * if there is an allocation already present for the given demand or supply.
     */
    default Line allocate(Line demand, Line supply) {
        throw notImplementedYet();
    }

    default void deallocate(Line demand, Line supply) {
        throw notImplementedYet();
    }
}
