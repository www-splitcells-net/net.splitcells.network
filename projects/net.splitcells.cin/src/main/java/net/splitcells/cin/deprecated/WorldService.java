/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.cin.deprecated;

import net.splitcells.gel.ext.GelExtCell;
import net.splitcells.gel.solution.SolutionView;

import static net.splitcells.cin.deprecated.World.WORLD_HISTORY;
import static net.splitcells.cin.deprecated.World.addNextTime;
import static net.splitcells.cin.deprecated.World.allocateBlinker;
import static net.splitcells.cin.deprecated.World.allocateRestAsDead;
import static net.splitcells.cin.deprecated.World.initWorldHistory;
import static net.splitcells.cin.deprecated.World.worldHistory;
import static net.splitcells.cin.deprecated.World.worldOptimizer;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.resource.communication.log.LogLevel.INFO;
import static net.splitcells.dem.utils.Time.reportRuntime;
import static net.splitcells.gel.solution.optimization.primitive.OnlineLinearInitialization.onlineLinearInitialization;
import static net.splitcells.sep.Network.network;

public class WorldService {
    public static void main(String... args) {
        GelExtCell.process(() -> {
            final var network = network();
            final var currentWorldHistory = worldHistory(WORLD_HISTORY, list(), list());
            reportRuntime(() -> {
                network.withNode(WORLD_HISTORY, currentWorldHistory);
                initWorldHistory(currentWorldHistory);
                allocateBlinker(currentWorldHistory);
                currentWorldHistory.init();
                allocateRestAsDead(currentWorldHistory);
            }, "Initialize world history.", INFO);
            reportRuntime(() -> {
                network.withOptimization(WORLD_HISTORY, onlineLinearInitialization());
                network.withOptimization(WORLD_HISTORY, worldOptimizer(network.node(WORLD_HISTORY)));
            }, "Initial world history optimization", INFO);
            reportRuntime(() -> {
                network.process(WORLD_HISTORY, SolutionView::createStandardAnalysis);
            }, "Execute initial createStandardAnalysis.", INFO);
            while (true) {
                reportRuntime(() -> {
                    network.withExecution(WORLD_HISTORY, wh -> {
                        addNextTime(wh);
                        wh.init();
                    });
                    network.withOptimization(WORLD_HISTORY, onlineLinearInitialization());
                    network.withOptimization(WORLD_HISTORY, worldOptimizer(network.node(WORLD_HISTORY)));
                }, "World history optimization", INFO);
                reportRuntime(() -> {
                    network.process(WORLD_HISTORY, SolutionView::createStandardAnalysis);
                }, "Execute createStandardAnalysis.", INFO);
            }
        }, env -> {
        });
    }
}
