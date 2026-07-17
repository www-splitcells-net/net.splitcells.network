/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.cin;

import net.splitcells.dem.Dem;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.environment.resource.ResourceOption;
import net.splitcells.dem.environment.resource.Service;
import net.splitcells.dem.execution.ThreadLoop;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.website.server.project.renderer.DiscoverableRenderer;
import net.splitcells.website.server.project.renderer.ObjectsRenderer;

import java.util.Optional;

import static java.util.stream.IntStream.range;
import static net.splitcells.cin.EntityManager.*;
import static net.splitcells.dem.Dem.configValue;
import static net.splitcells.dem.Dem.executeThread;
import static net.splitcells.dem.execution.ThreadLoop.threadLoop;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.website.server.project.renderer.ObjectsRenderer.registerObject;

/**
 * TODO Use {@link net.splitcells.gel.editor.Editor} in the future,
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
                entityManager.withInitedPlayers();
                if (configValue(CinServiceInitTest.class)) {
                    executeThread(CinService.class, () ->
                            range(0, 10).forEach(i -> entityManager.withOneStepForward()));
                } else {
                    threadLoop = threadLoop(CinService.class, () -> {
                        entityManager.withOneStepForward();
                    });
                }
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

    @Override public Optional<Tree> serialize(Service currentValue) {
        return Optional.of(tree(getClass() + " is enabled."));
    }
}
