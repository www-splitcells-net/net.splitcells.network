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
package net.splitcells.gel.solution.optimization.primitive.repair;

import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.data.view.Line;
import net.splitcells.gel.rating.type.Cost;
import net.splitcells.gel.solution.Solution;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.utils.random.RandomnessSource.randomness;
import static net.splitcells.gel.solution.optimization.primitive.repair.GroupSelectors.groupSelector;

public class RepairConfig {
    public static RepairConfig repairConfig() {
        return new RepairConfig();
    }

    private int numberOfGroupsSelectedPerDefiance = 1;
    private int minimumConstraintGroupPath = 1;

    private FluentGroupSelector groupSelector = GroupSelectors.groupSelector(randomness(), minimumConstraintGroupPath
            , numberOfGroupsSelectedPerDefiance);
    private SupplySelector supplySelector = SupplySelectors.supplySelector();

    private boolean repairCompliance = true;
    private boolean freeDefyingGroupOfConstraintGroup = true;

    private DemandSelector demandSelector = DemandSelectors.demandSelector(repairCompliance);

    private RepairConfig() {

    }

    private void setupGroupSelector() {
        groupSelector = GroupSelectors.groupSelector(randomness(), minimumConstraintGroupPath
                , numberOfGroupsSelectedPerDefiance);
    }

    /**
     * @deprecated Create a {@link FluentGroupSelector} builder instead.
     * @param minimumConstraintGroupPathArg
     * @return
     */
    @Deprecated
    public RepairConfig withMinimumConstraintGroupPath(int minimumConstraintGroupPathArg) {
        minimumConstraintGroupPath = minimumConstraintGroupPathArg;
        setupGroupSelector();
        return this;
    }

    /**
     * @deprecated Create a {@link FluentGroupSelector} builder instead.
     * @param numberOfGroupsSelectedPerDefianceArg
     * @return
     */
    @Deprecated
    public RepairConfig withNumberOfGroupsSelectedPerDefiance(int numberOfGroupsSelectedPerDefianceArg) {
        numberOfGroupsSelectedPerDefiance = numberOfGroupsSelectedPerDefianceArg;
        setupGroupSelector();
        return this;
    }

    /**
     * Determines whether {@link Line} of a {@link GroupId} in a {@link Solution#allocations()},
     * that cause no {@link Cost}, should be changed during the optimization or not.
     *
     * @param arg This is the new value for this flag.
     * @return Returns this.
     */
    public RepairConfig withRepairCompliance(boolean arg) {
        repairCompliance = arg;
        return this;
    }

    public FluentGroupSelector groupSelector() {
        return groupSelector;
    }

    /**
     * Sets {@link #groupSelector()} in such a way,
     * that only the root {@link net.splitcells.gel.constraint.Constraint} is selected.
     *
     * @deprecated Create a {@link FluentGroupSelector} builder instead.
     * @return The current {@link RepairConfig} state.
     */
    @Deprecated
    public RepairConfig withGroupSelectorOfRoot() {
        return withGroupSelector(rootConstraint -> list(list(rootConstraint)));
    }

    public RepairConfig withGroupSelector(FluentGroupSelector groupSelector) {
        this.groupSelector = groupSelector;
        return this;
    }

    public SupplySelector supplySelector() {
        return supplySelector;
    }

    public RepairConfig withSupplySelector(SupplySelector arg) {
        supplySelector = arg;
        return this;
    }

    public boolean repairCompliance() {
        return repairCompliance;
    }

    public RepairConfig withDemandSelector(DemandSelector demandSelector) {
        this.demandSelector = demandSelector;
        return this;
    }

    public DemandSelector demandSelector() {
        return demandSelector;
    }

    /**
     * @return If false the repair algorithm will only repair the selected demands.
     * Otherwise, the repair will extend the demand selection based on their respective groups.
     */
    public boolean freeDefyingGroupOfConstraintGroup() {
        return freeDefyingGroupOfConstraintGroup;
    }

    public RepairConfig withFreeDefyingGroupOfConstraintGroup(boolean arg) {
        freeDefyingGroupOfConstraintGroup = arg;
        return this;
    }
}
