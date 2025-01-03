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

import net.splitcells.dem.lang.annotations.JavaLegacyBody;

import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;

public final class SetFactoryImpl implements SetF {
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
        throw notImplementedYet();
    }

    @JavaLegacyBody
    @Override
    public <T> Set<T> set(java.util.Collection<T> arg) {
        throw notImplementedYet();
    }

}
