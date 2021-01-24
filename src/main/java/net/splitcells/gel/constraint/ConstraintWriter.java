package net.splitcells.gel.constraint;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.annotations.Returns_this;
import net.splitcells.dem.object.DiscoverableFromMultiplePathsSetter;

public interface ConstraintWriter extends DiscoverableFromMultiplePathsSetter {
    @Returns_this
    Constraint withChildren(Constraint... constraints);

    @Returns_this
    Constraint withChildren(Function<Query, Query> builder);

    @SuppressWarnings("unchecked")
    @Returns_this
    Constraint withChildren(List<Function<Query, Query>> builder);
}
