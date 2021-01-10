package net.splitcells.gel.rating.structure;

import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static net.splitcells.dem.lang.Xml.element;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.data.set.map.Maps.typeMapping;
import static net.splitcells.dem.utils.Not_implemented_yet.not_implemented_yet;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

import org.w3c.dom.Element;
import net.splitcells.dem.data.order.Ordering;
import net.splitcells.dem.data.set.map.Map;

public class MetaRatingMergerI implements MetaRatingMerger {
    protected final Map<Class<? extends Rating>, Rating> ratings;
    protected final Map<BiPredicate
            <Map
                    <Class<? extends Rating>, Rating>
                    , Map<Class<? extends Rating>, Rating>>
            , BiFunction
            <Map<Class<? extends Rating>, Rating>
                    , Map<Class<? extends Rating>, Rating>
                    , Map<Class<? extends Rating>, Rating>>> combiners = map();

    public static MetaRatingMerger metaRatingMerger
            (Map<Class<? extends Rating>, Rating> ratings) {
        return new MetaRatingMergerI(ratings);
    }

    protected MetaRatingMergerI(Map<Class<? extends Rating>, Rating> ratings) {
        this.ratings = requireNonNull(ratings);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <R extends Rating> R combine(Rating... additionalRatings) {
        // TODO Make it immutble.
        final var additionalRatingMap = typeMapping(simplify(additionalRatings));
        return (R) MetaRatingI.metaRating(combiners.entrySet().stream()
                .filter(combiner -> combiner.getKey().test(ratings, additionalRatingMap))
                .findFirst()
                .get()
                .getValue()
                .apply(ratings, additionalRatingMap));
    }

    @Override
    public <T extends Rating> void registerMerger
            (BiPredicate<Map<Class<? extends Rating>, Rating>, Map<Class<? extends Rating>, Rating>> condition
                    , BiFunction<Map<Class<? extends Rating>, Rating>, Map<Class<? extends Rating>, Rating>, Map<Class<? extends Rating>, Rating>> combiner) {
        if (combiners.containsKey(condition)) {
            throw new IllegalArgumentException();
        }
        combiners.put(condition, combiner);
    }

    private java.util.List<Rating> simplify(Rating... ratings) {
        return asList(ratings).stream().flatMap(rating -> {
            if (rating instanceof MetaRating) {
                return ((MetaRating) rating).content().values().stream();
            }
            return Stream.of(rating);
        }).collect(toList());
    }

    @Override
    public <R extends Rating> R _clone() {
        throw not_implemented_yet();
    }

    @Override
    public Optional<Ordering> compare_partially_to(Rating arg) {
        throw not_implemented_yet();
    }

    @Override
    public Element toDom() {
        return element(MetaRatingMerger.class.getSimpleName());
    }
}
