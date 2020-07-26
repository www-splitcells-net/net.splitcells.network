package net.splitcells.gel.kodols.novērtējums.struktūra;

import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static net.splitcells.dem.lang.Xml.element;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.data.set.map.Maps.typeMapping;
import static net.splitcells.dem.utils.Not_implemented_yet.not_implemented_yet;
import static net.splitcells.gel.kodols.novērtējums.struktūra.RefleksijaNovērtējumsI.metaRating;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

import org.w3c.dom.Element;
import net.splitcells.dem.data.order.Ordering;
import net.splitcells.dem.data.set.map.Map;

public class RefleksijaRatingSavienotiesI implements RefleksijaRatingSavienoties {
    protected final Map<Class<? extends Novērtējums>, Novērtējums> novērtējumi;
    protected final Map<BiPredicate
            <Map
                    <Class<? extends Novērtējums>, Novērtējums>
                    , Map<Class<? extends Novērtējums>, Novērtējums>>
            , BiFunction
            <Map<Class<? extends Novērtējums>, Novērtējums>
                    , Map<Class<? extends Novērtējums>, Novērtējums>
                    , Map<Class<? extends Novērtējums>, Novērtējums>>> kombinētaji = map();

    public static RefleksijaRatingSavienoties reflektētsNovērtejumsKombinetajs
            (Map<Class<? extends Novērtējums>, Novērtējums> novērtējumi) {
        return new RefleksijaRatingSavienotiesI(novērtējumi);
    }

    protected RefleksijaRatingSavienotiesI(Map<Class<? extends Novērtējums>, Novērtējums> novērtējumi) {
        this.novērtējumi = requireNonNull(novērtējumi);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <R extends Novērtējums> R kombinē(Novērtējums... papilduNovērtējums) {
        // DARĪT Darī nemainigu.
        final var papildiNovērtējumuVārdnīca = typeMapping(simplify(papilduNovērtējums));
        return (R) metaRating(kombinētaji.entrySet().stream()
                .filter(kombinētajs -> kombinētajs.getKey().test(novērtējumi, papildiNovērtējumuVārdnīca))
                .findFirst()
                .get()
                .getValue()
                .apply(novērtējumi, papildiNovērtējumuVārdnīca));
    }

    @Override
    public <T extends Novērtējums> void reģistrētieKombinētajs
            (BiPredicate<Map<Class<? extends Novērtējums>, Novērtējums>, Map<Class<? extends Novērtējums>, Novērtējums>> nosacījums, BiFunction<Map<Class<? extends Novērtējums>, Novērtējums>, Map<Class<? extends Novērtējums>, Novērtējums>, Map<Class<? extends Novērtējums>, Novērtējums>> kombinētajs) {
        if (kombinētaji.containsKey(nosacījums)) {
            throw new IllegalArgumentException();
        }
        kombinētaji.put(nosacījums, kombinētajs);
    }

    private java.util.List<Novērtējums> simplify(Novērtējums... novērtējumi) {
        return asList(novērtējumi).stream().flatMap(novērtējums -> {
            if (novērtējums instanceof RefleksijaNovērtējums) {
                return ((RefleksijaNovērtējums) novērtējums).saturs().values().stream();
            }
            return Stream.of(novērtējums);
        }).collect(toList());
    }

    @Override
    public <R extends Novērtējums> R _clone() {
        throw not_implemented_yet();
    }

    @Override
    public Optional<Ordering> compare_partially_to(Novērtējums arg) {
        throw not_implemented_yet();
    }

    @Override
    public Element toDom() {
        return element(RefleksijaRatingSavienoties.class.getSimpleName());
    }
}
