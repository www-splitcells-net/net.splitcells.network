package net.splitcells.gel.rating.structure;

import static java.util.Arrays.asList;
import static net.splitcells.dem.utils.Not_implemented_yet.not_implemented_yet;
import static net.splitcells.dem.lang.Xml.element;
import static net.splitcells.dem.data.order.Ordering.EQUAL;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.gel.rating.structure.MetaRatingMergerI.metaRatingMerger;
import static net.splitcells.gel.rating.structure.RatingTranslatorI.ratingTranslator;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

import net.splitcells.gel.rating.type.Cost;
import net.splitcells.gel.rating.type.Optimality;
import net.splitcells.gel.rating.type.Profit;
import org.w3c.dom.Node;
import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.data.order.Ordering;
import net.splitcells.dem.data.set.map.Map;

public class MetaRatingI implements MetaRating {
    protected final Map<Class<? extends Rating>, Rating> ratingMap;
    protected final RatingTranslator translator;
    protected final MetaRatingMerger merger;

    public static MetaRating metaRating(Map<Class<? extends Rating>, Rating> rating) {
        return new MetaRatingI(rating);
    }

    public static MetaRating metaRating() {
        return new MetaRatingI();
    }

    public static MetaRating reflektētsNovērtējums(Rating... ratings) {
        final Map<Class<? extends Rating>, Rating> ratingMap = map();
        asList(ratings).forEach(rating -> ratingMap.put(rating.getClass(), rating));
        final MetaRatingI metaRating = new MetaRatingI(ratingMap);
        return metaRating;
    }

    protected MetaRatingI() {
        this(map());
    }

    @SuppressWarnings("unlikely-arg-type")
    protected MetaRatingI(Map<Class<? extends Rating>, Rating> ratingMap) {
        this.ratingMap = ratingMap;
        translator = ratingTranslator(ratingMap);
        merger = metaRatingMerger(ratingMap);
        /**
         * Combine multiple simple {@link Rating} with ar {@link MetaRating}.
         */
        registerMerger(
                (base, addition) -> base.isEmpty()
                        && addition.size() == 1
                        && addition.values().iterator().next() instanceof MetaRating
                        && ((MetaRating) addition.values().iterator().next()).content().size() == 1
                , (base, addition) -> {
                    MetaRating metaAddition
                            = (MetaRating) addition.values().iterator().next();
                    final Map<Class<? extends Rating>, Rating> metaRating = map();
                    metaRating.put(metaAddition.content().keySet().iterator().next()
                            , metaAddition.content().values().iterator().next());
                    return metaRating;
                }
        );
        /**
         * Combine 2 primitive {@link Rating}.
         */
        registerMerger(
                (base, addition) -> base.size() == 1
                        && addition.size() == 1
                        && !(addition.values().iterator().next() instanceof MetaRating)
                        && !(base.values().iterator().next() instanceof MetaRating)
                , (base, addition) -> {
                    final Map<Class<? extends Rating>, Rating> metaRating = map();
                    final Rating primitiveAddition = base.values().iterator().next()
                            .combine(addition.values().iterator().next());
                    metaRating.put(primitiveAddition.getClass()
                            , primitiveAddition);
                    return metaRating;
                }
        );
        /**
         * Kombinē primitīvo vērtējumu ar {@link MetaRating} ar vienu primitīvo {@link Rating}.
         */
        registerMerger(
                (baže, papildinājums) -> baže.size() == 1
                        && papildinājums.size() == 1
                        && !(baže.values().iterator().next() instanceof MetaRating)
                        && papildinājums.values().iterator().next() instanceof MetaRating
                , (baže, papildinājums) -> {
                    final Map<Class<? extends Rating>, Rating> reflektētsNovērtējums = map();
                    Rating primitiveAddition = baže.values().iterator().next()
                            .combine(((MetaRating)
                                    papildinājums.values().iterator().next()).content().values().iterator().next());
                    reflektētsNovērtējums.put(primitiveAddition.getClass()
                            , primitiveAddition);
                    return reflektētsNovērtējums;
                }
        );
        /**
         * Primitīvu vērtējumu pielikt meitrālajam elementam.
         */
        registerMerger(
                (baže, papildinājums) -> baže.isEmpty()
                        && papildinājums.size() == 1
                        && !(papildinājums.values().iterator().next() instanceof MetaRating)
                , (baže, papildinājums) -> {
                    final Map<Class<? extends Rating>, Rating> reflektētsNovērtējums = map();
                    final var ratingClass = papildinājums.keySet().iterator().next();
                    /**
                     * Bez {@link net.splitcells.dem.object.DeepCloneable#deepClone}
                     * tiek radītas nevēlamas blakusparādības.
                     */
                    reflektētsNovērtējums.put(ratingClass, papildinājums.get(ratingClass));
                    return reflektētsNovērtējums;
                }
        );
    }

