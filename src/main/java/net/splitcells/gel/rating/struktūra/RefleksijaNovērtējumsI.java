package net.splitcells.gel.rating.struktūra;

import static java.util.Arrays.asList;
import static net.splitcells.dem.utils.Not_implemented_yet.not_implemented_yet;
import static net.splitcells.dem.lang.Xml.element;
import static net.splitcells.dem.data.order.Ordering.EQUAL;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.gel.rating.struktūra.RefleksijaRatingSavienotiesI.reflektētsNovērtejumsKombinetajs;
import static net.splitcells.gel.rating.struktūra.NovērtējumuTulksI.ratingTranslator;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

import net.splitcells.gel.rating.tips.Cena;
import net.splitcells.gel.rating.tips.Optimālums;
import net.splitcells.gel.rating.tips.Peļņa;
import org.w3c.dom.Node;
import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.data.order.Ordering;
import net.splitcells.dem.data.set.map.Map;

public class RefleksijaNovērtējumsI implements RefleksijaNovērtējums {
    protected final Map<Class<? extends Novērtējums>, Novērtējums> novērtējumi;
    protected final RatingTulks tulkotajs;
    protected final RefleksijaRatingSavienoties kombinētajs;

    public static RefleksijaNovērtējums metaRating(Map<Class<? extends Novērtējums>, Novērtējums> novērtējums) {
        return new RefleksijaNovērtējumsI(novērtējums);
    }

    public static RefleksijaNovērtējums rflektētsNovērtējums() {
        return new RefleksijaNovērtējumsI();
    }

    public static RefleksijaNovērtējums reflektētsNovērtējums(Novērtējums... argNovērtējumi) {
        final Map<Class<? extends Novērtējums>, Novērtējums> novērtējumuVārdnīca = map();
        asList(argNovērtējumi).forEach(novērtējums -> novērtējumuVārdnīca.put(novērtējums.getClass(), novērtējums));
        final RefleksijaNovērtējumsI reflektētsNovērtējums = new RefleksijaNovērtējumsI(novērtējumuVārdnīca);
        return reflektētsNovērtējums;
    }

    protected RefleksijaNovērtējumsI() {
        this(map());
    }

