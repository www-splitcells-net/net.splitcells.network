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
package net.splitcells.dem.data.set.map;

import net.splitcells.dem.environment.config.StaticFlags;
import net.splitcells.dem.lang.annotations.JavaLegacy;
import net.splitcells.dem.utils.ExecutionException;

import java.util.HashMap;

import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.testing.Assertions.requireNotNull;
import static net.splitcells.dem.utils.ExecutionException.execException;

@JavaLegacy
public class MapImpl<K, V> extends HashMap<K, V> implements Map<K, V> {

    @Override
    public Map<K, V> ensurePresence(K key, V value) {
        super.put(key, value);
        return this;
    }

    @Override
    public V put(K key, V value) {
        if (StaticFlags.ENFORCING_UNIT_CONSISTENCY
                && containsKey(key)) {
            throw ExecutionException.execException(tree("Key already exists")
                    .withProperty("key", key.toString())
                    .withProperty("value", value.toString()));
        }
        return super.put(key, value);
    }

    @Override
    public V ensurePresenceAndValue(K key, V value) {
        return super.put(key, value);
    }

    @Override
    public V get(Object key) {
        final var result = super.get(key);
        requireNotNull(result);
        return result;
    }

}
