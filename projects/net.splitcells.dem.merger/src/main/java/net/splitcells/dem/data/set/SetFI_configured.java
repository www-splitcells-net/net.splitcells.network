/*
 * Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License,
 * which is available at https://spdx.org/licenses/MIT.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 */
package net.splitcells.dem.data.set;

import net.splitcells.dem.Dem;
import net.splitcells.dem.data.atom.Bool;
import net.splitcells.dem.environment.config.IsDeterministic;
import net.splitcells.dem.lang.annotations.JavaLegacyBody;

import java.util.Optional;

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
        if (isDeterministic.isPresent() && isDeterministic.get().isTrue()) {
            setF = setFI_deterministic();
        } else {
            setF = setFI_random();
        }
    }

    @JavaLegacyBody
    @Override
    public <T> java.util.Set<T> lagacySet() {
        return setF.lagacySet();
    }

    @JavaLegacyBody
    @Override
    public <T> java.util.Set<T> legacySet(java.util.Collection<T> arg) {
        return setF.legacySet(arg);
    }

    /**
     * TODO Prevent unnecessary object construction.
     */
    @Deprecated
    private void update(Optional<Bool> oldValue, Optional<Bool> newValue) {
        if (newValue.isEmpty()) {
            setF = new SetFI();
        } else if (newValue.get().isTrue()) {
            setF = setFI_deterministic();
        } else {
            setF = setFI_random();
        }
    }

    @Override
    public <T> Set<T> set() {
        return setF.set();
    }

    @JavaLegacyBody
    @Override
    public <T> Set<T> set(java.util.Collection<T> arg) {
        return setF.set(arg);
    }
}
