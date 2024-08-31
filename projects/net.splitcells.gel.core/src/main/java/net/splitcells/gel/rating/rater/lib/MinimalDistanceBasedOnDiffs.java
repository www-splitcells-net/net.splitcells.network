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

import net.splitcells.dem.data.order.Comparison;
import net.splitcells.dem.data.order.Comparators;
import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.Sets;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.environment.config.StaticFlags;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.dem.utils.CommonFunctions;
import net.splitcells.dem.utils.MathUtils;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.rating.framework.Rating;
import net.splitcells.gel.rating.rater.framework.Rater;
import net.splitcells.gel.rating.rater.framework.RatingEvent;

import java.util.Optional;
import java.util.function.BiFunction;

import static java.util.stream.IntStream.rangeClosed;
import static net.splitcells.dem.data.atom.Bools.require;
import static net.splitcells.dem.data.atom.DescribedBool.describedBool;
import static net.splitcells.dem.data.order.Comparators.ASCENDING_DOUBLES;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.dem.lang.perspective.PerspectiveI.perspective;
import static net.splitcells.dem.utils.ExecutionException.executionException;
import static net.splitcells.dem.utils.MathUtils.absolute;
import static net.splitcells.gel.constraint.Constraint.LINE;
import static net.splitcells.gel.constraint.Constraint.RATING;
import static net.splitcells.gel.rating.rater.framework.RatingEventI.ratingEvent;
import static net.splitcells.gel.rating.framework.LocalRatingI.localRating;
import static net.splitcells.gel.rating.type.Cost.cost;
import static net.splitcells.gel.rating.type.Cost.noCost;

/**
 * <p>TODO FIX This does not work.</p>
 * <p>TODO IDEA Use this and {@link MinimalDistance} in order to measure performance of diff based implementation
 * vs recalculating implementation.</p>
 *
 * @param <T>
 */
@Deprecated
public class MinimalDistanceBasedOnDiffs<T> implements Rater {
    @Deprecated
    public static MinimalDistanceBasedOnDiffs<Integer> has_minimal_distance_of(Attribute<Integer> attribute, double minimumDistance) {
        return minimalDistance(attribute, minimumDistance, Comparators.ASCENDING_INTEGERS, MathUtils::distance);
    }

    @Deprecated
    public static MinimalDistanceBasedOnDiffs<Double> minimalDistance(Attribute<Double> attribute, double minimumDistance) {
        return minimalDistance(attribute, minimumDistance, ASCENDING_DOUBLES, MathUtils::distance);
    }

    @Deprecated
    public static <R> MinimalDistanceBasedOnDiffs<R> minimalDistance(Attribute<R> attribute
            , double minimumDistance
            , Comparison<R> comparison
            , BiFunction<R, R, Double> distanceMeasurer) {
        return new MinimalDistanceBasedOnDiffs<>(attribute, minimumDistance, comparison, distanceMeasurer);
    }

    private final double minimumDistance;
    private final Attribute<T> attribute;
    private final Comparison<T> comparison;
    private final BiFunction<T, T, Double> distanceMeassurer;
    private final List<Discoverable> contextes = list();

