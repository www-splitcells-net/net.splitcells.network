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
import net.splitcells.dem.lang.annotations.JavaLegacyArtifact;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.utils.ExecutionException.executionException;

@JavaLegacyArtifact
public class MapLegacyWrapper<Key, Value> implements Map<Key, Value> {

    public static <Key, Value> Map<Key, Value> mapLegacyWrapper(java.util.Map<Key, Value> content) {
        return new MapLegacyWrapper<>(content, Optional.empty());
    }

    public static <Key, Value> Map<Key, Value> mapLegacyWrapper(java.util.Map<Key, Value> content, Boolean isDeterministic) {
        return new MapLegacyWrapper<>(content, Optional.of(isDeterministic));
    }

    private final java.util.Map<Key, Value> content;
    private final Optional<Boolean> isDeterministic;

    private MapLegacyWrapper(java.util.Map<Key, Value> content, Optional<Boolean> isDeterministic) {
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
    public Value get(Object key) {
        return content.get(key);
    }

    @Override
    public Map<Key, Value> ensurePresence(Key key, Value value) {
        content.put(key, value);
        return this;
    }

    @Override
    public Value ensurePresenceAndValue(Key key, Value value) {
        return content.put(key, value);
    }

    @Override
    public Value put(Key key, Value value) {
        if (StaticFlags.ENFORCING_UNIT_CONSISTENCY
                && containsKey(key)) {
            throw executionException(tree("Key already exists")
                    .withProperty("key", key.toString())
                    .withProperty("value", value.toString()));
        }
        return content.put(key, value);
    }

    @Override
    public Value remove(Object key) {
        return content.remove(key);
    }

    @Override
    public void putAll(java.util.Map<? extends Key, ? extends Value> m) {
        content.putAll(m);
    }

    @Override
    public void clear() {
        content.clear();
    }

    @Override
    public Set<Key> keySet() {
        return content.keySet();
    }

    @Override
    public Collection<Value> values() {
        return content.values();
    }

    @Override
    public Set<Entry<Key, Value>> entrySet() {
        return content.entrySet();
    }

    @Override
    public Optional<Boolean> _isDeterministic() {
        return isDeterministic;
    }
}
