/*
 * Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License,
 * which is available at https://spdx.org/licenses/MIT.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 */
package net.splitcells.gel.data.allocation;

import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.Sets.toSetOfUniques;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.Sets;
import net.splitcells.dem.environment.config.StaticFlags;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.database.Database;
import net.splitcells.gel.data.table.LinePointer;
import net.splitcells.gel.data.table.attribute.Attribute;

/**
 * <p>
 * {@link Discoverable#path()} of {@link Allocations} start with the {@link Discoverable#path()} of the demand
 * and end with {@link net.splitcells.gel.common.Language#ALLOCATIONS}.
 * </p>
 * <p>
 * TODO Create usage table and use this instead of loosely coupled tables.
 * A usage table has a source table, a used table and unused table like demands, used demands and unused demands.
 * This also does reduce code duplication.
 * </p>
 */
public interface Allocations extends Database, AllocationsLiveView {
    Line allocate(Line demand, Line supply);

    /**
     * Removes all allocations,
     * that are identified by the {@link Line#index()} of {@code demand} and {@code supply}.
     * The {@link Line#values()} of {@code demand} and {@code supply} are not considered.
     *
     * @param demand Demand of the allocations to be removed.
     * @param supply Supply of the allocations to be removed.
     */
    default void deallocate(Line demand, Line supply) {
        allocationsOf(demand, supply).forEach(this::remove);
    }

    default Set<Line> allocationsOf(Line demand, Line supply) {
        final var allocationsOf = allocationsOfSupply(supply);
        return allocationsOfDemand(demand)
                .stream()
                .filter(allocationsOf::contains)
                .collect(toSetOfUniques());
    }

    Line allocationOf(LinePointer demand, LinePointer supply);
}