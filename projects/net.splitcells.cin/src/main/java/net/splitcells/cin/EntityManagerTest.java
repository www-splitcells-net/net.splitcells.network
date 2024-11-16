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
import net.splitcells.dem.resource.communication.log.LogLevel;
import net.splitcells.dem.testing.annotations.BenchmarkTest;
import net.splitcells.gel.data.lookup.LookupModificationCounter;
import net.splitcells.gel.data.table.TableModificationCounter;

import java.util.stream.IntStream;

import static net.splitcells.cin.EntityManager.entityManager;
import static net.splitcells.dem.Dem.configValue;
import static net.splitcells.dem.data.atom.DescribedBool.describedBool;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.resource.Time.measureTimeInNanoSeconds;
import static net.splitcells.dem.resource.Time.nanoToSeconds;
import static net.splitcells.dem.resource.communication.log.Logs.logs;

public class EntityManagerTest {
    @BenchmarkTest
    public void testRuntimeOfOneTimeStep() {
        final int loopCount = 100;
        Dem.process(() -> {
            logs().append("" + nanoToSeconds(measureTimeInNanoSeconds(() -> {
                IntStream.range(0, loopCount).forEach(i -> {
                    final var entityManager = entityManager();
                    entityManager.withInitedPlayers();
                    entityManager.withOneStepForward();
                });
            })) / loopCount, LogLevel.INFO);
        });
    }

    @BenchmarkTest
    public void testRuntimeOfMultipleTimeStep() {
        final int loopCount = 100;
        Dem.process(() -> {
            logs().append("" + nanoToSeconds(measureTimeInNanoSeconds(() -> {
                final var entityManager = entityManager();
                entityManager.withInitedPlayers();
                IntStream.range(0, loopCount).forEach(i -> {
                    entityManager.withOneStepForward();
                });
            })) / loopCount, LogLevel.INFO);
        });
    }

    @BenchmarkTest
    public void testModificationCountOfMultipleTimeStep() {
        final int loopCount = 100;
        Dem.process(() -> {
                    final var entityManager = entityManager();
                    entityManager.withInitedPlayers();
                    IntStream.range(0, loopCount).forEach(i -> {
                        entityManager.withOneStepForward();
                    });
                    final var tableModificationCount = configValue(TableModificationCounter.class).sumCounter().currentCount();
                    final var lookupModificationCounter = configValue(LookupModificationCounter.class).sumCounter().currentCount();
                    logs().append(tree("Modification counters:")
                                    .withProperty("tableModificationCount", "" + tableModificationCount)
                                    .withProperty("lookupModificationCounter", "" + lookupModificationCounter)
                            , LogLevel.INFO);
                }, env -> env.config()
                        .withInitedOption(TableModificationCounter.class)
                        .withInitedOption(LookupModificationCounter.class)
        );
    }
}