    @SuppressWarnings("unlikely-arg-type")
    protected RefleksijaNovērtējumsI(Map<Class<? extends Novērtējums>, Novērtējums> novērtējumi) {
        this.novērtējumi = novērtējumi;
        tulkotajs = ratingTranslator(novērtējumi);
        kombinētajs = reflektētsNovērtejumsKombinetajs(novērtējumi);
        /**
         * Apvienojiet vienkāršu vērtējumu ar {@link RefleksijaNovērtējums}.
         */
        reģistrētieKombinētajs(
                (baže, papildinājums) -> baže.isEmpty()
                        && papildinājums.size() == 1
                        && papildinājums.values().iterator().next() instanceof RefleksijaNovērtējums
                        && ((RefleksijaNovērtējums) papildinājums.values().iterator().next()).saturs().size() == 1
                , (baže, papildinājums) -> {
                    RefleksijaNovērtējums reflektētsPapildinājums
                            = (RefleksijaNovērtējums) papildinājums.values().iterator().next();
                    Map<Class<? extends Novērtējums>, Novērtējums> reflektētsNovērtējums = map();
                    reflektētsNovērtējums.put(reflektētsPapildinājums.saturs().keySet().iterator().next()
                            , reflektētsPapildinājums.saturs().values().iterator().next());
                    return reflektētsNovērtējums;
                }
        );
        /**
         * Apvieno 2 primitīvos {@link Novērtējums}.
         */
        reģistrētieKombinētajs(
                (baže, papildinājums) -> baže.size() == 1
                        && papildinājums.size() == 1
                        && !(papildinājums.values().iterator().next() instanceof RefleksijaNovērtējums)
                        && !(baže.values().iterator().next() instanceof RefleksijaNovērtējums)
                , (baže, papildinājums) -> {
                    final Map<Class<? extends Novērtējums>, Novērtējums> reflektētsNovērtējums = map();
                    Novērtējums primitiveAddition = baže.values().iterator().next()
                            .kombinē(papildinājums.values().iterator().next());
                    reflektētsNovērtējums.put(primitiveAddition.getClass()
                            , primitiveAddition);
                    return reflektētsNovērtējums;
                }
        );
        /**
         * Kombinē primitīvo vērtējumu ar {@link RefleksijaNovērtējums} ar vienu primitīvo {@link Novērtējums}.
         */
        reģistrētieKombinētajs(
                (baže, papildinājums) -> baže.size() == 1
                        && papildinājums.size() == 1
                        && !(baže.values().iterator().next() instanceof RefleksijaNovērtējums)
                        && papildinājums.values().iterator().next() instanceof RefleksijaNovērtējums
                , (baže, papildinājums) -> {
                    final Map<Class<? extends Novērtējums>, Novērtējums> reflektētsNovērtējums = map();
                    Novērtējums primitiveAddition = baže.values().iterator().next()
                            .kombinē(((RefleksijaNovērtējums)
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
                        && !(papildinājums.values().iterator().next() instanceof RefleksijaNovērtējums)
                , (baže, papildinājums) -> {
                    final Map<Class<? extends Novērtējums>, Novērtējums> reflektētsNovērtējums = map();
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
    public <R extends Novērtējums> R kombinē(Novērtējums... additionalNovērtējums) {
        return (R) kombinētajs.kombinē(additionalNovērtējums);
    }

    @Override
    public <R extends Novērtējums> R tulkošana(Class<R> tips) {
        if (novērtējumi.size() == 1) {
            if (novērtējumi.containsKey(Peļņa.class)) {
                return (R) Peļņa.peļņa(gūtSaturuDaļa(Peļņa.class).vertība());
            }
        }
        return (R) this;
    }

    @Override
    public <T extends Novērtējums> void reģistrētieKombinētajs
            (BiPredicate<Map<Class<? extends Novērtējums>, Novērtējums>, Map<Class<? extends Novērtējums>, Novērtējums>> nosacījums
                    , BiFunction
                     <Map<Class<? extends Novērtējums>, Novērtējums>
                             , Map<Class<? extends Novērtējums>, Novērtējums>
                             , Map<Class<? extends Novērtējums>, Novērtējums>> kombinētajs) {
        this.kombinētajs.reģistrētieKombinētajs(nosacījums, kombinētajs);
    }

    @Override
    public void reģistrēTulks
            (Class<? extends Novērtējums> mērķis
                    , Predicate<Map<Class<? extends Novērtējums>, Novērtējums>> nosacījums
                    , Function<Map<Class<? extends Novērtējums>, Novērtējums>, Novērtējums> tulks) {
        this.tulkotajs.reģistrēTulks(mērķis, nosacījums, tulks);
    }

    @Override
    public boolean equals(Object arg) {
        if (arg instanceof Novērtējums) {
            return compare_partially_to((Novērtējums) arg).get().equals(EQUAL);
        }
        throw not_implemented_yet();
    }

    @Override
    public Optional<Ordering> compare_partially_to(Novērtējums arg) {
        if (arg instanceof RefleksijaNovērtējums) {
            final RefleksijaNovērtējums other = (RefleksijaNovērtējums) arg;
            if (other.saturs().isEmpty() && novērtējumi.isEmpty()) {
                return Optional.of(EQUAL);
            }
            if (other.saturs().size() == 1 && novērtējumi.size() == 1) {
                return other.saturs().values().iterator().next()
                        .compare_partially_to(novērtējumi.values().iterator().next());
            }
        }
        if (arg instanceof Peļņa) {
            if (saturs().containsKey(Peļņa.class)) {
                return this.gūtSaturuDaļa(Peļņa.class).compare_partially_to(arg);
            }
            throw not_implemented_yet();
        }
        if (arg instanceof Cena) {
            if (saturs().containsKey(Cena.class)) {
                return this.gūtSaturuDaļa(Cena.class).compare_partially_to(arg);
            }
            if (saturs().isEmpty() && 0 == ((Cena) arg).vertība()) {
                return Optional.of(EQUAL);
            }
            if (saturs().containsKey(Optimālums.class)) {
                return this.gūtSaturuDaļa(Optimālums.class).compare_partially_to(arg);
            }
            throw not_implemented_yet();
        }
        if (arg instanceof Optimālums) {
            if (saturs().containsKey(Cena.class)) {
                return this.gūtSaturuDaļa(Cena.class).compare_partially_to(arg);
            }
        }
        throw not_implemented_yet();
    }

    @Override
    public Map<Class<? extends Novērtējums>, Novērtējums> saturs() {
        return novērtējumi;
    }

    @Override
    public <R extends Novērtējums> R _clone() {
        final RefleksijaNovērtējumsI clone = new RefleksijaNovērtējumsI();
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
        final var dom = element(RefleksijaNovērtējums.class.getSimpleName());
        return dom;
    }

    @Override
    public String toString() {
        return Xml.toPrettyString(toDom());
    }
}
