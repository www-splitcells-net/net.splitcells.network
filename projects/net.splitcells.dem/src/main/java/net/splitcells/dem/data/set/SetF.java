package net.splitcells.dem.data.set;

import java.util.Collection;

/**
 * TODO Specify expected min/average/max size
 *
 * TODO Specify required performance signature.
 */
public interface SetF {

	<T> Set<T> set();

	<T> Set<T> set(Collection<T> arg);

	@Deprecated
	<T> java.util.Set<T> lagacySet();

	@Deprecated
	<T> java.util.Set<T> legacySet(Collection<T> arg);
}
