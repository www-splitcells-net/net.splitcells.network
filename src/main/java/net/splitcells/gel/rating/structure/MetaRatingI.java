package net.splitcells.gel.rating.structure;

import static java.util.Arrays.asList;
import static net.splitcells.dem.utils.Not_implemented_yet.not_implemented_yet;
import static net.splitcells.dem.lang.Xml.element;
import static net.splitcells.dem.data.order.Ordering.EQUAL;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.gel.rating.structure.MetaRatingMergerI.reflektētsNovērtejumsKombinetajs;
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
    protected final Map<Class<? extends Rating>, Rating> novērtējumi;
    protected final RatingTranslator tulkotajs;
    protected final MetaRatingMerger kombinētajs;

    public static MetaRating metaRating(Map<Class<? extends Rating>, Rating> novērtējums) {
        return new MetaRatingI(novērtējums);
    }

    public static MetaRating rflektētsNovērtējums() {
        return new MetaRatingI();
    }

    public static MetaRating reflektētsNovērtējums(Rating... argNovērtējumi) {
        final Map<Class<? extends Rating>, Rating> novērtējumuVārdnīca = map();
        asList(argNovērtējumi).forEach(novērtējums -> novērtējumuVārdnīca.put(novērtējums.getClass(), novērtējums));
        final MetaRatingI reflektētsNovērtējums = new MetaRatingI(novērtējumuVārdnīca);
        return reflektētsNovērtējums;
    }

    protected MetaRatingI() {
        this(map());
    }

    @SuppressWarnings("unlikely-arg-type")
    protected MetaRatingI(Map<Class<? extends Rating>, Rating> novērtējumi) {
        this.novērtējumi = novērtējumi;
        tulkotajs = ratingTranslator(novērtējumi);
        kombinētajs = reflektētsNovērtejumsKombinetajs(novērtējumi);
        /**
         * Apvienojiet vienkāršu vērtējumu ar {@link MetaRating}.
         */
        reģistrētieKombinētajs(
                (baže, papildinājums) -> baže.isEmpty()
                        && papildinājums.size() == 1
                        && papildinājums.values().iterator().next() instanceof MetaRating
                        && ((MetaRating) papildinājums.values().iterator().next()).saturs().size() == 1
                , (baže, papildinājums) -> {
                    MetaRating reflektētsPapildinājums
                            = (MetaRating) papildinājums.values().iterator().next();
                    Map<Class<? extends Rating>, Rating> reflektētsNovērtējums = map();
                    reflektētsNovērtējums.put(reflektētsPapildinājums.saturs().keySet().iterator().next()
                            , reflektētsPapildinājums.saturs().values().iterator().next());
                    return reflektētsNovērtējums;
                }
        );
        /**
         * Apvieno 2 primitīvos {@link Rating}.
         */
        reģistrētieKombinētajs(
                (baže, papildinājums) -> baže.size() == 1
                        && papildinājums.size() == 1
                        && !(papildinājums.values().iterator().next() instanceof MetaRating)
                        && !(baže.values().iterator().next() instanceof MetaRating)
                , (baže, papildinājums) -> {
                    final Map<Class<? extends Rating>, Rating> reflektētsNovērtējums = map();
                    Rating primitiveAddition = baže.values().iterator().next()
                            .kombinē(papildinājums.values().iterator().next());
                    reflektētsNovērtējums.put(primitiveAddition.getClass()
                            , primitiveAddition);
                    return reflektētsNovērtējums;
                }
        );
        /**
         * Kombinē primitīvo vērtējumu ar {@link MetaRating} ar vienu primitīvo {@link Rating}.
         */
        reģistrētieKombinētajs(
                (baže, papildinājums) -> baže.size() == 1
                        && papildinājums.size() == 1
                        && !(baže.values().iterator().next() instanceof MetaRating)
                        && papildinājums.values().iterator().next() instanceof MetaRating
                , (baže, papildinājums) -> {
                    final Map<Class<? extends Rating>, Rating> reflektētsNovērtējums = map();
                    Rating primitiveAddition = baže.values().iterator().next()
                            .kombinē(((MetaRating)
                                    papildinājums.values().iterator().next()).saturs().values().iterator().next());
                    reflektētsNovērtējums.put(primitiveAddition.getClass()
                            , primitiveAddition);
                    return reflektētsNovērtējums;
                }
        );
        /**
         * Primitīvu vērtējumu pielikt meitrālajam elementam.
         */
        reģistrētieKombinētajs(
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
    public <R extends Rating> R kombinē(Rating... additionalNovērtējums) {
        return (R) kombinētajs.kombinē(additionalNovērtējums);
    }

    @Override
    public <R extends Rating> R tulkošana(Class<R> tips) {
        if (novērtējumi.size() == 1) {
            if (novērtējumi.containsKey(Profit.class)) {
                return (R) Profit.profit(gūtSaturuDaļa(Profit.class).value());
            }
        }
        return (R) this;
    }

    @Override
    public <T extends Rating> void reģistrētieKombinētajs
            (BiPredicate<Map<Class<? extends Rating>, Rating>, Map<Class<? extends Rating>, Rating>> nosacījums
                    , BiFunction
                     <Map<Class<? extends Rating>, Rating>
                             , Map<Class<? extends Rating>, Rating>
                             , Map<Class<? extends Rating>, Rating>> kombinētajs) {
        this.kombinētajs.reģistrētieKombinētajs(nosacījums, kombinētajs);
    }

    @Override
    public void reģistrēTulks
            (Class<? extends Rating> mērķis
                    , Predicate<Map<Class<? extends Rating>, Rating>> nosacījums
                    , Function<Map<Class<? extends Rating>, Rating>, Rating> tulks) {
        this.tulkotajs.reģistrēTulks(mērķis, nosacījums, tulks);
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
            if (other.saturs().isEmpty() && novērtējumi.isEmpty()) {
                return Optional.of(EQUAL);
            }
            if (other.saturs().size() == 1 && novērtējumi.size() == 1) {
                return other.saturs().values().iterator().next()
                        .compare_partially_to(novērtējumi.values().iterator().next());
            }
        }
        if (arg instanceof Profit) {
            if (saturs().containsKey(Profit.class)) {
                return this.gūtSaturuDaļa(Profit.class).compare_partially_to(arg);
            }
            throw not_implemented_yet();
        }
        if (arg instanceof Cost) {
            if (saturs().containsKey(Cost.class)) {
                return this.gūtSaturuDaļa(Cost.class).compare_partially_to(arg);
            }
            if (saturs().isEmpty() && 0 == ((Cost) arg).value()) {
                return Optional.of(EQUAL);
            }
            if (saturs().containsKey(Optimality.class)) {
                return this.gūtSaturuDaļa(Optimality.class).compare_partially_to(arg);
            }
            throw not_implemented_yet();
        }
        if (arg instanceof Optimality) {
            if (saturs().containsKey(Cost.class)) {
                return this.gūtSaturuDaļa(Cost.class).compare_partially_to(arg);
            }
        }
        throw not_implemented_yet();
    }

    @Override
    public Map<Class<? extends Rating>, Rating> saturs() {
        return novērtējumi;
    }

    @Override
    public <R extends Rating> R _clone() {
        final MetaRatingI clone = new MetaRatingI();
        clone.saturs().forEach((key, value) -> {
            clone.saturs().put(key, value._clone());
        });
        return (R) clone;
    }

    @Override
    public Node toDom() {
        if (1 == novērtējumi.size()) {
            return novērtējumi.values().iterator().next().toDom();
        }
        final var dom = element(MetaRating.class.getSimpleName());
        return dom;
    }

    @Override
    public String toString() {
        return Xml.toPrettyString(toDom());
    }
}
