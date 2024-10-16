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

import net.splitcells.dem.utils.random.Randomness;
import net.splitcells.gel.data.database.Database;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.solution.Solution;

import static java.util.stream.IntStream.range;
import static net.splitcells.cin.raters.TimeSteps.overlappingTimeSteps;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.utils.random.RandomnessSource.randomness;
import static net.splitcells.gel.data.table.attribute.AttributeI.attribute;
import static net.splitcells.gel.solution.SolutionBuilder.defineProblem;

/**
 * <p>Floats instead of doubles are used, as these are used in game development and
 * seem to be better supported on GPUs.</p>
 * <p>TODO Optional {@link Attribute} are used for now and that is OK,
 * as later the {@link #entities} {@link Database} will be split up into multiple,
 * which will than be represented by a meta {@link Solution}.</p>
 */
public class EntityManager {
    public static final Attribute<Integer> TIME = attribute(Integer.class, "time");
    public static final Attribute<Float> PLAYER = attribute(Float.class, "player");
    public static final Attribute<Float> PLAYER_ATTRIBUTE = attribute(Float.class, "player-attribute");
    public static final Attribute<Float> PLAYER_VALUE = attribute(Float.class, "value");

    public static final float PLAYER_ENERGY = 1f;

    public static EntityManager entityManager() {
        return new EntityManager();
    }

    private final Solution entities;
    private final Database entityDemands;
    private final Database entitySupplies;
    private int initTime = 1;
    private int currentTime = initTime;
    private int nextTime = currentTime + 1;
    private int numberOfPlayers = 100;
    private final Randomness random = randomness();

    private EntityManager() {
        entities = defineProblem("entity-manager")
                .withDemandAttributes(TIME, PLAYER)
                .withNoDemands()
                .withSupplyAttributes(PLAYER_ATTRIBUTE, PLAYER_VALUE)
                .withSupplies()
                .withConstraint(query -> {
                    query.forAll(overlappingTimeSteps(TIME));
                    return query;
                })
                .toProblem()
                .asSolution()
        ;
        entityDemands = entities.demands();
        entitySupplies = entities.supplies();
    }

    public Solution entities() {
        return entities;
    }

    public EntityManager withOptimized() {
        entities.demandsFree().unorderedLines()
                .forEach(fd -> entities.assign(fd, entitySupplies.addTranslated(list(PLAYER_ENERGY, random.integer(1, 100)))));
        return this;
    }

    public EntityManager withIncrementedNextTime() {
        nextTime += 1;
        return this;
    }

    public EntityManager withUpdatedCurrentTime() {
        currentTime = nextTime;
        return this;
    }

    public EntityManager withInitedPlayers() {
        range(0, numberOfPlayers).forEach(i -> entityDemands.addTranslated(list(initTime, i)));
        return this;
    }


    public EntityManager withSuppliedNextTime() {
        entityDemands.lookup(TIME, currentTime).unorderedLinesStream()
                .forEach(demand -> entityDemands.addTranslated(list(nextTime, demand.value(PLAYER))));
        entities.suppliesFree().unorderedLines().forEach(entitySupplies::remove);
        return this;

    }

    public EntityManager withDeletedOldTime() {
        final var oldTime = currentTime - 10;
        final var deletionCandidates = entityDemands.unorderedLines();
        deletionCandidates.forEach(dc -> {
            if (dc.value(TIME) < oldTime) {
                entityDemands.remove(dc);
            }
        });
        return this;
    }
}
