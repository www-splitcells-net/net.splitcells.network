/*
 * Copyright (c) 2021 Contributors To The `net.splitcells.*` Projects
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License v2.0 or later
 * which is available at https://www.gnu.org/licenses/old-licenses/gpl-2.0-standalone.html
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
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
