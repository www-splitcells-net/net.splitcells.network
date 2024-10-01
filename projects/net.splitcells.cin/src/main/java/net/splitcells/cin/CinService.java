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

import net.splitcells.dem.Dem;
import net.splitcells.dem.environment.resource.ResourceOption;
import net.splitcells.dem.environment.resource.ResourceOptionI;
import net.splitcells.dem.environment.resource.Service;

import static net.splitcells.cin.World.WORLD_HISTORY;
import static net.splitcells.cin.World.addNextTime;
import static net.splitcells.cin.World.allocateBlinker;
import static net.splitcells.cin.World.allocateRestAsDead;
import static net.splitcells.cin.World.initWorldHistory;
import static net.splitcells.cin.World.worldHistory;
import static net.splitcells.cin.World.worldOptimizer;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.resource.communication.log.LogLevel.INFO;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.dem.utils.Time.reportRuntime;
import static net.splitcells.gel.solution.optimization.primitive.OnlineLinearInitialization.onlineLinearInitialization;
import static net.splitcells.sep.Network.network;

public class CinService implements ResourceOption<Service> {
    @Override
    public Service defaultValue() {
        return new Service() {
            @Override
            public void start() {
                Dem.executeThread("Cin", () -> {
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
                        network.withExecution(WORLD_HISTORY, wh -> {
                            addNextTime(wh);
                            wh.init();
                        });
                        network.withOptimization(WORLD_HISTORY, onlineLinearInitialization());
                        network.withOptimization(WORLD_HISTORY, worldOptimizer(network.node(WORLD_HISTORY)));
                    }, "World history optimization", INFO);
                });
            }

            @Override
            public void close() {
                throw notImplementedYet();
            }

            @Override
            public void flush() {
                // There is no known flush operation yet for this service.
            }
        };
    }
}
