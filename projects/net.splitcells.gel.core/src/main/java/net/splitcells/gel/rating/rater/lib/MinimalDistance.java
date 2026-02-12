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
package net.splitcells.gel.rating.rater.lib;

import static java.util.stream.IntStream.range;
import static java.util.stream.IntStream.rangeClosed;
import static net.splitcells.dem.data.order.Comparators.ASCENDING_DOUBLES;
import static net.splitcells.dem.data.order.Comparators.ASCENDING_INTEGERS;
import static net.splitcells.dem.data.set.Sets.toSetOfUniques;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.utils.MathUtils.absolute;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.gel.constraint.Constraint.LINE;
import static net.splitcells.gel.constraint.Constraint.RATING;
import static net.splitcells.gel.rating.rater.framework.RatingEventI.ratingEvent;
import static net.splitcells.gel.rating.type.Cost.cost;
import static net.splitcells.gel.rating.type.Cost.noCost;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import net.splitcells.dem.data.order.Comparison;
import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.environment.config.StaticFlags;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.dem.utils.CommonFunctions;
import net.splitcells.dem.utils.MathUtils;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.gel.data.view.Line;
import net.splitcells.gel.data.view.View;
import net.splitcells.gel.data.view.attribute.Attribute;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.rating.framework.Rating;
import net.splitcells.gel.rating.rater.framework.Rater;
import net.splitcells.gel.rating.rater.framework.RatingEvent;

/**
 * The cost value of defiance for a {@link GroupId} is equals to the sum of additionally required distances.
 */
public class MinimalDistance<T> implements Rater {

    public static final String MINIMAL_DISTANCE_NAME = "minimalDistance";

    /**
     * This is a dedicated constant, so that {@link #equals(Object)} is working expectedly,
     * when call on itself.
     */
    private static final BiFunction<Double, Double, Double> LINEAR_DOUBLE_DISTANCE = new BiFunction<>() {
        @Override public Double apply(Double a, Double b) {
            return MathUtils.distance(a, b);
        }

        @Override public String toString() {
            return "Linear distance";
        }
    };

    /**
     * This is a dedicated constant, so that {@link #equals(Object)} is working expectedly,
     * when call on itself.
     */
    private static final BiFunction<Integer, Integer, Double> LINEAR_INTEGER_DISTANCE = new BiFunction<>() {
        @Override public Double apply(Integer a, Integer b) {
            return MathUtils.distance(a, b);
        }

        @Override public String toString() {
            return "Linear distance";
        }
    };

    public static MinimalDistance<Integer> hasMinimalDistanceOf(Attribute<Integer> attribute, double minimumDistance) {
        return minimalDistance(attribute, minimumDistance, ASCENDING_INTEGERS, LINEAR_INTEGER_DISTANCE);
    }

    public static MinimalDistance<Double> minimalDistance(Attribute<Double> attribute, double minimumDistance) {
        return minimalDistance(attribute, minimumDistance, ASCENDING_DOUBLES, LINEAR_DOUBLE_DISTANCE);
    }

    public static <R> MinimalDistance<R> minimalDistance(Attribute<R> attribute
            , double minimumDistance
            , Comparison<R> comparison
            , BiFunction<R, R, Double> distanceMeasurer) {
        return new MinimalDistance<>(attribute, minimumDistance, comparison, distanceMeasurer);
    }

    private final double minimumDistance;
    private final Attribute<T> attribute;
    private final Comparison<T> comparison;
    private final BiFunction<T, T, Double> distanceMeasurer;
    private final List<Discoverable> contextes = list();

    @Override public Tree toTree() {
        return tree("Minimal distance")
                .withProperty("Minimum distance", minimumDistance + "")
                .withProperty("Attribute", attribute.toTree())
                .withProperty("Comparator", comparison + "")
                .withProperty("Distance measurer", distanceMeasurer + "");
    }

    private MinimalDistance
            (Attribute<T> attribute, double minimumDistance
                    , Comparison<T> comparison
                    , BiFunction<T, T, Double> distanceMeasurer) {
        this.distanceMeasurer = distanceMeasurer;
        this.attribute = attribute;
        this.minimumDistance = minimumDistance;
        this.comparison = comparison;
    }

