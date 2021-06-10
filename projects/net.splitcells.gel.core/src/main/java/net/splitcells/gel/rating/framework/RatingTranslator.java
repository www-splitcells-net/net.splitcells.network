package net.splitcells.gel.rating.framework;

import java.util.function.Function;
import java.util.function.Predicate;

import net.splitcells.dem.data.set.map.Map;

public interface RatingTranslator {
	<T extends Rating> T translate(Class<T> type);

	void registerTranslator(Class<? extends Rating> target, Predicate<Map<Class<? extends Rating>, Rating>> condition,
							Function<Map<Class<? extends Rating>, Rating>, Rating> translator);

}
