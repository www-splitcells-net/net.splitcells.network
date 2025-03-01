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

import net.splitcells.dem.data.Flow;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.lang.annotations.JavaLegacyArtifact;
import net.splitcells.dem.utils.ExecutionException;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import static net.splitcells.dem.data.Flows.flow;
import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.utils.ExecutionException.execException;

@JavaLegacyArtifact
public interface Map<K, V> extends java.util.Map<K, V> {

    default Optional<V> getOptionally(K key) {
        return Optional.ofNullable(get(key));
    }

    default Map<K, V> with(K key, V value) {
        put(key, value);
        return this;
    }

    default Flow<java.util.Map.Entry<K, V>> flowMappingsByValue(V value) {
        return flow(entrySet().stream().filter(entry -> value.equals(entry.getValue())));
    }

    /**
     * Adds the given key value relationship to this,
     * regardless if this key is already present in this.
     * It is encourage to use {@link #put(Object, Object)} instead,
     * in order to minimize bug potential.
     *
     * @param key
     * @param value
     * @return
     */
    Map<K, V> ensurePresence(K key, V value);

    /**
     * Works like {@link #ensurePresence(Object, Object)}, but returns the given value instead.
     *
     * @param key
     * @param value
     * @return
     */
    V ensurePresenceAndValue(K key, V value);


    @Override
    default V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> updateFunction) {
        final V oldValue = get(key);
        if (oldValue != null) {
            final var newValue = updateFunction.apply(key, oldValue);
            if (newValue != null) {
                ensurePresence(key, newValue);
            } else {
                remove(key);
            }
            return newValue;
        }
        return null;
    }

    /**
     * <p>This method should throw an exception, if the key is already present.
     * The reason for that, is that in this case, there is an higher chance of a programmer bug being present.</p>
     * <p>If the key is added regardless of the fact, if this already has the key or not,
     * use {@link #ensurePresence(Object, Object)} instead.
     * This method name is shorter than {@link #ensurePresence(Object, Object)},
     * which encourages to use this safer method.</p>
     *
     * @param key
     * @param value
     * @return
     */
    V put(K key, V value);

    default net.splitcells.dem.data.set.Set<K> keySet2() {
        return setOfUniques(keySet());
    }

    default Map<K, V> withMerged(Map<K, V> args, BiFunction<V, V, V> mergeFunction) {
        args.forEach((aKey, aVal) -> this.merge(aKey, aVal, mergeFunction));
        return this;
    }

    /**
     * Add mappings to this, which are not present yet.
     * Mappings with already present keys are ignored.
     *
     * @param args This contains the mappings to be added.
     * @return Returns this.
     */
    default Map<K, V> withMissingEntries(Map<K, V> args) {
        args.entrySet().forEach(e -> {
            if (!containsKey(e.getKey())) {
                put(e.getKey(), e.getValue());
            }
        });
        return this;
    }

    /**
     * Add mappings as given to this, which are not present yet.
     * Mappings with already present keys are used to update the existing value
     * for the respective key in this via a merger function.
     *
     * @param args
     * @param merger
     * @return
     */
    default Map<K, V> withMergedEntries(Map<K, V> args, BiFunction<V, V, V> merger) {
        args.entrySet().forEach(e -> {
            final var key = e.getKey();
            final var existingValue = get(key);
            if (existingValue == null) {
                put(key, e.getValue());
            } else {
                ensurePresence(key, merger.apply(get(key), e.getValue()));
            }
        });
        return this;
    }

    /**
     * TODO Is this a duplicate of {@link #computeIfAbsent(Object, Function)}?
     * <p>
     * RENAME
     */
    default V addIfAbsent(K key, Supplier<V> valueSupplier) {
        V rVal = get(key);
        if (!containsKey(key)) {
            rVal = valueSupplier.get();
            put(key, rVal);
        }
        return rVal;
    }

    /**
     * Determines if actions on this {@link Map} are deterministic.
     * <p>
     * This is only used in order to test {@link Map} factories.
     *
     * @return Is this determinstic.
     */
    default Optional<Boolean> _isDeterministic() {
        return Optional.empty();
    }

    default void requireEmpty() {
        if (!isEmpty()) {
            throw ExecutionException.execException("Expecting map to be empty, but is not: " + this);
        }
    }

    default void requireSizeOf(int arg) {
        if (size() != arg) {
            throw ExecutionException.execException("Map should be size of " + arg + " but has size of " + size() + " instead: " + this);
        }
    }

    default Map<K, V> requireKeyAbsence(K key) {
        if (containsKey(key)) {
            throw ExecutionException.execException(tree("Map should not contain given key, but has it.")
                    .withProperty("map", toString())
                    .withProperty("key", key.toString()));
        }
        return this;
    }

    default void requireEqualityTo(Map<K, V> requiredContent) {
        requiredContent.keySet2().forEach(requiredKey -> {
            if (!containsKey(requiredKey)) {
                throw ExecutionException.execException(tree("2 sets should be equal, but are not.")
                        .withProperty("this", toString())
                        .withProperty("requiredContent", requiredContent.toString())
                        .withProperty("Missing key in this", requiredKey.toString()));
            }
            if (!requiredContent.get(requiredKey).equals(get(requiredKey))) {
                throw ExecutionException.execException(tree("2 sets should be equal, but are not.")
                        .withProperty("this", toString())
                        .withProperty("requiredContent", requiredContent.toString())
                        .withProperty("Unequal value for key", requiredKey.toString())
                        .withProperty("Unequal value in this", get(requiredKey).toString())
                        .withProperty("Unequal value in this", requiredContent.get(requiredKey).toString()));
            }
        });
        keySet2().forEach(key -> {
            if (!requiredContent.containsKey(key)) {
                throw ExecutionException.execException(tree("2 sets should be equal, but are not.")
                        .withProperty("this", toString())
                        .withProperty("requiredContent", requiredContent.toString())
                        .withProperty("Missing key in required content", key.toString()));
            }
            if (!requiredContent.get(key).equals(get(key))) {
                throw ExecutionException.execException(tree("2 sets should be equal, but are not.")
                        .withProperty("this", toString())
                        .withProperty("requiredContent", requiredContent.toString())
                        .withProperty("Unequal value for key", key.toString())
                        .withProperty("Unequal value in this", get(key).toString())
                        .withProperty("Unequal value in this", requiredContent.get(key).toString()));
            }
        });
    }

    default Map<K, V> shallowCopy() {
        return map(this);
    }

    default List<V> valueList() {
        return Lists.listWithValuesOf(values());
    }
}
