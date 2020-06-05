package net.splitcells.dem.data.set;

import java.util.Arrays;
import java.util.Collection;

import static java.util.Arrays.asList;

public interface Set<T> extends java.util.Set<T> {

	default void addAll(T... objects) {
		Arrays.stream(objects).forEach(e -> add(e));
	}

	default boolean containsAny(T... objects) {
		boolean rVal = false;
		final var containment = Arrays.stream(objects)
				.map(e -> contains(e))
				.reduce((a,b) -> a || b);
		return rVal || containment.orElse(false);
	}

	default Set<T> with(T... args) {
		addAll(asList(args));
		return this;
	}

	default Set<T> with(Collection<T> args) {
		addAll(args);
		return this;
	}
}
