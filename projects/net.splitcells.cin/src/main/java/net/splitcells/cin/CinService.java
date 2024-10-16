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
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.environment.resource.ResourceOption;
import net.splitcells.dem.environment.resource.Service;
import net.splitcells.website.server.project.renderer.DiscoverableRenderer;
import net.splitcells.website.server.project.renderer.ObjectsRenderer;

import java.util.Optional;

import static net.splitcells.cin.EntityManager.*;
import static net.splitcells.website.server.project.renderer.ObjectsRenderer.registerObject;

/**
 * TODO Use {@link net.splitcells.gel.ui.Editor} in the future,
 * instead of pure {@link net.splitcells.gel.solution.Solution} or {@link net.splitcells.sep.Network}.
 */
public class CinService implements ResourceOption<Service> {
    @Override
    public Service defaultValue() {
        return new Service() {
            private volatile boolean isRunning = false;

            @Override
            public void start() {
                isRunning = true;
                Dem.executeThread("Cin", () -> {
                    final var entityManager = entityManager("entity-manager");
                    registerObject(entityManager.discoverableRenderer());
                    registerObject(entityManager.demands().discoverableRenderer());
                    registerObject(entityManager.supplies().discoverableRenderer());
                    float currentTime = 1f;
                    float nextTime;
                    initPlayers(entityManager, currentTime, 100);
                    while (isRunning) {
                        nextTime = currentTime + 1f;
                        supplyNextTime(entityManager, currentTime, nextTime);
                        deleteOldTime(entityManager, currentTime, currentTime - 10f);
                        currentTime = nextTime;
                        Dem.sleepAtLeast(1000);
                    }
                });
            }

            @Override
            public void close() {
                isRunning = false;
            }

            @Override
            public void flush() {

            }
        };
    }
}
