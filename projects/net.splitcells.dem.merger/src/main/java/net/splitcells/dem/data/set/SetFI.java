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

import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;

public final class SetFI implements SetF {
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

    @Override
    public <T> Set<T> set() {
        return null;
    }

    @JavaLegacyBody
    @Override
    public <T> Set<T> set(java.util.Collection<T> arg) {
        throw notImplementedYet();
    }

}
