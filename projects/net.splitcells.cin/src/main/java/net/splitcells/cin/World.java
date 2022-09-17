/*
 * Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License,
 * which is available at https://spdx.org/licenses/MIT.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 */
package net.splitcells.cin;

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

import java.util.Optional;

import static java.util.stream.IntStream.rangeClosed;
import static net.splitcells.cin.TimeSteps.timeSteps;
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
        return defineProblem("Conway's Game of Life")
                .withDemandAttributes(WORLD_TIME)
                .withDemands(worldsTimeSpace(0))
                .withSupplyAttributes(POSITION_X, POSITION_Y, VALUE)
                .withSupplies(worldWithGlider())
                .withConstraint(r -> {
                    r.forAll(timeSteps(WORLD_TIME));
                    // TODO r.forAll(timeSteps()).forAll(positionClusters()).forAll(isAlive()).forAll(loneliness()).then(dies());
                    // TODO r.forAll(timeSteps()).forAll(positionClusters()).forAll(isAlive()).forAll(goodCompany()).then(survives());
                    // TODO r.forAll(timeSteps()).forAll(positionClusters()).forAll(isAlive()).forAll(crowded()).then(dies());
                    // TODO r.forAll(timeSteps()).forAll(positionClusters()).forAll(isDead()).forAll(revivalCondition()).then(becomesAlive());
                    // TODO r.forAll(timeSteps()).forAll(positionClusters()).then(unchanged());
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

    private static Rater crowded() {
        throw notImplementedYet();
    }

    private static Rater survives() {
        throw notImplementedYet();
    }

    private static Rater isAlive() {
        return lineValueRater(line -> line.value(VALUE) > 0);
    }

    private static Rater isDead() {
        throw notImplementedYet();
    }

    private static Rater revivalCondition() {
        throw notImplementedYet();
    }

    private static Rater becomesAlive() {
        throw notImplementedYet();
    }

    private static Rater loneliness() {
        throw notImplementedYet();
    }

    private static Rater dies() {
        throw notImplementedYet();
    }

    private static Rater unchanged() {
        throw notImplementedYet();
    }

    private static Rater goodCompany() {
        throw notImplementedYet();
    }
}
