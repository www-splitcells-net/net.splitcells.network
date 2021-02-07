package net.splitcells.gel.rating.rater;

import static java.lang.Math.abs;
import static java.util.Comparator.naturalOrder;
import static java.util.stream.IntStream.range;
import static java.util.stream.IntStream.rangeClosed;
import static net.splitcells.dem.data.order.Comparator.comparator_;
import static net.splitcells.dem.data.order.Ordering.GREATER_THAN;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.dem.utils.Not_implemented_yet.not_implemented_yet;
import static net.splitcells.gel.constraint.Constraint.LINE;
import static net.splitcells.gel.constraint.Constraint.RATING;
import static net.splitcells.gel.rating.rater.RatingEventI.ratingEvent;
import static net.splitcells.gel.rating.type.Cost.cost;
import static net.splitcells.gel.rating.type.Cost.noCost;
import static net.splitcells.gel.rating.structure.LocalRatingI.localRating;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import net.splitcells.dem.data.order.Ordering;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.environment.config.StaticFlags;
import net.splitcells.dem.utils.MathUtils;
import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.data.order.Comparator;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.rating.structure.Rating;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.OptionalAssert;
import org.w3c.dom.Node;

public class MinimalDistance<T> implements Rater {
    public static MinimalDistance<Integer> has_minimal_distance_of(Attribute<Integer> attribute, double minimumDistance) {
        return minimalDistance(attribute, minimumDistance, comparator_(Integer::compare), MathUtils::distance);
    }

    public static MinimalDistance<Double> minimalDistance(Attribute<Double> attribute, double minimumDistance) {
        return minimalDistance(attribute, minimumDistance, comparator_(Double::compare), MathUtils::distance);
    }

    public static <R> MinimalDistance<R> minimalDistance(Attribute<R> attribute
            , double minimumDistance
            , Comparator<R> comparator
            , BiFunction<R, R, Double> distanceMeasurer) {
        return new MinimalDistance<>(attribute, minimumDistance, comparator, distanceMeasurer);
    }

    private final double minimumDistance;
    private final Attribute<T> attribute;
    private final Comparator<T> comparator;
    private final BiFunction<T, T, Double> distanceMeassurer;
    private final List<Discoverable> contextes = list();

    protected MinimalDistance
            (Attribute<T> attribute, double minimumDistance
                    , Comparator<T> comparator
                    , BiFunction<T, T, Double> distanceMeassurer) {
        this.distanceMeassurer = distanceMeassurer;
        this.attribute = attribute;
        this.minimumDistance = minimumDistance;
        this.comparator = comparator;
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
                .filter(e -> e.value(LINE).equals(removal.value(LINE)))
                .collect(toList());
        return rateDistance(sortedLines, children, Optional.empty());
    }

    @Override
    public RatingEvent rating_after_addition(Table lines
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
            range(0, i).takeWhile(left -> {
                final var pairRating = pairRating(sortedLines.get(left), current_line);
                if (!pairRating.equalz(noCost())) {
                    ratingEvent.addRating_viaAddition
                            (current_line
                                    , pairRating
                                    , children
                                    , Optional.empty());
                    return true;
                }
                return false;
            });
            range(i + 1, sortedLines.size()).takeWhile(right -> {
                final var pairRating = pairRating(current_line, sortedLines.get(right));
                if (!pairRating.equalz(noCost())) {
                    ratingEvent.addRating_viaAddition
                            (current_line
                                    , pairRating
                                    , children
                                    , Optional.empty());
                    return true;
                }
                return false;
            });
        });
        return ratingEvent;
    }

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
                            assertThat(noCost().betterThan(sortedProcessingLines.get(i).value(RATING)))
                                    .isTrue();
                        } else {
                            assertThat(sortedProcessingLines.get(i).value(RATING).equalz(noCost())).isTrue();
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
                                            .get(sortedProcessingLines.size() - 2)
                                            .value(RATING)))
                            .isTrue();
                } else {
                    assertThat
                            (noCost().betterThan
                                    (sortedProcessingLines.get(sortedProcessingLines.size() - 1).value(RATING)))
                            .isTrue();
                }
            }
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    protected Rating pairRating(Line left, Line right) {
        final var actualDistance = distance(left, right);
        if (actualDistance >= minimumDistance) {
            return noCost();
        } else {
            return cost(actualDistance - minimumDistance);
        }
    }

    private double distance(Line left, Line right) {
        return abs(distanceMeassurer
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
    public Node argumentation(GroupId group, Table allocations) {
        final var reasoning = Xml.element("min-distance");
        reasoning.appendChild(
                Xml.element("minimum"
                        , Xml.textNode(minimumDistance + "")));
        reasoning.appendChild(
                Xml.element("order"
                        , Xml.textNode(comparator.toString())));
        defyingSorted(allocations).forEach(e -> reasoning.appendChild(e.toDom()));
        return reasoning;
    }

    @Override
    public List<Domable> arguments() {
        return Lists.list
                (() -> Xml.element("minimumDistance", Xml.textNode("" + minimumDistance))
                        , () -> Xml.element("attribute", attribute.toDom())
                        , () -> Xml.element("comparator", Xml.textNode("" + comparator))
                        , () -> Xml.element("distanceMeassurer", Xml.textNode("" + distanceMeassurer))
                );
    }

    @Override
    public boolean equals(Object arg) {
        if (arg != null && arg instanceof MinimalDistance) {
            return this.minimumDistance == ((MinimalDistance) arg).minimumDistance
                    && this.attribute.equals(((MinimalDistance) arg).attribute)
                    && this.comparator.equals(((MinimalDistance) arg).comparator)
                    && this.distanceMeassurer.equals(((MinimalDistance) arg).distanceMeassurer);
        }
        return false;
    }

    @Override
    public void addContext(Discoverable context) {
        contextes.add(context);
    }

    @Override
    public Collection<List<String>> paths() {
        return contextes.stream().map(Discoverable::path).collect(toList());
    }

    private Stream<Line> sortedStream(Table lines) {
        return lines.rawLinesView().stream()
                .filter(e -> e != null)
                .sorted((a, b) -> {
                            try {
                                return comparator.compare
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
                                return comparator.compare
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
                .sorted((a, b) -> comparator.compare(a.value(LINE).value(attribute), b.value(LINE).value(attribute)))
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
                .min(naturalOrder());
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