    @Override
    public RatingEvent rating_before_removal
            (View lines
                    , Line removal
                    , net.splitcells.dem.data.set.list.List<Constraint> children
                    , View ratingsBeforeRemoval) {
        if (StaticFlags.ENFORCING_UNIT_CONSISTENCY) {
            checkConsistency(ratingsBeforeRemoval);
        }
        final var sortedLines = sortedStream(lines)
                .filter(e -> !e.value(LINE).equals(removal.value(LINE)))
                .collect(toList());
        return rateDistance(sortedLines, children, Optional.empty());
    }

    @Override
    public RatingEvent ratingAfterAddition(View lines
            , Line addition
            , List<Constraint> children
            , View ratingsBeforeAddition) {
        if (StaticFlags.ENFORCING_UNIT_CONSISTENCY) {
            checkConsistency(ratingsBeforeAddition);
        }
        return rateDistance(sorted(lines), children, Optional.of(addition));
    }

    private RatingEvent rateDistance(List<Line> sortedLines, List<Constraint> children, Optional<Line> potentialNewLine) {
        final var ratingEvent = ratingEvent();
        range(0, sortedLines.size()).forEach(i -> {
            final var currentLine = sortedLines.get(i);
            potentialNewLine.ifPresentOrElse(newLine -> {
                        if (!newLine.equalsTo(currentLine)) {
                            ratingEvent.removal().add(sortedLines.get(i).value(LINE));
                        }
                    },
                    () -> ratingEvent.removal().add(sortedLines.get(i).value(LINE)));
            ratingEvent.addRating_viaAddition
                    (currentLine
                            , noCost()
                            , children
                            , Optional.empty());
            range(0, i).forEach(left -> {
                final var pairRating = pairRating(sortedLines.get(left), currentLine);
                // TODO PERFORAMNCE Only check rating, where it is needed.
                ratingEvent.addRating_viaAddition
                        (currentLine
                                , pairRating
                                , children
                                , Optional.empty());
            });
            range(i + 1, sortedLines.size()).forEach(right -> {
                final var pairRating = pairRating(currentLine, sortedLines.get(right));
                // TODO PERFORAMNCE Only check rating, where it is needed.
                ratingEvent.addRating_viaAddition
                        (currentLine
                                , pairRating
                                , children
                                , Optional.empty());
            });
        });
        return ratingEvent;
    }

