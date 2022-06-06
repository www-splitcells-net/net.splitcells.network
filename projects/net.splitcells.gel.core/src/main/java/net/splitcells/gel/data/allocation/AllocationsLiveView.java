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

import net.splitcells.dem.data.set.Set;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.data.database.Database;


import static net.splitcells.dem.data.set.Sets.setOfUniques;

public interface AllocationsLiveView extends Table {
    /**
     * Returns a {@link Database} of all supplies.
     * The {@link Line#index()} of one supply is the same across this database, {@link #suppliesFree()} and {@link #suppliesUsed()}
     * 
     * @return Database Of All Supplies
     */
    Database supplies();

    /**
     * 
     * @return This database contains all {@link #supplies()}, that are allocated to at least one of {@link #demands()}.
     */
    Database suppliesUsed();

    /**
     * 
     * @return This database contains all {@link #supplies()} that are not allocated to any {@link #demands()}
     */
    Database suppliesFree();

    /**
     * Returns a {@link Database} of all demands.
     * The {@link Line#index()} of one demand is the same across this database, {@link #demandsFree()} and {@link #demandsUsed()}
     *
     * @return Database Of All Supplies
     */
    Database demands();

    /**
     *
     * @return This database contains all {@link #demands()}, that are allocated to at least one of {@link #supplies()}.
     */
    Database demandsUsed();

    /**
     *
     * @return This database contains all {@link #demands()}, that are not allocated to any {@link #supplies()}.
     */
    Database demandsFree();

    /**
     * Determines the demand of the given allocation.
     * 
     * @param allocation Element of {@link #demands()}.
     * @return
     */
    Line demandOfAllocation(Line allocation);

    /**
     * Determines the supply of a given allocation.
     * 
     * @param allocation Element of {@link #supplies()}.
     * @return
     */
    Line supplyOfAllocation(Line allocation);

    Set<Line> allocationsOfSupply(Line supply);

    Set<Line> allocationsOfDemand(Line demand);

    default Set<Line> supply_of_demand(Line demand) {
        final Set<Line> suppliesOfDemands = setOfUniques();
        allocationsOfDemand(demand)
                .forEach(allocation -> suppliesOfDemands.add(supplyOfAllocation(allocation)));
        return suppliesOfDemands;
    }
}