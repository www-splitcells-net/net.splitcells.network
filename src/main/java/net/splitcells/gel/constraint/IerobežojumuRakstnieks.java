package net.splitcells.gel.constraint;

import java.util.function.Function;

import net.splitcells.dem.lang.annotations.Returns_this;
import net.splitcells.dem.object.DiscoverableFromMultiplePathsSetter;

public interface IerobežojumuRakstnieks extends DiscoverableFromMultiplePathsSetter {
	@Returns_this
    Ierobežojums arBērnu(Ierobežojums... ierobežojums);

	@Returns_this
    Ierobežojums arBērnu(Function<Jautājums, Jautājums> būvētājs);
}
