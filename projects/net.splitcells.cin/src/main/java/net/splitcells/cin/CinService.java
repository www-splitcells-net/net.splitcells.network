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
import net.splitcells.dem.execution.ThreadLoop;
import net.splitcells.website.server.project.renderer.DiscoverableRenderer;
import net.splitcells.website.server.project.renderer.ObjectsRenderer;

import java.util.Optional;

import static net.splitcells.cin.EntityManager.*;
import static net.splitcells.dem.execution.ThreadLoop.threadLoop;
import static net.splitcells.website.server.project.renderer.ObjectsRenderer.registerObject;

/**
 * TODO Use {@link net.splitcells.gel.ui.Editor} in the future,
 * instead of pure {@link net.splitcells.gel.solution.Solution} or {@link net.splitcells.sep.Network}.
 */
public class CinService implements ResourceOption<Service> {
    @Override
    public Service defaultValue() {
        return new Service() {
            private ThreadLoop threadLoop;

            @Override
            public synchronized void start() {
                final var entityManager = entityManager();
                registerObject(entityManager.entities().discoverableRenderer());
                registerObject(entityManager.entities().demands().discoverableRenderer());
                registerObject(entityManager.entities().supplies().discoverableRenderer());
                entityManager.withInitedPlayers();
                threadLoop = threadLoop("Cin", () -> {
                    entityManager.withOneStepForward();
                });
            }

            @Override
            public synchronized void close() {
                if (threadLoop != null) {
                    threadLoop.stop();
                }
            }

            @Override
            public synchronized void flush() {
                // No resources are present to be flushed.
            }
        };
    }
}