    @Override
    public <R extends Rating> R combine(Rating... additionalNovērtējums) {
        return (R) merger.combine(additionalNovērtējums);
    }

    @Override
    public <R extends Rating> R tulkošana(Class<R> tips) {
        if (ratingMap.size() == 1) {
            if (ratingMap.containsKey(Profit.class)) {
                return (R) Profit.profit(getContentValue(Profit.class).value());
            }
        }
        return (R) this;
    }

    @Override
    public <T extends Rating> void registerMerger
            (BiPredicate<Map<Class<? extends Rating>, Rating>, Map<Class<? extends Rating>, Rating>> nosacījums
                    , BiFunction
                     <Map<Class<? extends Rating>, Rating>
                             , Map<Class<? extends Rating>, Rating>
                             , Map<Class<? extends Rating>, Rating>> kombinētajs) {
        this.merger.registerMerger(nosacījums, kombinētajs);
    }

    @Override
    public void reģistrēTulks
            (Class<? extends Rating> mērķis
                    , Predicate<Map<Class<? extends Rating>, Rating>> nosacījums
                    , Function<Map<Class<? extends Rating>, Rating>, Rating> tulks) {
        this.translator.reģistrēTulks(mērķis, nosacījums, tulks);
    }

    @Override
    public boolean equals(Object arg) {
        if (arg instanceof Rating) {
            return compare_partially_to((Rating) arg).get().equals(EQUAL);
        }
        throw not_implemented_yet();
    }

    @Override
    public Optional<Ordering> compare_partially_to(Rating arg) {
        if (arg instanceof MetaRating) {
            final MetaRating other = (MetaRating) arg;
            if (other.content().isEmpty() && ratingMap.isEmpty()) {
                return Optional.of(EQUAL);
            }
            if (other.content().size() == 1 && ratingMap.size() == 1) {
                return other.content().values().iterator().next()
                        .compare_partially_to(ratingMap.values().iterator().next());
            }
        }
        if (arg instanceof Profit) {
            if (content().containsKey(Profit.class)) {
                return this.getContentValue(Profit.class).compare_partially_to(arg);
            }
            throw not_implemented_yet();
        }
        if (arg instanceof Cost) {
            if (content().containsKey(Cost.class)) {
                return this.getContentValue(Cost.class).compare_partially_to(arg);
            }
            if (content().isEmpty() && 0 == ((Cost) arg).value()) {
                return Optional.of(EQUAL);
            }
            if (content().containsKey(Optimality.class)) {
                return this.getContentValue(Optimality.class).compare_partially_to(arg);
            }
            throw not_implemented_yet();
        }
        if (arg instanceof Optimality) {
            if (content().containsKey(Cost.class)) {
                return this.getContentValue(Cost.class).compare_partially_to(arg);
            }
        }
        throw not_implemented_yet();
    }

    @Override
    public Map<Class<? extends Rating>, Rating> content() {
        return ratingMap;
    }

    @Override
    public <R extends Rating> R _clone() {
        final MetaRatingI clone = new MetaRatingI();
        clone.content().forEach((key, value) -> {
            clone.content().put(key, value._clone());
        });
        return (R) clone;
    }

    @Override
    public Node toDom() {
        if (1 == ratingMap.size()) {
            return ratingMap.values().iterator().next().toDom();
        }
        final var dom = element(MetaRating.class.getSimpleName());
        return dom;
    }

    @Override
    public String toString() {
        return Xml.toPrettyString(toDom());
    }
}
