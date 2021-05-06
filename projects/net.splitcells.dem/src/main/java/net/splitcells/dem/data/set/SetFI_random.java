package net.splitcells.dem.data.set;

import net.splitcells.dem.utils.NotImplementedYet;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static net.splitcells.dem.data.set.SetLegacyWrapper.setLegacyWrapper;

public class SetFI_random implements SetF {

    public static SetF setFI_random() {
        return new SetFI_random();
    }

    private SetFI_random() {

    }

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
        return setLegacyWrapper(new HashSet<>(), false);
    }

    @Override
    public <T> net.splitcells.dem.data.set.Set<T> set(Collection<T> arg) {
        return setLegacyWrapper(new HashSet<T>(), false).with(arg);
    }
}
