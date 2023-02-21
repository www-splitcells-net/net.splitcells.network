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

import net.splitcells.cin.raters.TimeSteps;
import net.splitcells.dem.data.atom.Bools;
import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.data.set.map.Pair;
import net.splitcells.dem.environment.config.IsDeterministic;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.gel.GelDev;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.rating.framework.LocalRating;
import net.splitcells.gel.rating.rater.Rater;
import net.splitcells.gel.rating.rater.RatingEvent;
import net.splitcells.gel.solution.Solution;
import net.splitcells.gel.solution.SolutionView;

import java.util.Objects;
import java.util.Optional;

import static java.util.stream.IntStream.rangeClosed;
import static net.splitcells.cin.raters.CrowdDetector.crowdDetector;
import static net.splitcells.cin.raters.Dies.dies;
import static net.splitcells.cin.raters.IsAlive.isAlive;
import static net.splitcells.cin.raters.Loneliness.loneliness;
import static net.splitcells.cin.raters.PlayerValuePersistenceClassifier.playerValuePersistenceClassifier;
import static net.splitcells.cin.raters.PositionClusters.positionClustering;
import static net.splitcells.cin.raters.TimeSteps.overlappingTimeSteps;
import static net.splitcells.cin.raters.TimeSteps.timeSteps;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.data.set.map.Pair.pair;
import static net.splitcells.dem.utils.MathUtils.floorToInt;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.gel.data.table.attribute.AttributeI.attribute;
import static net.splitcells.gel.rating.framework.LocalRatingI.localRating;
import static net.splitcells.gel.rating.rater.RaterBasedOnLineValue.lineValueRater;
import static net.splitcells.gel.rating.rater.RatingEventI.ratingEvent;
import static net.splitcells.gel.rating.type.Cost.noCost;
import static net.splitcells.gel.solution.SolutionBuilder.defineProblem;
import static net.splitcells.gel.solution.optimization.primitive.LinearInitialization.linearInitialization;
import static net.splitcells.sep.Network.network;

public class World {
    public static final String WORLD_HISTORY = "world-history";
    public static final Attribute<Integer> WORLD_TIME = attribute(Integer.class, "world-time");
    public static final Attribute<Integer> POSITION_X = attribute(Integer.class, "position-x");
    public static final Attribute<Integer> POSITION_Y = attribute(Integer.class, "position-y");
    public static final Attribute<Integer> VALUE = attribute(Integer.class, "value");

    public static void main(String... args) {
        GelDev.process(() -> {
            final var network = network();
            network.withNode(WORLD_HISTORY, worldHistory());
            network.withOptimization(WORLD_HISTORY, linearInitialization());
            network.process(WORLD_HISTORY, SolutionView::createStandardAnalysis);
        }, env -> env.config().withConfigValue(IsDeterministic.class, Optional.of(Bools.truthful())));
    }

    public static Solution worldHistory() {
        // The name is made so it is portable and easily used as file name in websites, which makes linking easier.
        return defineProblem("conway-s-game-of-life")
                .withDemandAttributes(WORLD_TIME)
                .withDemands(worldsTimeSpace(0))
                .withSupplyAttributes(POSITION_X, POSITION_Y, VALUE)
                .withSupplies(worldWithGlider())
                .withConstraint(r -> {
                    r.forAll(overlappingTimeSteps(WORLD_TIME))
                            .forAll(positionClustering(POSITION_X, POSITION_Y))
                            .forAll(isAlive(1, VALUE, WORLD_TIME, POSITION_X, POSITION_Y))
                            .forAll(loneliness(1, VALUE, WORLD_TIME, POSITION_X, POSITION_Y))
                            .then(dies(1, VALUE, WORLD_TIME, POSITION_X, POSITION_Y));
                    r.forAll(overlappingTimeSteps(WORLD_TIME))
                            .forAll(positionClustering(POSITION_X, POSITION_Y))
                            .forAll(isAlive(1, VALUE, WORLD_TIME, POSITION_X, POSITION_Y))
                            .forAll(goodCompany(1, VALUE, WORLD_TIME, POSITION_X, POSITION_Y))
                            .then(survives(1, VALUE, WORLD_TIME, POSITION_X, POSITION_Y));
                    r.forAll(overlappingTimeSteps(WORLD_TIME))
                            .forAll(positionClustering(POSITION_X, POSITION_Y))
                            .forAll(isAlive(1, VALUE, WORLD_TIME, POSITION_X, POSITION_Y))
                            .forAll(crowded(1, VALUE, WORLD_TIME, POSITION_X, POSITION_Y))
                            .then(dies(1, VALUE, WORLD_TIME, POSITION_X, POSITION_Y));
                    r.forAll(overlappingTimeSteps(WORLD_TIME))
                            .forAll(positionClustering(POSITION_X, POSITION_Y))
                            .forAll(isDead(1, VALUE, WORLD_TIME, POSITION_X, POSITION_Y))
                            .forAll(revivalCondition(1, VALUE, WORLD_TIME, POSITION_X, POSITION_Y))
                            .then(reproduction(1, VALUE, WORLD_TIME, POSITION_X, POSITION_Y));
                    return r;
                }).toProblem()
                .asSolution();
    }

