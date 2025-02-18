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
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.resource.communication.log.LogLevel;
import net.splitcells.dem.testing.annotations.BenchmarkTest;
import net.splitcells.dem.testing.annotations.IntegrationTest;
import net.splitcells.dem.testing.annotations.UnitTest;
import net.splitcells.dem.utils.StreamUtils;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.data.lookup.LookupModificationCounter;
import net.splitcells.gel.data.table.TableModificationCounter;

import java.util.stream.IntStream;

import static net.splitcells.cin.EntityManager.*;
import static net.splitcells.cin.raters.CommitmentAdherence.commitmentAdherence;
import static net.splitcells.dem.Dem.configValue;
import static net.splitcells.dem.data.atom.DescribedBool.describedBool;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.resource.Time.measureTimeInNanoSeconds;
import static net.splitcells.dem.resource.Time.nanoToSeconds;
import static net.splitcells.dem.resource.communication.log.Logs.logs;
import static net.splitcells.dem.utils.StreamUtils.streamOf;
import static net.splitcells.gel.Gel.defineProblem;
import static net.splitcells.gel.data.view.attribute.AttributeI.attribute;

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

    /**
     * TODO This should be an integration test, but integration tests are not regularly executed yet.
     */
    @UnitTest
    public void testCommitAdherenceByDefaultOptimization() {
        final var playerAttribute = 1;
        final var entityManager = entityManager();
        entityManager.entities().init();
        entityManager.entities().assign(entityManager.entities().demands().addTranslated(0, 0)
                , entityManager.entities().supplies().addTranslated(playerAttribute, 1, RESULT_VALUE, NO_SOURCE));
        entityManager.withInitedPlayerState();
        entityManager.entities().assign(entityManager.entities().demands().addTranslated(1, 0)
                , entityManager.entities().supplies().addTranslated(playerAttribute, 1, ADD_VALUE, NO_SOURCE));
        entityManager.entities().assign(entityManager.entities().demands().addTranslated(1, 0)
                , entityManager.entities().supplies().addTranslated(playerAttribute, 1, RESULT_VALUE, NO_SOURCE));
        // TODO Thest whether the default optimization changes the result value of the time 1.
        entityManager.withOptimized();
        entityManager.entities().init();
        entityManager.withInitedPlayerState();
        // TODO Thest whether the default optimization changes the result value of the time 1.
    }
}
