package net.splitcells.dem.data.set;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import static net.splitcells.dem.data.set.SetLegacyWrapper.setLegacyWrapper;

public class SetFI_deterministic implements SetF {

    public static SetF setFI_deterministic() {
        return new SetFI_deterministic();
    }

    private SetFI_deterministic() {

    }

    @Override
    public <T> Set<T> lagacySet() {
        return new LinkedHashSet<>();
    }

    @Override
    public <T> Set<T> legacySet(Collection<T> arg) {
        return new LinkedHashSet<>();
    }

    @Override
    public <T> net.splitcells.dem.data.set.Set<T> set() {
        return setLegacyWrapper(new LinkedHashSet<>(), true);
    }

    @Override
    public <T> net.splitcells.dem.data.set.Set<T> set(Collection<T> arg) {
        return setLegacyWrapper(new LinkedHashSet<>(arg), true);
    }

}
