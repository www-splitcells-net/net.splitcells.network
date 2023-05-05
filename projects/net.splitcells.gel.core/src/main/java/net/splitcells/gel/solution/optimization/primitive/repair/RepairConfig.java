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

import static net.splitcells.dem.utils.random.RandomnessSource.randomness;
import static net.splitcells.gel.solution.optimization.primitive.repair.GroupSelectors.groupSelector;

public class RepairConfig {
    public static RepairConfig repairConfig() {
        return new RepairConfig();
    }

    private int numberOfGroupsSelectedPerDefiance = 1;
    private int minimum_constraint_group_path = 1;

    private FluentGroupSelector groupSelector = GroupSelectors.groupSelector(randomness(), minimum_constraint_group_path
            , numberOfGroupsSelectedPerDefiance);
    private SupplySelector supplySelector = SupplySelectors.supplySelector();

    private boolean repairCompliants = true;

    private DemandSelector demandSelector = DemandSelectors.demandSelector(repairCompliants);

    private RepairConfig() {

    }

    public RepairConfig withRepairCompliants(boolean arg) {
        repairCompliants = arg;
        return this;
    }

    public FluentGroupSelector groupSelector() {
        return groupSelector;
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

    public boolean repairCompliants() {
        return repairCompliants;
    }

    public RepairConfig withDemandSelector(DemandSelector demandSelector) {
        this.demandSelector = demandSelector;
        return this;
    }

    public DemandSelector demandSelector() {
        return demandSelector;
    }
}
