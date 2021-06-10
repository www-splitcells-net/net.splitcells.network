package net.splitcells.gel.rating.rater;

import static java.util.stream.Collectors.toList;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.environment.config.StaticFlags.ENFORCING_UNIT_CONSISTENCY;
import static net.splitcells.dem.utils.IncorrectImplementation.incorrectImplementation;
import static net.splitcells.gel.rating.rater.RatingEventI.ratingEvent;
import static net.splitcells.gel.rating.type.Cost.cost;
import static net.splitcells.gel.rating.type.Cost.noCost;
import static net.splitcells.gel.rating.framework.LocalRatingI.localRating;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.Predicate;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.dem.environment.config.StaticFlags;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.constraint.Constraint;

public class AllDifferent<T> implements Rater {
    private final Attribute<T> attribute;
    private final Map<T, Predicate<Line>> predicate = map();
    private final Function<T, Predicate<Line>> predicateFactory;
    private final List<Discoverable> contexts = list();

    public static <R> AllDifferent<R> allDifferent(Attribute<R> attribute) {
        return new AllDifferent<>(attribute);
    }

    private AllDifferent(Attribute<T> attribute) {
        this.attribute = attribute;
        predicateFactory = value -> line -> line.value(this.attribute).equals(value);
        ;
    }

    private Predicate<Line> predicate(T value) {
        if (!predicate.containsKey(value)) {
            predicate.put(value, predicateFactory.apply(value));
        }
        return predicate.get(value);
    }

    @Override
    public RatingEvent ratingAfterAddition(Table lines, Line addition, List<Constraint> children
            , Table ratingsBeforeAddition) {
        final T value = addition.value(Constraint.LINE).value(attribute);
        final var group = lines.columnView(Constraint.LINE).lookup(predicate(value));
        final var ratingEvent = ratingEvent();
        if (1 == group.size()) {
            ratingEvent.additions().put(
                    addition
                    , localRating()
                            .withPropagationTo(children)
                            .withRating(noCost())
                            .withResultingGroupId(addition.value(Constraint.INCOMING_CONSTRAINT_GROUP)));
        } else if (2 == group.size()) {
            if (ENFORCING_UNIT_CONSISTENCY) {
                assertThat(group.rawLinesView().stream().filter(e -> e != null)).hasSize(2);
            }
            // TODO Parameterize test.
            group.rawLinesView().stream()
                    .filter(e -> e != null)
                    .forEach(e -> ratingEvent.additions()
                            .put(e//
                                    , localRating()
                                            .withPropagationTo(children)
                                            .withRating(cost(1.0))
                                            .withResultingGroupId(addition.value(Constraint.INCOMING_CONSTRAINT_GROUP))));
            group.rawLinesView().stream()
                    .filter(e -> e != null)
                    .filter(e -> e.index() != addition.index())
                    .forEach(e -> ratingEvent.removal().add(e));
        } else if (2 < group.size()) {
            // TODO Parameterize test.
            ratingEvent.additions().put
                    (addition
                            , localRating()
                                    .withPropagationTo(children)
                                    .withRating(cost(1.0))
                                    .withResultingGroupId(addition.value(Constraint.INCOMING_CONSTRAINT_GROUP)));
        } else {
            throw incorrectImplementation("" + group.size());
        }
        return ratingEvent;
    }

    @Override
    public RatingEvent rating_before_removal(Table lines, Line removal, List<Constraint> children
            , Table ratingsBeforeRemoval) {
        final T value = removal.value(Constraint.LINE).value(attribute);
        final var group = lines.columnView(Constraint.LINE).lookup(predicate(value));
        final var ratingEvent = ratingEvent();
        if (1 == group.size()) {
            // Before removal there was 1 duplication and now there is now duplicate lines
            // for this value present anymore.
            if (StaticFlags.ENFORCING_UNIT_CONSISTENCY) {
                assertThat(group.rawLinesView().stream().filter(e -> e != null)).hasSize(1);
            }
            group.rawLinesView().stream()
                    .filter(e -> e != null)
                    .forEach(e -> {
                        ratingEvent.removal().add(e);
                        ratingEvent.additions()
                                .put(e
                                        , localRating()
                                                .withPropagationTo(children)
                                                .withRating(noCost())
                                                .withResultingGroupId(removal.value(Constraint.INCOMING_CONSTRAINT_GROUP)));
                    });
        }
        return ratingEvent;
    }

    @Override
    public Class<? extends Rater> type() {
        return AllDifferent.class;
    }

    @Override
    public List<Domable> arguments() {
        return list(attribute);
    }

    @Override
    public void addContext(Discoverable context) {
        contexts.add(context);
    }

    @Override
    public Collection<List<String>> paths() {
        return contexts.stream().map(Discoverable::path).collect(toList());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ", " + attribute.name();
    }
}