    /**
     * TODO Move this to sheath project.
     */
    private void checkConsistency(View lineProcessing) {
        final var sortedProcessingLines = sorted(lineProcessing);
        try {
            if (sortedProcessingLines.size() > 1) {
                final var remainingDistance = minimumDistance
                        - distance(sortedProcessingLines.get(0), sortedProcessingLines.get(1));
                if (remainingDistance > 0) {
                    assertThat(noCost().betterThan(sortedProcessingLines.get(0).value(RATING)))
                            .isTrue();
                } else {
                    assertThat(sortedProcessingLines.get(0).value(RATING).equalz(noCost())).isTrue();
                }
            }
            rangeClosed(1, sortedProcessingLines.size() - 2)
                    .forEach(i -> {
                        final var remainingLeftDistance
                                = minimumDistance
                                - distance(sortedProcessingLines.get(i - 1), sortedProcessingLines.get(i));
                        final var remainingRightDistance
                                = minimumDistance
                                - distance(sortedProcessingLines.get(i), sortedProcessingLines.get(i + 1));
                        final double remainingDistance;
                        if (remainingLeftDistance > 0 && remainingRightDistance <= 0) {
                            remainingDistance = remainingLeftDistance;
                        } else if (remainingRightDistance > 0 && remainingLeftDistance <= 0) {
                            remainingDistance = remainingRightDistance;
                        } else if (remainingLeftDistance > 0 && remainingRightDistance > 0) {
                            remainingDistance = remainingLeftDistance + remainingRightDistance;
                        } else {
                            remainingDistance = 0;
                        }
                        if (remainingDistance > 0) {
                            assertThat(noCost().betterThan(sortedProcessingLines.get(i).value(RATING)))
                                    .isTrue();
                        } else {
                            assertThat(sortedProcessingLines.get(i).value(RATING).equalz(noCost()))
                                    .describedAs("Line at "
                                            + i
                                            + " with value "
                                            + sortedProcessingLines.get(i).value(LINE).value(attribute)
                                            + " should have no cost, but has rating of "
                                            + sortedProcessingLines.get(i).value(RATING)
                                            + ".")
                                    .isTrue();
                        }
                    });
            if (sortedProcessingLines.size() > 2) {
                final var remainingDistance = minimumDistance
                        - distance(sortedProcessingLines.get(sortedProcessingLines.size() - 2)
                        , sortedProcessingLines.get(sortedProcessingLines.size() - 1));
                if (remainingDistance > 0) {
                    assertThat
                            (noCost()
                                    .betterThan(sortedProcessingLines
                                            .get(sortedProcessingLines.size() - 1)
                                            .value(RATING)))
                            .isTrue();
                } else {
                    assertThat
                            (noCost())
                            .isEqualTo(sortedProcessingLines.get(sortedProcessingLines.size() - 1).value(RATING));
                }
            }
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    private Rating pairRating(Line left, Line right) {
        final var actualDistance = distance(left, right);
        if (actualDistance >= minimumDistance) {
            return noCost();
        } else {
            return cost((minimumDistance - actualDistance) / 2.0);
        }
    }

    private double distance(Line left, Line right) {
        return absolute(distanceMeasurer
                .apply(left.value(LINE).value(attribute)
                        , right.value(LINE).value(attribute)));
    }

    private boolean isValid(Line a, Line b) {
        return distance(a, b) >= minimumDistance;
    }

    @Override
    public Class<? extends Rater> type() {
        return MinimalDistance.class;
    }

    @Override
    public List<Domable> arguments() {
        return Lists.list
                (tree("minimumDistance").withChild(tree("" + minimumDistance))
                        , tree("attribute").withChild(attribute.toTree())
                        , tree("comparator").withChild(tree("" + comparison))
                        , tree("distanceMeassurer").withChild(tree("" + distanceMeasurer)));
    }

    @Override
    public boolean equals(Object arg) {
        if (arg instanceof MinimalDistance<?> cArg) {
            return this.minimumDistance == cArg.minimumDistance
                    && this.attribute.equals(cArg.attribute)
                    && this.comparison.equals(cArg.comparison)
                    && this.distanceMeasurer.equals(cArg.distanceMeasurer);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return CommonFunctions.hashCode(minimumDistance, attribute, comparison, distanceMeasurer);
    }

    @Override
    public void addContext(Discoverable context) {
        contextes.add(context);
    }

    @Override
    public Set<List<String>> paths() {
        return contextes.stream().map(Discoverable::path).collect(toSetOfUniques());
    }

    private Stream<Line> sortedStream(View lines) {
        return lines.rawLinesView().stream()
                .filter(e -> e != null)
                .sorted((a, b) ->
                        comparison.compare(a.value(LINE).value(attribute), b.value(LINE).value(attribute)));
    }

    private List<Line> sorted(View lines) {
        return lines.rawLinesView().stream()
                .filter(e -> e != null)
                .sorted((a, b) -> comparison.compare(a.value(LINE).value(attribute)
                        , b.value(LINE).value(attribute)))
                .collect(toList());
    }

    private List<Line> defyingSorted(View lines) {
        final var cost = noCost();
        return lines.rawLinesView().stream()
                .filter(e -> e != null)
                .filter(e -> !e.value(Constraint.RATING).equalz(cost))
                .sorted((a, b) -> comparison.compare(a.value(LINE).value(attribute), b.value(LINE).value(attribute)))
                .collect(toList());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ", " + attribute + ", " + minimumDistance;
    }

    @Override
    public String toSimpleDescription(Line line, View groupsLineProcessing, GroupId incomingGroup) {
        final var sorted = sorted(groupsLineProcessing);
        final var minimalDistance = rangeClosed(0, sorted.size() - 2)
                .filter(i -> sorted.get(i).value(LINE).equalsTo(line) || sorted.get(i + 1).value(LINE).equalsTo(line))
                .mapToObj(i -> distance(sorted.get(i), sorted.get(i + 1)))
                .min(ASCENDING_DOUBLES);
        return minimalDistance.map(distance -> "Has a minimum distance of "
                        + distance
                        + " "
                        + attribute.name()
                        + " but should have at least a minimum distance of "
                        + minimumDistance
                        + " "
                        + attribute.name())
                .orElse("Should have at least a minimum distance of " + minimumDistance + " " + attribute.name());
    }
}