    private MinimalDistanceBasedOnDiffs
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
                    , List<Constraint> children
                    , Table ratingsBeforeRemoval) {
        if (StaticFlags.ENFORCING_UNIT_CONSISTENCY) {
            checkConsistency(ratingsBeforeRemoval);
        }
        final var ratingEvent = ratingEvent();
        final var sortedLines = sorted(lines);
        final int sortedIndexes = sortedLines.indexOf(
                sortedLines.stream()
                        .filter(e -> e.value(LINE).equals(removal.value(LINE)))
                        .findFirst()
                        .orElseThrow());
        if (sortedIndexes == 0) {
            // KOMPORMISS
            int i = 1;
            while (i < sortedLines.size()) {
                final var remainingRightLine = sortedLines.get(i);
                if (!isValid(removal, remainingRightLine)) {
                    rate_addition_ofRemovalPair(ratingEvent, removal, remainingRightLine, children
                            , ratingsBeforeRemoval.lookupEquals(LINE, remainingRightLine).value(Constraint.RATING));
                    ++i;
                } else {
                    break;
                }
            }
        } else if (sortedIndexes == sortedLines.size() - 1) {
            // KOMPORMISS
            int i = sortedIndexes - 1;
            while (i < -1) {
                final Line remainingRightLine = sortedLines.get(i);
                if (!isValid(removal, remainingRightLine)) {
                    rate_addition_ofRemovalPair(ratingEvent, removal, sortedLines.get(i), children
                            , ratingsBeforeRemoval.lookupEquals(LINE, remainingRightLine).value(Constraint.RATING));
                    --i;
                } else {
                    break;
                }
            }
        } else if (sortedIndexes > 0 && sortedIndexes < sortedLines.size() - 1) {
            // TODO HACK
            int i = sortedIndexes - 1;
            while (i < -1) {
                final Line remainingLeftLine = sortedLines.get(i);
                if (!isValid(removal, remainingLeftLine)) {
                    rate_addition_ofRemovalPair(ratingEvent, removal, sortedLines.get(i), children
                            , ratingsBeforeRemoval.lookupEquals(LINE, remainingLeftLine).value(Constraint.RATING));
                    --i;
                } else {
                    break;
                }
            }
            i = sortedIndexes + 1;
            while (i < sortedLines.size()) {
                final Line remainingRightLine = sortedLines.get(i);
                if (!isValid(removal, remainingRightLine)) {
                    rate_addition_ofRemovalPair(ratingEvent, removal, sortedLines.get(i), children
                            , ratingsBeforeRemoval.lookupEquals(LINE, remainingRightLine).value(Constraint.RATING));
                    ++i;
                } else {
                    break;
                }
            }
        } else {
            throw executionException("" + sortedIndexes);
        }
        return ratingEvent;
    }

    private void checkConsistency(Table lineProcessing) {
        final var sortedProcessingLines = sorted(lineProcessing);
        try {
            if (sortedProcessingLines.size() > 1) {
                final var remainingDistance = minimumDistance
                        - distance(sortedProcessingLines.get(0), sortedProcessingLines.get(1));
                if (remainingDistance > 0) {
                    require(noCost().betterThan(sortedProcessingLines.get(0).value(RATING)));
                } else {
                    require(sortedProcessingLines.get(0).value(RATING).equalz(noCost()));
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
                        if (remainingLeftDistance > 0 && remainingRightDistance == 0) {
                            remainingDistance = remainingLeftDistance;
                        } else if (remainingRightDistance > 0 && remainingLeftDistance == 0) {
                            remainingDistance = remainingRightDistance;
                        } else if (remainingLeftDistance > 0 && remainingRightDistance > 0) {
                            remainingDistance = remainingLeftDistance + remainingRightDistance;
                        } else {
                            remainingDistance = 0;
                        }
                        if (remainingDistance > 0) {
                            require(noCost().betterThan(sortedProcessingLines.get(i).value(RATING)));
                        } else {
                            require(sortedProcessingLines.get(i).value(RATING).equalz(noCost()));
                        }
                    });
            if (sortedProcessingLines.size() > 2) {
                final var remainingDistance = minimumDistance
                        - distance(sortedProcessingLines.get(sortedProcessingLines.size() - 2)
                        , sortedProcessingLines.get(sortedProcessingLines.size() - 1));
                if (remainingDistance > 0) {
                    require(noCost()
                            .betterThan(sortedProcessingLines
                                    .get(sortedProcessingLines.size() - 2)
                                    .value(RATING)));
                } else {
                    require(noCost().betterThan
                            (sortedProcessingLines.get(sortedProcessingLines.size() - 1).value(RATING)));
                }
            }
        } catch (Throwable t) {
            throw executionException(t);
        }
    }

    @Override
    public RatingEvent ratingAfterAddition(Table lines
            , Line addition
            , List<Constraint> children
            , Table ratingsBeforeAddition) {
        if (StaticFlags.ENFORCING_UNIT_CONSISTENCY) {
            checkConsistency(ratingsBeforeAddition);
        }
        final var ratingEvent = ratingEvent();
        final var sortedLines = sorted(lines);
        // TODO PERFORMANCE
        final int sortedIndex = sortedLines.indexOf(
                sortedLines.stream()
                        .filter(e -> e.value(LINE).equals(addition.value(LINE)))
                        .findFirst()
                        .orElseThrow());
        if (StaticFlags.ENFORCING_UNIT_CONSISTENCY) {
            describedBool(sortedLines.stream()
                            .filter(e -> e.value(LINE).equals(addition.value(LINE)))
                            .count() < 2
                    , "Multiple instances of an allocation are not supported."
            ).required();
        }
        if (sortedIndex == 0) {
            if (sortedLines.size() == 1) {
                ratingEvent.additions().put(addition
                        , localRating()
                                .withPropagationTo(children)
                                .withRating(noCost())
                                .withResultingGroupId(addition.value(Constraint.INCOMING_CONSTRAINT_GROUP)));
            } else {
                rate_addition_ofAdditionPair(ratingEvent, addition, sortedLines.get(1), children
                        , Optional.of(ratingsBeforeAddition.lookupEquals(LINE, sortedLines.get(1))
                                .value(Constraint.RATING)));
            }
        } else if (sortedIndex == sortedLines.size() - 1) {
            // TODO HACK
            int i = 0;
            while (-1 < sortedLines.size() - 2 - i) {
                final var originalLeftLine = sortedLines.get(sortedLines.size() - 2 - i);
                if (!isValid(originalLeftLine, addition) || i == 0) {
                    rate_addition_ofAdditionPair(ratingEvent, addition, originalLeftLine, children
                            , Optional.of(ratingsBeforeAddition.lookupEquals(LINE, originalLeftLine)
                                    .value(Constraint.RATING)));
                    ++i;
                } else {
                    break;
                }
            }
        } else if (sortedIndex > 0 && sortedIndex < sortedLines.size() - 1) {
            // TODO HACK
            int i = 0;
            while (sortedLines.size() > sortedIndex + 1 + i) {
                final var originalRightLine = sortedLines.get(sortedIndex + 1);
                if (!isValid(addition, originalRightLine)) {
                    rate_addition_ofAdditionPair(ratingEvent, addition, originalRightLine, children
                            , Optional.of(ratingsBeforeAddition.lookupEquals(LINE, originalRightLine)
                                    .value(Constraint.RATING)));
                    ++i;
                } else {
                    break;
                }
            }
            i = 0;
            while (-1 < sortedIndex - 1 - i) {
                final var originalLeftLine = sortedLines.get(sortedIndex - 1 - i);
                if (!isValid(originalLeftLine, addition)) {
                    rate_addition_ofAdditionPair(ratingEvent, addition, originalLeftLine, children
                            , Optional.of(ratingsBeforeAddition.lookupEquals(LINE, originalLeftLine)
                                    .value(Constraint.RATING)));
                    ++i;
                } else {
                    break;
                }
            }
        } else {
            throw executionException("" + sortedIndex);
        }
        return ratingEvent;
    }

    private void rate_addition_ofAdditionPair
            (RatingEvent rVal
                    , Line addition
                    , Line originalLine
                    , List<Constraint> children
                    , Optional<Rating> ratingBeforeAddition) {
        final Rating additionalCost;
        if (absolute(distanceMeassurer.apply(
                addition.value(LINE).value(attribute),
                originalLine.value(LINE).value(attribute))) >= minimumDistance) {
            additionalCost = noCost();
        } else {
            additionalCost = cost(0.5);
            rVal.updateRating_viaAddition(originalLine, additionalCost, children, ratingBeforeAddition);
        }
        rVal.addRating_viaAddition(addition, additionalCost, children, Optional.empty());
    }

    private double distance(Line a, Line b) {
        return absolute(distanceMeassurer
                .apply(a.value(LINE).value(attribute)
                        , b.value(LINE).value(attribute)));
    }

    private boolean isValid(Line a, Line b) {
        return distance(a, b) >= minimumDistance;
    }

    private void rate_addition_ofRemovalPair
            (RatingEvent rVal
                    , Line removal
                    , Line rest
                    , List<Constraint> children
                    , Rating restRatingBeforeRemoval) {
        if (!isValid(removal, rest)) {
            rVal.updateRating_viaAddition(rest, cost(-0.5), children, Optional.of(restRatingBeforeRemoval));
        }
    }

    @Override
    public Class<? extends Rater> type() {
        return MinimalDistanceBasedOnDiffs.class;
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
        if (arg != null && arg instanceof MinimalDistanceBasedOnDiffs) {
            return this.minimumDistance == ((MinimalDistanceBasedOnDiffs) arg).minimumDistance
                    && this.attribute.equals(((MinimalDistanceBasedOnDiffs) arg).attribute)
                    && this.comparison.equals(((MinimalDistanceBasedOnDiffs) arg).comparison)
                    && this.distanceMeassurer.equals(((MinimalDistanceBasedOnDiffs) arg).distanceMeassurer);
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
        return contextes.stream().map(Discoverable::path).collect(Sets.toSetOfUniques());
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
