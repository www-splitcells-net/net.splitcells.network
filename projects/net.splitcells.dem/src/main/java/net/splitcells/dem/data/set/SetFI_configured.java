package net.splitcells.dem.data.set;

import net.splitcells.dem.Dem;
import net.splitcells.dem.data.atom.Bool;
import net.splitcells.dem.environment.config.IsDeterministic;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import static net.splitcells.dem.data.set.SetFI_deterministic.setFI_deterministic;
import static net.splitcells.dem.data.set.SetFI_random.setFI_random;
import static net.splitcells.dem.data.set.SetLegacyWrapper.setLegacyWrapper;

public class SetFI_configured implements SetF {
    public static SetF setFiConfigured() {
        return new SetFI_configured();
    }

    private SetF setF;

    private SetFI_configured() {
        final var isDeterministic = Dem.configValue(IsDeterministic.class);
        if (isDeterministic.isPresent() && isDeterministic.get().is_true()) {
            setF = setFI_deterministic();
        } else {
            setF = setFI_random();
        }
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
    @Deprecated
    private void update(Optional<Bool> oldValue, Optional<Bool> newValue) {
        if (newValue.isEmpty()) {
            setF = new SetFI();
        } else if (newValue.get().is_true()) {
            setF = setFI_deterministic();
        } else {
            setF = setFI_random();
        }
    }

    @Override
    public <T> net.splitcells.dem.data.set.Set<T> set() {
        return setF.set();
    }

    @Override
    public <T> net.splitcells.dem.data.set.Set<T> set(Collection<T> arg) {
        return setF.set(arg);
    }
}
