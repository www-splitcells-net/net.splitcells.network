package net.splitcells.dem.data.set.list;

import net.splitcells.dem.data.set.SetWA;
import net.splitcells.dem.lang.annotations.Returns_this;

import java.util.Collection;

/**
 * TODOC What is the difference between append and add? -> add set specific and
 * the method does not guarrenties that is added to the of the list. Append adds
 * to end.
 */
public interface ListWA<T> extends SetWA<T> {
	@Returns_this
	<R extends ListWA<T>> R append(T arg);

	@Override
	default <R extends SetWA<T>> R add(T arg) {
		return append(arg);
	}

	@SuppressWarnings("unchecked")
	@Returns_this
	default <R extends ListWA<T>> R appendAll(T... arg) {
		for (int i = 0; i < arg.length; ++i) {
			append(arg[i]);
		}
		return (R) this;
	}

	@SuppressWarnings("unchecked")
	@Returns_this
	default <R extends ListWA<T>> R appendAll(Collection<T> arg) {
		arg.forEach(e -> this.append(e));
		return (R) this;
	}

	@Override
	default <R extends SetWA<T>> R addAll(Collection<T> value) {
		return appendAll(value);
	}
}
