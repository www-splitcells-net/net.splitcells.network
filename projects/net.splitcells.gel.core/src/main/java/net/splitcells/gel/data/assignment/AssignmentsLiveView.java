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
package net.splitcells.gel.data.assignment;

import net.splitcells.dem.data.set.Set;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.data.view.Line;
import net.splitcells.gel.data.view.LinePointer;
import net.splitcells.gel.data.view.View;
import net.splitcells.gel.solution.optimization.OnlineOptimization;


import static net.splitcells.dem.data.set.Sets.setOfUniques;

/**
 * TODO Access to things like {@link #supplies()} should be read only via {@link View} by default.
 * Only the creator of an {@link AssignmentsLiveView} should typically be able to set or change such.
 * Things like {@link OnlineOptimization} should not have write access to such by default.
 */
public interface AssignmentsLiveView extends View {
    /**
     * Returns a {@link Table} of all supplies.
     * The {@link Line#index()} of one supply is the same across this database, {@link #suppliesFree()} and {@link #suppliesUsed()}
     * 
     * @return Database Of All Supplies
     */
    Table supplies();

    /**
     * 
     * @return This database contains all {@link #supplies()}, that are allocated to at least one of {@link #demands()}.
     */
    Table suppliesUsed();

    /**
     * 
     * @return This database contains all {@link #supplies()} that are not allocated to any {@link #demands()}
     */
    Table suppliesFree();

    /**
     * Returns a {@link Table} of all demands.
     * The {@link Line#index()} of one demand is the same across this database, {@link #demandsFree()} and {@link #demandsUsed()}
     *
     * @return Database Of All Supplies
     */
    Table demands();

    /**
     *
     * @return This database contains all {@link #demands()}, that are allocated to at least one of {@link #supplies()}.
     */
    Table demandsUsed();

    /**
     *
     * @return This database contains all {@link #demands()}, that are not allocated to any {@link #supplies()}.
     */
    Table demandsFree();

    /**
     * Determines the demand of the given allocation.
     * 
     * @param assignment Element of {@link #demands()}.
     * @return
     */
    Line demandOfAssignment(Line assignment);

    /**
     * <p>Determines the supply of a given allocation.</p>
     * 
     * @param assignment Element of {@link #supplies()}.
     * @return
     */
    Line supplyOfAssignment(Line assignment);

    Line anyAssignmentOf(LinePointer demand, LinePointer supply);

    Set<Line> assignmentsOfSupply(Line supply);

    Set<Line> assignmentsOfDemand(Line demand);

    default Set<Line> suppliesOfDemand(Line demand) {
        final Set<Line> suppliesOfDemands = setOfUniques();
        assignmentsOfDemand(demand)
                .forEach(allocation -> suppliesOfDemands.add(supplyOfAssignment(allocation)));
        return suppliesOfDemands;
    }
}