    private static List<List<Object>> worldsTimeSpace(Integer worldTime) {
        final List<List<Object>> worldsTimeSpace = list();
        rangeClosed(-10, 0).forEach(i -> {
            rangeClosed(-10, 0).forEach(j -> {
                worldsTimeSpace.add(list(worldTime));
            });
        });
        return worldsTimeSpace;
    }

    @SuppressWarnings("unchecked")
    private static List<List<Object>> worldWithGlider() {
        final List<List<Object>> worldWithGlider = list();
        worldWithGlider.withAppended(
                list(1, 0, 1)
                , list(2, -1, 1)
                , list(3, -1, 1)
                , list(1, -2, 1)
                , list(2, -2, 1));
        rangeClosed(-10, 0).forEach(i -> {
            rangeClosed(-10, 0).forEach(j -> {
                if (!worldWithGlider.contains(list(i, j, 1))) {
                    worldWithGlider.add(list(i, j, 0));
                }
            });
        });
        return worldWithGlider;
    }

    private static Rater positionClusters() {
        return new Rater() {

            private final Map<Pair<Integer, Integer>, GroupId> neighbourhoods = map();

            @Override
            public List<Domable> arguments() {
                throw notImplementedYet();
            }

            @Override
            public void addContext(Discoverable context) {
                throw notImplementedYet();
            }

            @Override
            public Set<List<String>> paths() {
                throw notImplementedYet();
            }

            @Override
            public RatingEvent ratingAfterAddition(Table lines, Line addition, List<Constraint> children, Table ratingsBeforeAddition) {
                final var ratingEvent = ratingEvent();
                final var positionX = addition.value(POSITION_X);
                final var positionY = addition.value(POSITION_Y);
                final var xNeighbourhood = floorToInt(positionX / 3d);
                final var yNeighbourhood = floorToInt(positionY / 3d);
                final var neighbourhoodId = pair(xNeighbourhood, yNeighbourhood);
                final var neighbourhoodGroup = neighbourhoods.computeIfAbsent(neighbourhoodId, key -> GroupId.group(key.getKey() + ", " + key.getValue()));
                final var localRating = localRating()
                        .withRating(noCost())
                        .withPropagationTo(children)
                        .withResultingGroupId(neighbourhoodGroup);
                ratingEvent.additions().put(addition, localRating);
                addPosition(lines, addition, positionX + 1, positionY, ratingEvent, localRating);
                addPosition(lines, addition, positionX + 1, positionY - 1, ratingEvent, localRating);
                addPosition(lines, addition, positionX, positionY - 1, ratingEvent, localRating);
                addPosition(lines, addition, positionX - 1, positionY - 1, ratingEvent, localRating);
                addPosition(lines, addition, positionX - 1, positionY, ratingEvent, localRating);
                addPosition(lines, addition, positionX - 1, positionY + 1, ratingEvent, localRating);
                addPosition(lines, addition, positionX, positionY + 1, ratingEvent, localRating);
                addPosition(lines, addition, positionX + 1, positionY + 1, ratingEvent, localRating);

                addNeighbourhoodPosition(lines, addition, xNeighbourhood, yNeighbourhood + 1, ratingEvent, children);
                addNeighbourhoodPosition(lines, addition, xNeighbourhood + 1, yNeighbourhood + 1, ratingEvent, children);
                addNeighbourhoodPosition(lines, addition, xNeighbourhood + 1, yNeighbourhood, ratingEvent, children);
                addNeighbourhoodPosition(lines, addition, xNeighbourhood + 1, yNeighbourhood - 1, ratingEvent, children);
                addNeighbourhoodPosition(lines, addition, xNeighbourhood, yNeighbourhood - 1, ratingEvent, children);
                addNeighbourhoodPosition(lines, addition, xNeighbourhood - 1, yNeighbourhood - 1, ratingEvent, children);
                addNeighbourhoodPosition(lines, addition, xNeighbourhood - 1, yNeighbourhood, ratingEvent, children);
                addNeighbourhoodPosition(lines, addition, xNeighbourhood - 1, yNeighbourhood + 1, ratingEvent, children);
                return ratingEvent;
            }

            @Override
            public RatingEvent rating_before_removal(Table lines, Line removal, List<Constraint> children, Table ratingsBeforeRemoval) {
                return ratingEvent();
            }

            private void addNeighbourhoodPosition(Table lines, Line addition, int neighbourhoodPositionX, int neighbourhoodPositionY, RatingEvent ratingEvent, List<Constraint> children) {
                final var neighbourhood = neighbourhoods.get(pair(neighbourhoodPositionX, neighbourhoodPositionY));
                if (neighbourhood != null) {
                    final var localRating = localRating()
                            .withRating(noCost())
                            .withPropagationTo(children)
                            .withResultingGroupId(neighbourhood);
                    ratingEvent.additions().put(addition, localRating);
                }
            }
        };
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
                .lines()
                .lastValue();
    }

