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

    public RepairConfig withMinimumConstraintGroupPath(int minimumConstraintGroupPathArg) {
        minimumConstraintGroupPath = minimumConstraintGroupPathArg;
        setupGroupSelector();
        return this;
    }

    public RepairConfig withNumberOfGroupsSelectedPerDefiance(int numberOfGroupsSelectedPerDefianceArg) {
        numberOfGroupsSelectedPerDefiance = numberOfGroupsSelectedPerDefianceArg;
        setupGroupSelector();
        return this;
    }

    public RepairConfig withRepairCompliants(boolean arg) {
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
     * @return The current {@link RepairConfig} state.
     */
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
