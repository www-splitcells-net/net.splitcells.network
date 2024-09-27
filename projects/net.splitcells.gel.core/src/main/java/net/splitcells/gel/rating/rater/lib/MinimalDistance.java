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
import static net.splitcells.dem.lang.tree.TreeI.perspective;
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
import net.splitcells.dem.utils.CommonFunctions;
import net.splitcells.dem.utils.MathUtils;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.data.table.attribute.Attribute;
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
    public static MinimalDistance<Integer> has_minimal_distance_of(Attribute<Integer> attribute, double minimumDistance) {
        return minimalDistance(attribute, minimumDistance, ASCENDING_INTEGERS, MathUtils::distance);
    }

    public static MinimalDistance<Double> minimalDistance(Attribute<Double> attribute, double minimumDistance) {
        return minimalDistance(attribute, minimumDistance, ASCENDING_DOUBLES, MathUtils::distance);
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
    private final BiFunction<T, T, Double> distanceMeassurer;
    private final List<Discoverable> contextes = list();

    private MinimalDistance
            (Attribute<T> attribute, double minimumDistance
                    , Comparison<T> comparison
                    , BiFunction<T, T, Double> distanceMeassurer) {
        this.distanceMeassurer = distanceMeassurer;
        this.attribute = attribute;
        this.minimumDistance = minimumDistance;
        this.comparison = comparison;
    }

    @Override
    public RatingEvent rating_before_removal
            (Table lines
                    , Line removal
                    , net.splitcells.dem.data.set.list.List<Constraint> children
                    , Table ratingsBeforeRemoval) {
        if (StaticFlags.ENFORCING_UNIT_CONSISTENCY) {
            checkConsistency(ratingsBeforeRemoval);
        }
        final var sortedLines = sortedStream(lines)
                .filter(e -> !e.value(LINE).equals(removal.value(LINE)))
                .collect(toList());
        return rateDistance(sortedLines, children, Optional.empty());
    }

    @Override
    public RatingEvent ratingAfterAddition(Table lines
            , Line addition
            , List<Constraint> children
            , Table ratingsBeforeAddition) {
        if (StaticFlags.ENFORCING_UNIT_CONSISTENCY) {
            checkConsistency(ratingsBeforeAddition);
        }
        return rateDistance(sorted(lines), children, Optional.of(addition));
    }

    private RatingEvent rateDistance(List<Line> sortedLines, List<Constraint> children, Optional<Line> potential_new_line) {
        final var ratingEvent = ratingEvent();
        range(0, sortedLines.size()).forEach(i -> {
            final var current_line = sortedLines.get(i);
            potential_new_line.ifPresentOrElse(new_line -> {
                        if (!new_line.equalsTo(current_line)) {
                            ratingEvent.removal().add(sortedLines.get(i).value(LINE));
                        }
                    },
                    () -> ratingEvent.removal().add(sortedLines.get(i).value(LINE)));
            ratingEvent.addRating_viaAddition
                    (current_line
                            , noCost()
                            , children
                            , Optional.empty());
            range(0, i).forEach(left -> {
                final var pairRating = pairRating(sortedLines.get(left), current_line);
                // TODO PERFORAMNCE Only check rating, where it is needed.
                ratingEvent.addRating_viaAddition
                        (current_line
                                , pairRating
                                , children
                                , Optional.empty());
            });
            range(i + 1, sortedLines.size()).forEach(right -> {
                final var pairRating = pairRating(current_line, sortedLines.get(right));
                // TODO PERFORAMNCE Only check rating, where it is needed.
                ratingEvent.addRating_viaAddition
                        (current_line
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
    private void checkConsistency(Table lineProcessing) {
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
        return absolute(distanceMeassurer
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
                (perspective("minimumDistance").withChild(perspective("" + minimumDistance))
                        , perspective("attribute").withChild(attribute.toPerspective())
                        , perspective("comparator").withChild(perspective("" + comparison))
                        , perspective("distanceMeassurer").withChild(perspective("" + distanceMeassurer)));
    }

    @Override
    public boolean equals(Object arg) {
        if (arg != null && arg instanceof MinimalDistance) {
            return this.minimumDistance == ((MinimalDistance) arg).minimumDistance
                    && this.attribute.equals(((MinimalDistance) arg).attribute)
                    && this.comparison.equals(((MinimalDistance) arg).comparison)
                    && this.distanceMeassurer.equals(((MinimalDistance) arg).distanceMeassurer);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return CommonFunctions.hashCode(minimumDistance, attribute, comparison, distanceMeassurer);
    }

    @Override
    public void addContext(Discoverable context) {
        contextes.add(context);
    }

    @Override
    public Set<List<String>> paths() {
        return contextes.stream().map(Discoverable::path).collect(toSetOfUniques());
    }

    private Stream<Line> sortedStream(Table lines) {
        return lines.rawLinesView().stream()
                .filter(e -> e != null)
                .sorted((a, b) -> {
                            try {
                                return comparison.compare
                                        (a.value(LINE).value(attribute)
                                                , b.value(LINE).value(attribute));
                            } catch (RuntimeException e) {
                                throw e;
                            }
                        }
                );
    }

    private List<Line> sorted(Table lines) {
        return lines.rawLinesView().stream()
                .filter(e -> e != null)
                .sorted((a, b) -> {
                            try {
                                return comparison.compare
                                        (a.value(LINE).value(attribute)
                                                , b.value(LINE).value(attribute));
                            } catch (RuntimeException e) {
                                throw e;
                            }
                        }
                )
                .collect(toList());
    }

    private List<Line> defyingSorted(Table lines) {
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
    public String toSimpleDescription(Line line, Table groupsLineProcessing, GroupId incomingGroup) {
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
