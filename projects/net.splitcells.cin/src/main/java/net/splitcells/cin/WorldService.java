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
package net.splitcells.cin;

import net.splitcells.gel.GelDev;
import net.splitcells.gel.solution.SolutionView;

import static net.splitcells.dem.Dem.waitIndefinitely;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.resource.communication.interaction.LogLevel.INFO;
import static net.splitcells.dem.utils.Time.reportRuntime;
import static net.splitcells.gel.solution.optimization.primitive.OnlineLinearInitialization.onlineLinearInitialization;
import static net.splitcells.gel.solution.optimization.primitive.repair.ConstraintGroupBasedRepair.constraintGroupBasedRepair;
import static net.splitcells.gel.solution.optimization.primitive.repair.DemandSelectors.demandSelector;
import static net.splitcells.gel.solution.optimization.primitive.repair.DemandSelectorsConfig.demandSelectorsConfig;
import static net.splitcells.gel.solution.optimization.primitive.repair.RepairConfig.repairConfig;
import static net.splitcells.sep.Network.network;

public class WorldService {
    public static void main(String... args) {
        GelDev.process(() -> {
            final var network = network();
            final var currentWorldHistory = World.worldHistory(World.WORLD_HISTORY, list(), list());
            reportRuntime(() -> {
                network.withNode(World.WORLD_HISTORY, currentWorldHistory);
                World.initWorldHistory(currentWorldHistory);
                World.allocateGlider(currentWorldHistory);
                currentWorldHistory.init();
                World.allocateRestAsDead(currentWorldHistory);
            }, "Initialize world history.", INFO);
            reportRuntime(() -> {
                network.withOptimization(World.WORLD_HISTORY, onlineLinearInitialization());
                network.withOptimization(World.WORLD_HISTORY, constraintGroupBasedRepair(
                        repairConfig()
                                .withRepairCompliants(false)
                                .withFreeDefyingGroupOfConstraintGroup(false)
                                .withDemandSelector(demandSelector(demandSelectorsConfig()
                                                .withRepairCompliants(false)
                                                .withUseCompleteRating(true)
                                        , list(currentWorldHistory.constraint()
                                                , currentWorldHistory.constraint().child(0))))
                                .withGroupSelectorOfRoot()));
            }, "World history optimization", INFO);
            reportRuntime(() -> {
                network.process(World.WORLD_HISTORY, SolutionView::createStandardAnalysis);
            }, "createStandardAnalysis", INFO);
            waitIndefinitely();
        }, env -> {
        });
    }
}
