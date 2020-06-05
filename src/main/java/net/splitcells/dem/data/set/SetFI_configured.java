package net.splitcells.dem.data.set;

import net.splitcells.dem.data.atom.Bool;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import static net.splitcells.dem.data.set.SetLegacyWrapper.setLegacyWrapper;

public class SetFI_configured implements SetF {
	private SetF setF = new SetFI();

	public SetFI_configured() {
	}

	@Override
	public <T> Set<T> lagacySet() {
		return setF.lagacySet();
	}

	@Override
	public <T> Set<T> legacySet(Collection<T> arg) {
		return setF.legacySet(arg);
	}

	/**
	 * TODO Prevent unnecessary object construction.
	 */
	public void update(Optional<Bool> oldValue, Optional<Bool> newValue) {
		if (newValue.isEmpty()) {
			setF = new SetFI();
		} else if (newValue.get().is_true()) {
			setF = new SetFI_deterministic();
		} else {
			setF = new SetFI_random();
		}
	}

	@Override
	public <T> net.splitcells.dem.data.set.Set<T> set() {
		return setLegacyWrapper();
	}

	@Override
	public <T> net.splitcells.dem.data.set.Set<T> set(Collection<T> arg) {
		return SetLegacyWrapper.<T>setLegacyWrapper().with(arg);
	}
}
