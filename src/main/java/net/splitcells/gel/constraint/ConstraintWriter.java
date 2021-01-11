package net.splitcells.gel.constraint;

import java.util.function.Function;

import net.splitcells.dem.lang.annotations.Returns_this;
import net.splitcells.dem.object.DiscoverableFromMultiplePathsSetter;

public interface ConstraintWriter extends DiscoverableFromMultiplePathsSetter {
	@Returns_this
    Constraint withChildren(Constraint... ierobežojums);

	@Returns_this
    Constraint withChildren(Function<Query, Query> būvētājs);
}
