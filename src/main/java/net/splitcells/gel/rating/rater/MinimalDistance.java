package net.splitcells.gel.rating.rater;

import static java.lang.Math.abs;
import static java.util.Comparator.naturalOrder;
import static java.util.stream.IntStream.rangeClosed;
import static net.splitcells.dem.data.order.Comparator.comparator_;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.gel.constraint.Constraint.LINE;
import static net.splitcells.gel.rating.type.Cost.cost;
import static net.splitcells.gel.rating.type.Cost.noCost;
import static net.splitcells.gel.rating.structure.LocalRatingI.localRating;

import java.util.Collection;
import java.util.Optional;
import java.util.function.BiFunction;

import net.splitcells.dem.data.set.list.List;
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
import org.w3c.dom.Node;

public class MinimalDistance<T> implements Rater {
    public static MinimalDistance<Integer> minimalDistance2(Attribute<Integer> attribute, double minimumDistance) {
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
        final var ratingEvent = RatingEventI.ratingEvent();
        final var sortedLines = sorted(lines);
        final int sortedIndexes = sortedLines.indexOf(
                sortedLines.stream()
                        .filter(e -> e.value(LINE).equals(removal.value(LINE)))
                        .findFirst().get());
        if (sortedIndexes == 0) {
            // KOMPORMISS
            int i = 1;
            while (i < sortedLines.size()) {
                final var remainingRightLine = sortedLines.get(i);
                if (!isValid(removal, remainingRightLine)) {
                    rate_addition_ofRemovalPair(ratingEvent, removal, remainingRightLine, children//
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
                    rate_addition_ofRemovalPair(ratingEvent, removal, sortedLines.get(i), children//
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
                    rate_addition_ofRemovalPair(ratingEvent, removal, sortedLines.get(i), children//
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
                    rate_addition_ofRemovalPair(ratingEvent, removal, sortedLines.get(i), children//
                            , ratingsBeforeRemoval.lookupEquals(LINE, remainingRightLine).value(Constraint.RATING));
                    ++i;
                } else {
                    break;
                }
            }
        } else {
            throw new AssertionError("" + sortedIndexes);
        }
        return ratingEvent;
    }

    @Override
    public RatingEvent rating_after_addition(Table lines, Line addition, net.splitcells.dem.data.set.list.List<Constraint> children, Table ratingsBeforeAddition) {
        final var ratingEvent = RatingEventI.ratingEvent();
        final var sortedLines = sorted(lines);
        // TODO PERFORMANCE
        final int sortedIndexes = sortedLines.indexOf(
                sortedLines.stream()
                        .filter(e -> e.value(LINE).equals(addition.value(LINE)))
                        .findFirst()
                        .get());
        if (sortedIndexes == 0) {
            if (sortedLines.size() == 1) {
                ratingEvent.additions().put(addition//
                        , localRating().
                                withPropagationTo(children).
                                withRating(noCost()).
                                withResultingGroupId(addition.value(Constraint.INCOMING_CONSTRAINT_GROUP)));
            } else {
                rate_addition_ofAdditionPair(ratingEvent, addition, sortedLines.get(1), children//
                        , Optional.of(ratingsBeforeAddition.lookupEquals(LINE, sortedLines.get(1)).value(Constraint.RATING)));
            }
        } else if (sortedIndexes == sortedLines.size() - 1) {
            // TODO HACK
            int i = 0;
            while (-1 < sortedLines.size() - 2 - i) {
                final var originalLeftLine = sortedLines.get(sortedLines.size() - 2 - i);
                if (!isValid(originalLeftLine, addition) || i == 0) {
                    rate_addition_ofAdditionPair(ratingEvent, addition, originalLeftLine, children//
                            , Optional.of(ratingsBeforeAddition.lookupEquals(LINE, originalLeftLine).value(Constraint.RATING)));
                    ++i;
                } else {
                    break;
                }
            }
        } else if (sortedIndexes > 0 && sortedIndexes < sortedLines.size() - 1) {
            // TODO HACK
            int i = 0;
            while (sortedLines.size() > sortedIndexes + 1 + i) {
                final var originalRightLine = sortedLines.get(sortedIndexes + 1);
                if (!isValid(addition, originalRightLine)) {
                    rate_addition_ofAdditionPair(ratingEvent, addition, originalRightLine, children
                            , Optional.of(ratingsBeforeAddition.lookupEquals(LINE, originalRightLine).value(Constraint.RATING)));
                    ++i;
                } else {
                    break;
                }
            }
            i = 0;
            while (-1 < sortedIndexes - 1 - i) {
                final var originalLeftLine = sortedLines.get(sortedIndexes - 1 - i);
                if (!isValid(originalLeftLine, addition)) {
                    rate_addition_ofAdditionPair(ratingEvent, addition, originalLeftLine, children, Optional.of(ratingsBeforeAddition.lookupEquals(LINE, originalLeftLine).value(Constraint.RATING)));
                    ++i;
                } else {
                    break;
                }
            }
        } else {
            throw new AssertionError("" + sortedIndexes);
        }
        return ratingEvent;
    }

    protected void rate_addition_ofAdditionPair
            (RatingEvent rVal
                    , Line addition
                    , Line originalLine
                    , List<Constraint> children
                    , Optional<Rating> ratingBeforeAddition) {
        final Rating additionalCost;
        if (abs(distanceMeassurer.apply(
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
        return abs(distanceMeassurer
                .apply(a.value(LINE).value(attribute)
                        , b.value(LINE).value(attribute)));
    }

    private boolean isValid(Line a, Line b) {
        return distance(a, b) >= minimumDistance;
    }

    protected void rate_addition_ofRemovalPair
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
    public net.splitcells.dem.data.set.list.List<Domable> arguments() {
        return Lists.list(//
                () -> Xml.element("minimumDistance", Xml.textNode("" + minimumDistance))//
                , () -> Xml.element("attribute", attribute.toDom())//
                , () -> Xml.element("comparator", Xml.textNode("" + comparator))//
                , () -> Xml.element("distanceMeassurer", Xml.textNode("" + distanceMeassurer))//
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
    public Collection<net.splitcells.dem.data.set.list.List<String>> paths() {
        return contextes.stream().map(Discoverable::path).collect(toList());
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
