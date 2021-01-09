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
    protected final Map<Class<? extends Rating>, Rating> novērtējumi;
    protected final Map<BiPredicate
            <Map
                    <Class<? extends Rating>, Rating>
                    , Map<Class<? extends Rating>, Rating>>
            , BiFunction
            <Map<Class<? extends Rating>, Rating>
                    , Map<Class<? extends Rating>, Rating>
                    , Map<Class<? extends Rating>, Rating>>> kombinētaji = map();

    public static MetaRatingMerger reflektētsNovērtejumsKombinetajs
            (Map<Class<? extends Rating>, Rating> novērtējumi) {
        return new MetaRatingMergerI(novērtējumi);
    }

    protected MetaRatingMergerI(Map<Class<? extends Rating>, Rating> novērtējumi) {
        this.novērtējumi = requireNonNull(novērtējumi);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <R extends Rating> R kombinē(Rating... papilduNovērtējums) {
        // DARĪT Darī nemainigu.
        final var papildiNovērtējumuVārdnīca = typeMapping(simplify(papilduNovērtējums));
        return (R) MetaRatingI.metaRating(kombinētaji.entrySet().stream()
                .filter(kombinētajs -> kombinētajs.getKey().test(novērtējumi, papildiNovērtējumuVārdnīca))
                .findFirst()
                .get()
                .getValue()
                .apply(novērtējumi, papildiNovērtējumuVārdnīca));
    }

    @Override
    public <T extends Rating> void reģistrētieKombinētajs
            (BiPredicate<Map<Class<? extends Rating>, Rating>, Map<Class<? extends Rating>, Rating>> nosacījums, BiFunction<Map<Class<? extends Rating>, Rating>, Map<Class<? extends Rating>, Rating>, Map<Class<? extends Rating>, Rating>> kombinētajs) {
        if (kombinētaji.containsKey(nosacījums)) {
            throw new IllegalArgumentException();
        }
        kombinētaji.put(nosacījums, kombinētajs);
    }

    private java.util.List<Rating> simplify(Rating... novērtējumi) {
        return asList(novērtējumi).stream().flatMap(novērtējums -> {
            if (novērtējums instanceof MetaRating) {
                return ((MetaRating) novērtējums).saturs().values().stream();
            }
            return Stream.of(novērtējums);
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
