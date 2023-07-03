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

import net.splitcells.cin.raters.deprecated.TimeSteps;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.rating.framework.LocalRating;
import net.splitcells.gel.rating.rater.framework.Rater;
import net.splitcells.gel.rating.rater.framework.RatingEvent;
import net.splitcells.gel.solution.Solution;
import net.splitcells.gel.solution.optimization.OnlineOptimization;

import java.util.Optional;

import static java.util.stream.IntStream.rangeClosed;
import static net.splitcells.cin.raters.CommitmentAdherence.commitmentAdherence;
import static net.splitcells.cin.raters.FundamentalWorldRules.fundamentalWorldRules;
import static net.splitcells.cin.raters.deprecated.CrowdClassifier.crowdClassifier;
import static net.splitcells.cin.raters.deprecated.Dies.dies;
import static net.splitcells.cin.raters.deprecated.Loneliness.loneliness;
import static net.splitcells.cin.raters.deprecated.PlayerValuePersistenceClassifier.playerValuePersistenceClassifier;
import static net.splitcells.cin.raters.deprecated.PositionClusters.positionClustering;
import static net.splitcells.cin.raters.deprecated.TimeSteps.overlappingTimeSteps;
import static net.splitcells.cin.raters.deprecated.TimeSteps.timeSteps;
import static net.splitcells.dem.data.atom.Bools.require;
import static net.splitcells.dem.data.order.Comparators.ASCENDING_INTEGERS;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.gel.data.table.attribute.AttributeI.attribute;
import static net.splitcells.gel.rating.rater.lib.RaterBasedOnLineValue.lineValueRater;
import static net.splitcells.gel.solution.SolutionBuilder.defineProblem;
import static net.splitcells.gel.solution.optimization.primitive.repair.ConstraintGroupBasedRepair.constraintGroupBasedRepair;
import static net.splitcells.gel.solution.optimization.primitive.repair.DemandSelectors.demandSelector;
import static net.splitcells.gel.solution.optimization.primitive.repair.DemandSelectorsConfig.demandSelectorsConfig;
import static net.splitcells.gel.solution.optimization.primitive.repair.RepairConfig.repairConfig;

public class World {
    public static final String WORLD_HISTORY = "world-history";
    public static final Attribute<Integer> WORLD_TIME = attribute(Integer.class, "world-time");
    public static final Attribute<Integer> POSITION_X = attribute(Integer.class, "position-x");
    public static final Attribute<Integer> POSITION_Y = attribute(Integer.class, "position-y");
    public static final Attribute<Integer> VALUE = attribute(Integer.class, "value");
    public static final int MIN_X = 0;
    public static final int MAX_X = 10;
    public static final int MIN_Y = 0;
    public static final int MAX_Y = 10;
    public static final int START_TIME = 0;

    public static OnlineOptimization worldOptimizer(Solution currentWorldHistory) {
        return constraintGroupBasedRepair(
                repairConfig()
                        .withRepairCompliants(false)
                        .withFreeDefyingGroupOfConstraintGroup(false)
                        .withDemandSelector(demandSelector(demandSelectorsConfig()
                                        .withRepairCompliants(false)
                                        .withUseCompleteRating(true)
                                , list(currentWorldHistory.constraint()
                                        , currentWorldHistory.constraint().child(0))))
                        .withGroupSelectorOfRoot());
    }

    public static void initWorldHistory(Solution world) {
        worldsTimeSpace(START_TIME, START_TIME + 1, MIN_X, MAX_X, MIN_Y, MAX_Y)
                .forEach(world.demands()::addTranslated);
        values(START_TIME, START_TIME + 1, MIN_X, MAX_X, MIN_Y, MAX_Y, 0, 1)
                .forEach(world.supplies()::addTranslated);
    }

    public static void addNextTime(Solution world) {
        final var times = world.allocations().columnView(WORLD_TIME)
                .values()
                .stream()
                .sorted(ASCENDING_INTEGERS)
                .collect(toList());
        require(times.size() > 2);
        final var minTime = times.firstValue().orElseThrow();
        final var currentTime = times.lastValue().orElseThrow();
        /* TODO final var obsoleteDemands = world.allocations().demands().lookup(WORLD_TIME, minTime).unorderedLines();
        obsoleteDemands.forEach(od -> world.demands().remove(od));*/
        worldsTimeSpace(currentTime + 1, MIN_X, MAX_X, MIN_Y, MAX_Y)
                .forEach(world.demands()::addTranslated);
        values(currentTime, currentTime, MIN_X, MAX_X, MIN_Y, MAX_Y, 0, 1)
                .forEach(world.supplies()::addTranslated);
    }

    public static void allocateRestAsDead(Solution worldHistory) {
        final var freeDemands = worldHistory.demandsFree().unorderedLinesStream().collect(toList());
        final var playerSupply = worldHistory.suppliesFree().lookup(VALUE, 0).unorderedLinesStream().collect(toList());
        freeDemands.forEach(d -> worldHistory.assign(d, playerSupply.removeAt(0)));
    }

