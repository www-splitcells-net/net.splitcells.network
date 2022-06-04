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
import net.splitcells.dem.utils.NotImplementedYet;

import static net.splitcells.dem.data.set.SetLegacyWrapper.setLegacyWrapper;

public class SetFI_random implements SetF {

    public static SetF setFI_random() {
        return new SetFI_random();
    }

    private SetFI_random() {

    }

    @JavaLegacyBody
    @Override
    public <T> java.util.Set<T> lagacySet() {
        return new java.util.HashSet<>();
    }

    @JavaLegacyBody
    @Override
    public <T> java.util.Set<T> legacySet(java.util.Collection<T> arg) {
        return new java.util.HashSet<>(arg);
    }

    @JavaLegacyBody
    @Override
    public <T> net.splitcells.dem.data.set.Set<T> set() {
        return setLegacyWrapper(new java.util.HashSet<>(), false);
    }

    @JavaLegacyBody
    @Override
    public <T> net.splitcells.dem.data.set.Set<T> set(java.util.Collection<T> arg) {
        return setLegacyWrapper(new java.util.HashSet<T>(), false).with(arg);
    }
}
