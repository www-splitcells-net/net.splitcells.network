package net.splitcells.gel.rating.struktūra;

import static net.splitcells.dem.data.set.map.Maps.map;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import net.splitcells.dem.data.set.map.Map;

public class NovērtējumuTulksI implements RatingTulks {
    private final Map<Class<? extends Novērtējums>, Novērtējums> novērtējumi;

    public static RatingTulks ratingTranslator(Map<Class<? extends Novērtējums>, Novērtējums> novērtējumi) {
        return new NovērtējumuTulksI(novērtējumi);
    }

    protected NovērtējumuTulksI(Map<Class<? extends Novērtējums>, Novērtējums> novērtējumi) {
        this.novērtējumi = novērtējumi;
    }

    protected final Map<Class<? extends Novērtējums>
            , Map<Predicate<Map<Class<? extends Novērtējums>, Novērtējums>>
            , Function<Map<Class<? extends Novērtējums>, Novērtējums>, Novērtējums>>> tulki = map();

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Novērtējums> T tulkošana(Class<T> target) {
        if (novērtējumi.containsKey(target)) {
            return (T) novērtējumi.get(target);
        }
        Function<Map<Class<? extends Novērtējums>, Novērtējums>, Novērtējums> derīgsTulks;
        final var tulkiKandidāts
                = classesOf(target).stream()
                .filter(i -> tulki.containsKey(i))
                .map(i -> tulki.get(i).entrySet()).flatMap(i -> i.stream())
                .filter(i -> i.getKey().test(novērtējumi))
                .map(i -> i.getValue())
                .findFirst();
        assertTrue(tulkiKandidāts.isPresent());
        derīgsTulks = tulkiKandidāts.get();
        return (T) derīgsTulks.apply(novērtējumi);
    }

    @Override
    public void reģistrēTulks(Class<? extends Novērtējums> mērķis
            , Predicate<Map<Class<? extends Novērtējums>, Novērtējums>> nosacījums
            , Function<Map<Class<? extends Novērtējums>, Novērtējums>, Novērtējums> tulks) {
        if (!tulki.containsKey(mērķis)) {
            tulki.put(mērķis, map());
        }
        tulki.get(mērķis).put(nosacījums, tulks);
    }

    public static List<Class<?>> classesOf(Class<?> clazz) {
        final List<Class<?>> rVal = new ArrayList<>();
        rVal.add(clazz);
        Class<?> superclass = clazz.getSuperclass();
        while (superclass != null) {
            rVal.add(superclass);
            clazz = superclass;
            superclass = clazz.getSuperclass();
        }
        return rVal;
    }
}
