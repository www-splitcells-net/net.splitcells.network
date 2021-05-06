package net.splitcells.dem.data.set;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static net.splitcells.dem.utils.NotImplementedYet.not_implemented_yet;

public final class SetFI implements SetF {

	@Override
	public <T> Set<T> lagacySet() {
		return new HashSet<>();
	}

	@Override
	public <T> Set<T> legacySet(Collection<T> arg) {
		return new HashSet<>(arg);
	}

	@Override
	public <T> net.splitcells.dem.data.set.Set<T> set() {
		return null;
	}

	@Override
	public <T> net.splitcells.dem.data.set.Set<T> set(Collection<T> arg) {
		throw notImplementedYet();
	}

}
