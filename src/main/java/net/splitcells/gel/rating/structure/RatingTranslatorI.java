package net.splitcells.gel.rating.structure;

import static net.splitcells.dem.data.set.map.Maps.map;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import net.splitcells.dem.data.set.map.Map;

public class RatingTranslatorI implements RatingTranslator {
    private final Map<Class<? extends Rating>, Rating> novērtējumi;

    public static RatingTranslator ratingTranslator(Map<Class<? extends Rating>, Rating> novērtējumi) {
        return new RatingTranslatorI(novērtējumi);
    }

    protected RatingTranslatorI(Map<Class<? extends Rating>, Rating> novērtējumi) {
        this.novērtējumi = novērtējumi;
    }

    protected final Map<Class<? extends Rating>
            , Map<Predicate<Map<Class<? extends Rating>, Rating>>
            , Function<Map<Class<? extends Rating>, Rating>, Rating>>> tulki = map();

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Rating> T tulkošana(Class<T> target) {
        if (novērtējumi.containsKey(target)) {
            return (T) novērtējumi.get(target);
        }
        Function<Map<Class<? extends Rating>, Rating>, Rating> derīgsTulks;
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
    public void reģistrēTulks(Class<? extends Rating> mērķis
            , Predicate<Map<Class<? extends Rating>, Rating>> nosacījums
            , Function<Map<Class<? extends Rating>, Rating>, Rating> tulks) {
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
