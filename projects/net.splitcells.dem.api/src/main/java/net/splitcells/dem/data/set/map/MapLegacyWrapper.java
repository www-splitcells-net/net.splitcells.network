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

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.utils.ExecutionException.execException;

@JavaLegacy
public class MapLegacyWrapper<K, V> implements Map<K, V> {

    public static <K1, V1> Map<K1, V1> mapLegacyWrapper(java.util.Map<K1, V1> content) {
        return new MapLegacyWrapper<>(content, Optional.empty());
    }

    public static <K1, V1> Map<K1, V1> mapLegacyWrapper(java.util.Map<K1, V1> content, Boolean isDeterministic) {
        return new MapLegacyWrapper<>(content, Optional.of(isDeterministic));
    }

    private final java.util.Map<K, V> content;
    private final Optional<Boolean> isDeterministic;

    private MapLegacyWrapper(java.util.Map<K, V> content, Optional<Boolean> isDeterministic) {
        this.content = content;
        this.isDeterministic = isDeterministic;
    }

    @Override
    public int size() {
        return content.size();
    }

    @Override
    public boolean isEmpty() {
        return content.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return content.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return content.containsValue(value);
    }

    @Override
    public V get(Object key) {
        return content.get(key);
    }

    @Override
    public Map<K, V> ensurePresence(K key, V value) {
        content.put(key, value);
        return this;
    }

    @Override
    public V ensurePresenceAndValue(K key, V value) {
        return content.put(key, value);
    }

    @Override
    public V put(K key, V value) {
        if (StaticFlags.ENFORCING_UNIT_CONSISTENCY
                && containsKey(key)) {
            throw ExecutionException.execException(tree("Key already exists")
                    .withProperty("key", key.toString())
                    .withProperty("value", value.toString()));
        }
        return content.put(key, value);
    }

    @Override
    public V remove(Object key) {
        return content.remove(key);
    }

    @Override
    public void putAll(java.util.Map<? extends K, ? extends V> m) {
        content.putAll(m);
    }

    @Override
    public void clear() {
        content.clear();
    }

    @Override
    public Set<K> keySet() {
        return content.keySet();
    }

    @Override
    public Collection<V> values() {
        return content.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return content.entrySet();
    }

    @Override
    public Optional<Boolean> _isDeterministic() {
        return isDeterministic;
    }

    @Override
    public String toString() {
        return content.toString();
    }
}
