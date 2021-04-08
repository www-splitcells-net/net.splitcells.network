package net.splitcells.dem.data.set;

import net.splitcells.dem.Dem;
import net.splitcells.dem.environment.resource.ResourceI;

import java.util.Collection;
import java.util.stream.Collector;

import static java.util.Arrays.asList;
import static net.splitcells.dem.Dem.configValue;
import static net.splitcells.dem.data.set.SetFI_configured.setFiConfigured;

public class Sets extends ResourceI<SetF> {
    public Sets() {
        super(() -> setFiConfigured());
    }

    public static <T> Collector<T, ?, Set<T>> toSetOfUniques() {
        return Collector.of(
                Sets::<T>setOfUniques,
                Set::addAll,
                (a, b) -> {
                    a.addAll(b);
                    return a;
                }
        );
    }

    @SafeVarargs
    public static <T> Set<T> merge(Collection<T>... collections) {
        final var rVal = configValue(Sets.class).<T>set();
        for (Collection<T> collection : collections) {
            rVal.addAll(collection);
        }
        return rVal;
    }

    public static <T> Set<T> setOfUniques() {
        return configValue(Sets.class).<T>set();
    }

    @SafeVarargs
    public static <T> Set<T> setOfUniques(T... args) {
        return setOfUniques(asList(args));
    }

    public static <T> Set<T> setOfUniques(Collection<T> arg) {
        final var rVal = configValue(Sets.class).<T>set();
        rVal.addAll(arg);
        return rVal;
    }
}
