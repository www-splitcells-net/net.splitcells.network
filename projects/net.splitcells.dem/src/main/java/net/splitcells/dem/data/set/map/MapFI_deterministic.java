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
package net.splitcells.dem.data.set.map;

import java.util.LinkedHashMap;

import static net.splitcells.dem.data.set.map.MapLegacyWrapper.mapLegacyWrapper;

public final class MapFI_deterministic implements MapF {

    @Override
    public <K, V> Map<K, V> map() {
        return mapLegacyWrapper(new LinkedHashMap<K, V>(), true);
    }

    @Override
    public <K, V> Map<K, V> map(java.util.Map<K, V> arg) {
        return mapLegacyWrapper(new LinkedHashMap<K, V>(arg), true);
    }

    @Override
    public void close() {
    }

}