    private static Rater crowded(int playerValue
            , Attribute<Integer> playerAttribute
            , Attribute<Integer> timeAttribute
            , Attribute<Integer> xCoordinate
            , Attribute<Integer> yCoordinate) {
        return crowdDetector(playerValue, playerAttribute, timeAttribute, xCoordinate, yCoordinate,
                playerCount -> playerCount < 3);
    }

    private static Rater survives(int playerValue
            , Attribute<Integer> playerAttribute
            , Attribute<Integer> timeAttribute
            , Attribute<Integer> xCoordinate
            , Attribute<Integer> yCoordinate) {
        return playerValuePersistenceClassifier(playerValue, playerAttribute, timeAttribute, xCoordinate, yCoordinate,
                Objects::equals);
    }

    private static Rater isDead(int playerValue
            , Attribute<Integer> playerAttribute
            , Attribute<Integer> timeAttribute
            , Attribute<Integer> xCoordinate
            , Attribute<Integer> yCoordinate) {
        return playerValuePersistenceClassifier(playerValue, playerAttribute, timeAttribute, xCoordinate, yCoordinate,
                (startPlayerValue, endPlayerValue) -> endPlayerValue == playerValue);
    }

    private static Rater revivalCondition(int playerValue
            , Attribute<Integer> playerAttribute
            , Attribute<Integer> timeAttribute
            , Attribute<Integer> xCoordinate
            , Attribute<Integer> yCoordinate) {
        return crowdDetector(playerValue, playerAttribute, timeAttribute, xCoordinate, yCoordinate,
                playerCount -> playerCount == 3);
    }

    private static Rater reproduction(int playerValue
            , Attribute<Integer> playerAttribute
            , Attribute<Integer> timeAttribute
            , Attribute<Integer> xCoordinate
            , Attribute<Integer> yCoordinate) {
        return playerValuePersistenceClassifier(playerValue, playerAttribute, timeAttribute, xCoordinate, yCoordinate,
                (startPlayerValue, endPlayerValue) -> endPlayerValue == playerValue && startPlayerValue != playerValue);
    }

    /**
     * Determines if the player {@code playerValue},
     * has 2-3 neighbouring positions with the same {@code playerValue} given a {@link TimeSteps} during the start.
     * {@link #goodCompany} can only be calculated, if the start and end time of the center position is present.
     */
    private static Rater goodCompany(int playerValue
            , Attribute<Integer> playerAttribute
            , Attribute<Integer> timeAttribute
            , Attribute<Integer> xCoordinate
            , Attribute<Integer> yCoordinate) {
        return crowdDetector(playerValue, playerAttribute, timeAttribute, xCoordinate, yCoordinate,
                playerCount -> 2 <= playerCount && playerCount <= 3);
    }
}