    public static void allocateGlider(Solution worldHistory) {
        worldHistory.assign(worldHistory.demandsFree()
                        .lookup(WORLD_TIME, 0)
                        .lookup(POSITION_X, 1)
                        .lookup(POSITION_Y, 2)
                        .orderedLine(0)
                , worldHistory.suppliesFree()
                        .lookup(VALUE, 1)
                        .orderedLine(0));
        worldHistory.assign(worldHistory.demandsFree()
                        .lookup(WORLD_TIME, 0)
                        .lookup(POSITION_X, 2)
                        .lookup(POSITION_Y, 2)
                        .orderedLine(0)
                , worldHistory.suppliesFree()
                        .lookup(VALUE, 1)
                        .orderedLine(0));
        worldHistory.assign(worldHistory.demandsFree()
                        .lookup(WORLD_TIME, 0)
                        .lookup(POSITION_X, 3)
                        .lookup(POSITION_Y, 2)
                        .orderedLine(0)
                , worldHistory.suppliesFree()
                        .lookup(VALUE, 1)
                        .orderedLine(0));
    }

    public static Solution worldHistory(String name, List<List<Object>> demands, List<List<Object>> supplies) {
        // The name is made so it is portable and easily used as file name in websites, which makes linking easier.
        if (true) {
            return defineProblem(name)
                    .withDemandAttributes(WORLD_TIME, POSITION_X, POSITION_Y)
                    .withDemands(demands)
                    .withSupplyAttributes(VALUE)
                    .withSupplies(supplies)
                    .withConstraint(r -> {
                        r.then(commitmentAdherence(WORLD_TIME));
                        r.then(fundamentalWorldRules(WORLD_TIME, POSITION_X, POSITION_Y, VALUE));
                        return r;
                    })
                    .toProblem()
                    .asSolution();
        } else {
            // TODO REMOVE
            return defineProblem(name)
                    .withDemandAttributes(WORLD_TIME, POSITION_X, POSITION_Y)
                    .withDemands(demands)
                    .withSupplyAttributes(VALUE)
                    .withSupplies(supplies)
                    .withConstraint(r -> {
                        r.then(commitmentAdherence(WORLD_TIME));
                        final var positionTimeSteps = r.forAll(overlappingTimeSteps(WORLD_TIME))
                                .forAll(positionClustering(POSITION_X, POSITION_Y));
                        positionTimeSteps
                                .forAll(isAlive(1, VALUE, WORLD_TIME, POSITION_X, POSITION_Y))
                                .forAll(loneliness(1, VALUE, WORLD_TIME, POSITION_X, POSITION_Y))
                                .then(dies(1, VALUE, WORLD_TIME, POSITION_X, POSITION_Y));
                        positionTimeSteps
                                .forAll(isAlive(1, VALUE, WORLD_TIME, POSITION_X, POSITION_Y))
                                .forAll(hasGoodCompany(1, VALUE, WORLD_TIME, POSITION_X, POSITION_Y))
                                .then(survives(1, VALUE, WORLD_TIME, POSITION_X, POSITION_Y));
                        positionTimeSteps
                                .forAll(isAlive(1, VALUE, WORLD_TIME, POSITION_X, POSITION_Y))
                                .forAll(crowded(1, VALUE, WORLD_TIME, POSITION_X, POSITION_Y))
                                .then(dies(1, VALUE, WORLD_TIME, POSITION_X, POSITION_Y));
                        positionTimeSteps
                                .forAll(isDead(1, VALUE, WORLD_TIME, POSITION_X, POSITION_Y))
                                .forAll(revivalCondition(1, VALUE, WORLD_TIME, POSITION_X, POSITION_Y))
                                .then(reproduction(1, VALUE, WORLD_TIME, POSITION_X, POSITION_Y));
                        return r;
                    }).toProblem()
                    .asSolution();
        }
    }

    private static List<List<Object>> worldsTimeSpace(Integer startTime
            , Integer endTime
            , Integer startX
            , Integer endX
            , Integer startY
            , Integer endY) {
        final List<List<Object>> worldsTimeSpace = list();
        rangeClosed(startTime, endTime).forEach(time ->
                rangeClosed(startX, endX).forEach(x ->
                        rangeClosed(startY, endY).forEach(y ->
                                worldsTimeSpace.add(list(time, x, y)))));
        return worldsTimeSpace;
    }

    private static List<List<Object>> worldsTimeSpace(Integer time
            , Integer startX
            , Integer endX
            , Integer startY
            , Integer endY) {
        final List<List<Object>> worldsTimeSpace = list();
        rangeClosed(startX, endX).forEach(x ->
                rangeClosed(startY, endY).forEach(y ->
                        worldsTimeSpace.add(list(time, x, y))));
        return worldsTimeSpace;
    }

