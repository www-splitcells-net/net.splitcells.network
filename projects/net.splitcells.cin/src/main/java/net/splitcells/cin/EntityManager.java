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

import net.splitcells.dem.data.set.list.ListView;
import net.splitcells.dem.utils.random.Randomness;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.data.view.Line;
import net.splitcells.gel.data.view.attribute.Attribute;
import net.splitcells.gel.solution.Solution;
import net.splitcells.gel.solution.optimization.OnlineOptimization;

import java.util.stream.Stream;

import static java.util.stream.IntStream.range;
import static net.splitcells.cin.raters.CommitmentAdherence.commitmentAdherence;
import static net.splitcells.cin.raters.TimeSteps.overlappingTimeSteps;
import static net.splitcells.cin.raters.ValueUpdate.valueUpdate;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.utils.random.RandomnessSource.randomness;
import static net.splitcells.gel.data.view.attribute.AttributeI.attribute;
import static net.splitcells.gel.solution.SolutionBuilder.defineProblem;
import static net.splitcells.gel.solution.optimization.DefaultOptimizationStaging.defaultOptimizationStaging;

/**
 * <p>This is the accounting of the game's objects.</p>
 * <p>Integers instead of floats are used in order to simplify computation, when precision is important.
 * Keep in mind, that this way floating points can still be simulated by i.e. multiplying a number by 10^n.</p>
 * <p>TODO Optional {@link Attribute} are used for now and that is OK,
 * as later the {@link #entities} {@link Table} will be split up into multiple,
 * which will than be represented by a meta {@link Solution}.</p>
 */
public class EntityManager {
    public static final Attribute<Integer> TIME = attribute(Integer.class, "time");
    public static final Attribute<Integer> PLAYER = attribute(Integer.class, "player");
    public static final Attribute<Integer> PLAYER_ATTRIBUTE = attribute(Integer.class, "player-attribute");
    public static final Attribute<Integer> PLAYER_VALUE = attribute(Integer.class, "player-value");
    /**
     * States how the {@link #PLAYER_ATTRIBUTE} should be updated for a given {@link #TIME}.
     * TODO A dictionary of all reserved values is required.
     */
    public static final Attribute<Integer> EVENT_TYPE = attribute(Integer.class, "event-type");
    /**
     * States the thing, that updates the {@link #PLAYER_ATTRIBUTE} for a given {@link #TIME}.
     * TODO A dictionary of all reserved values is required.
     */
    public static final Attribute<Integer> EVENT_SOURCE = attribute(Integer.class, "event-source");
    public static final int EXISTENCE_COST_EVENT_SOURCE = 1;
    /**
     * Such an {@link #EVENT_TYPE}, determines the value, of a {@link #PLAYER_ATTRIBUTE} at a specific {@link #TIME}.
     * In other words, this is the result of the {@link OnlineOptimization} for {@link #entities}.
     */
    public static final int SET_VALUE = 0;
    /**
     * This {@link #EVENT_TYPE} adds a value to a {@link #PLAYER_ATTRIBUTE} at a specific {@link #TIME}.
     */
    public static final int ADD_VALUE = 1;

    /**
     * This {@link #EVENT_TYPE} determines the resulting value of a {@link #PLAYER_ATTRIBUTE}
     * at a specific {@link #TIME}, after applying other events.
     * In other words, this event sets the final value of an {@link #PLAYER_ATTRIBUTE} at a certain {@link #TIME}.
     * Therefore, this also needs to be used, when initial values are set during the first {@link #TIME}
     * aka. the creation of the initial state.
     */
    public static final int RESULT_VALUE = 2;

    /**
     * This {@link #EVENT_TYPE} determines,
     * whether the previously existing value is deleted at the current {@link #TIME}.
     * A {@link #PLAYER_ATTRIBUTE} of 0 indicates no such deletion is taking place,
     * whereas 1 states its deletion.
     */
    public static final int DELETE_VALUE = 3;
    /**
     * This is the default value of {@link #EVENT_SOURCE}.
     */
    public static final int NO_SOURCE = 0;

    /**
     * This is an {@link #PLAYER_ATTRIBUTE}.
     */
    public static final int PLAYER_ENERGY = 1;

    public static EntityManager entityManager() {
        return new EntityManager();
    }

    private final Solution entities;
    private final Table entityDemands;
    private final Table entitySupplies;
    private int currentTime = -1;
    private int nextTime = -1;
    private final Randomness random = randomness();

    /**
     * The {@link Line} are grouped by {@link #PLAYER} first and {@link #TIME} second,
     * in order to improve the performance and overview of the {@link Constraint}.
     */
    private EntityManager() {
        entities = defineProblem("entity-manager")
                .withDemandAttributes(TIME, PLAYER)
                .withNoDemands()
                .withSupplyAttributes(PLAYER_ATTRIBUTE, PLAYER_VALUE, EVENT_TYPE, EVENT_SOURCE)
                .withSupplies()
                .withConstraint(query -> {
                    final var playerTimeSteps = query.forAll(PLAYER).forAll(overlappingTimeSteps(TIME));
                    playerTimeSteps.then(valueUpdate(PLAYER_ENERGY));
                    query.then(commitmentAdherence(TIME));
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
                .forEach(fd -> entitySupplies.addTranslated(list(PLAYER_ENERGY, random.integer(1, 100), RESULT_VALUE, NO_SOURCE)));
        entities.init();
        defaultOptimizationStaging().optimize(entities.optimize());
        return this;
    }

    public EntityManager withOneStepForward() {
        withDemandedNextTime();
        withDeletedOldTime();
        withOptimized();
        entities().init();
        currentTime = nextTime;
        nextTime = currentTime + 1;
        return this;
    }

    public EntityManager withInitedPlayers() {
        return withInitedPlayers(range(0, 3).mapToObj(i -> list(0, i)));
    }

    public EntityManager withInitedPlayers(Stream<ListView<? extends Object>> initialValues) {
        initialValues.forEach(entityDemands::addTranslated);
        withUpdateTimeTrackers();
        return this;
    }

    /**
     * This updates all variables, that are tracking the currently relevant times.
     *
     * @return
     */
    private EntityManager withUpdateTimeTrackers() {
        currentTime = entityDemands.columnView(TIME).stream().max(Integer::compareTo).orElse(0);
        nextTime = currentTime + 1;
        return this;
    }


    public EntityManager withDemandedNextTime() {
        entityDemands.persistedLookup(TIME, currentTime).unorderedLinesStream()
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
