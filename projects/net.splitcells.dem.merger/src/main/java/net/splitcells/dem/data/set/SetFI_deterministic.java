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

import net.splitcells.dem.lang.annotations.JavaLegacyBody;

import static net.splitcells.dem.data.set.SetLegacyWrapper.setLegacyWrapper;

public class SetFI_deterministic implements SetF {

    public static SetF setFI_deterministic() {
        return new SetFI_deterministic();
    }

    private SetFI_deterministic() {

    }

    @JavaLegacyBody
    @Override
    public <T> java.util.Set<T> lagacySet() {
        return new java.util.LinkedHashSet<>();
    }

    @JavaLegacyBody
    @Override
    public <T> java.util.Set<T> legacySet(java.util.Collection<T> arg) {
        return new java.util.LinkedHashSet<>();
    }

    @JavaLegacyBody
    @Override
    public <T> net.splitcells.dem.data.set.Set<T> set() {
        return setLegacyWrapper(new java.util.LinkedHashSet<>(), true);
    }

    @JavaLegacyBody
    @Override
    public <T> net.splitcells.dem.data.set.Set<T> set(java.util.Collection<T> arg) {
        return setLegacyWrapper(new java.util.LinkedHashSet<>(arg), true);
    }

}