    private static List<List<Object>> values(Integer startTime
            , Integer endTime
            , Integer startX
            , Integer endX
            , Integer startY
            , Integer endY
            , Integer startValue
            , Integer endValue) {
        final List<List<Object>> worldsTimeSpace = list();
        rangeClosed(startTime, endTime).forEach(time ->
                rangeClosed(startX, endX).forEach(x ->
                        rangeClosed(startY, endY).forEach(y ->
                                rangeClosed(startValue, endValue).forEach(value ->
                                        worldsTimeSpace.add(list(value))))));
        return worldsTimeSpace;
    }

    private static void addPosition(Table lines, Line addition, int positionX, int positionY, RatingEvent ratingEvent, LocalRating localRating) {
        retrievePosition(lines, positionX + 1, positionY)
                .ifPresent(neighbour -> ratingEvent.additions().put(addition, localRating));
    }

    private static Optional<Line> retrievePosition(Table lines, int positionX, int positionY) {
        return lines.columnView(POSITION_X)
                .lookup(positionX)
                .columnView(POSITION_Y)
                .lookup(positionY)
                .unorderedLines()
                .lastValue();
    }

    private static Rater crowded(int playerValue
            , Attribute<Integer> playerAttribute
            , Attribute<Integer> timeAttribute
            , Attribute<Integer> xCoordinate
            , Attribute<Integer> yCoordinate) {
        return crowdClassifier(playerValue, playerAttribute, timeAttribute, xCoordinate, yCoordinate
                , playerCount -> playerCount > 3
                , "crowded");
    }

    private static Rater survives(int playerValue
            , Attribute<Integer> playerAttribute
            , Attribute<Integer> timeAttribute
            , Attribute<Integer> xCoordinate
            , Attribute<Integer> yCoordinate) {
        return playerValuePersistenceClassifier(playerValue, playerAttribute, timeAttribute, xCoordinate, yCoordinate
                , (startPlayerValues, endPlayerValues)
                        -> startPlayerValues.anyMatch(s -> s.value(playerAttribute) == playerValue)
                        && endPlayerValues.anyMatch(s -> s.value(playerAttribute) == playerValue)
                , "survives");
    }

    private static Rater isDead(int playerValue
            , Attribute<Integer> playerAttribute
            , Attribute<Integer> timeAttribute
            , Attribute<Integer> xCoordinate
            , Attribute<Integer> yCoordinate) {
        return playerValuePersistenceClassifier(playerValue, playerAttribute, timeAttribute, xCoordinate, yCoordinate,
                (centerStartPositions, centerEndPositions) -> !centerStartPositions
                        .anyMatch(l -> l.value(playerAttribute) == playerValue)
                , "isDead");
    }

    public static Rater isAlive(int playerValue
            , Attribute<Integer> playerAttribute
            , Attribute<Integer> timeAttribute
            , Attribute<Integer> xCoordinate
            , Attribute<Integer> yCoordinate) {
        return playerValuePersistenceClassifier(playerValue, playerAttribute, timeAttribute, xCoordinate, yCoordinate,
                (centerStartPositions, centerEndPositions) -> centerStartPositions
                        .anyMatch(l -> l.value(playerAttribute) == playerValue), "isAlive");
    }

    private static Rater revivalCondition(int playerValue
            , Attribute<Integer> playerAttribute
            , Attribute<Integer> timeAttribute
            , Attribute<Integer> xCoordinate
            , Attribute<Integer> yCoordinate) {
        return crowdClassifier(playerValue, playerAttribute, timeAttribute, xCoordinate, yCoordinate
                , playerCount -> playerCount == 3
                , "revivalCondition");
    }

    private static Rater reproduction(int playerValue
            , Attribute<Integer> playerAttribute
            , Attribute<Integer> timeAttribute
            , Attribute<Integer> xCoordinate
            , Attribute<Integer> yCoordinate) {
        return playerValuePersistenceClassifier(playerValue, playerAttribute, timeAttribute, xCoordinate, yCoordinate
                , (centerStartPositions, centerEndPositions) ->
                        !centerStartPositions.anyMatch(l -> l.value(playerAttribute) == playerValue)
                                && centerEndPositions.anyMatch(l -> l.value(playerAttribute) == playerValue)
                , "reproduction");
    }

    /**
     * Determines if the player {@code playerValue},
     * has 2-3 neighbouring positions with the same {@code playerValue} given a {@link TimeSteps} during the start.
     * {@link #hasGoodCompany} can only be calculated, if the start and end time of the center position is present.
     */
    private static Rater hasGoodCompany(int playerValue
            , Attribute<Integer> playerAttribute
            , Attribute<Integer> timeAttribute
            , Attribute<Integer> xCoordinate
            , Attribute<Integer> yCoordinate) {
        return crowdClassifier(playerValue, playerAttribute, timeAttribute, xCoordinate, yCoordinate
                , playerCount -> 2 == playerCount || playerCount == 3
                , "has good company");
    }
}
