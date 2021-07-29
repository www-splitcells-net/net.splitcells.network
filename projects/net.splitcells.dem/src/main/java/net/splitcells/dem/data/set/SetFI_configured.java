/*
 * Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License,
 * which is available at https://spdx.org/licenses/MIT.html.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License, version 2
 * or any later versions with the GNU Classpath Exception which is
 * available at https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT OR GPL-2.0-or-later WITH Classpath-exception-2.0
 */
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
        if (isDeterministic.isPresent() && isDeterministic.get().isTrue()) {
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
        } else if (newValue.get().isTrue()) {
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
