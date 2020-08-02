package net.splitcells.dem.resource;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.annotations.Returns_this;

import java.util.function.Function;

import static net.splitcells.dem.data.set.list.Lists.list;

public abstract class AspectOrientedConstructor<T> {
    private List<Function<T, T>> aspects = list();

    @Returns_this
    public AspectOrientedConstructor<T> withAspect(Function<T, T> aspect) {
        aspects.add(aspect);
        return this;
    }

    protected T joinAspects(T arg) {
        T joinedAspects = arg;
        for (final var aspect : aspects) {
            joinedAspects = aspect.apply(joinedAspects);
        }
        return joinedAspects;
    }
}
