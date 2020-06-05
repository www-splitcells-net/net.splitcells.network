package net.splitcells.dem.data.set.list;

import net.splitcells.dem.utils.ConstructorIllegal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collector;

public final class Lists {

    public static <T> Collector<T, ?, List<T>> toList() {
        return Collector.of(
                () -> list(),
                (a, b) -> a.addAll(b),
                (a, b) -> {
                    a.addAll(b);
                    return a;
                }
        );
    }

    private Lists() {
        throw new ConstructorIllegal();
    }

    @SafeVarargs
	public static <T> List<T> concat(Collection<T>... collections) {
        final var rVal = Lists.<T>list();
        for (Collection<T> collection : collections) {
            rVal.addAll(collection);
        }
        return rVal;
    }

    public static <T> List<T> list() {
        return new ListI<>();
    }

    public static <T> List<T> listWithValuesOf(Collection<T> values) {
        final var list = Lists.<T>list();
        list.addAll(values);
        return list;
    }

    @SafeVarargs
	public static <T> List<T> listWithValuesOf(T... values) {
        return  listWithValuesOf(Arrays.asList(values));
    }

    @SafeVarargs
    public static <T> List<T> list(T... args) {
        final var list = new ListI<T>();
        list.addAll(Arrays.asList(args));
        return list;
    }

    @SafeVarargs
    public static <T> java.util.List<T> _list(T... args) {
        return new ArrayList<>(Arrays.asList(args));
    }

}
