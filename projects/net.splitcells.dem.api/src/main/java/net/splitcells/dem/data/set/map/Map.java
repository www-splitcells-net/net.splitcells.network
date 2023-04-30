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

import net.splitcells.dem.lang.annotations.JavaLegacyArtifact;
import net.splitcells.dem.lang.annotations.JavaLegacyBody;

import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.utils.ExecutionException.executionException;

@JavaLegacyArtifact
public interface Map<Key, Value> extends java.util.Map<Key, Value> {

    default Optional<Value> getOptionally(Key key) {
        return Optional.ofNullable(get(key));
    }

    default Map<Key, Value> with(Key key, Value value) {
        put(key, value);
        return this;
    }

    default net.splitcells.dem.data.set.Set<Key> keySet2() {
        return setOfUniques(keySet());
    }

    default Map<Key, Value> withMerged(Map<Key, Value> args, BiFunction<Value, Value, Value> mergeFunction) {
        args.forEach((aKey, aVal) -> this.merge(aKey, aVal, mergeFunction));
        return this;
    }

    /**
     * TODO Is this a duplicate of {@link #computeIfAbsent(Object, Function)}?
     * <p>
     * RENAME
     */
    default Value addIfAbsent(Key key, Supplier<Value> valueSupplier) {
        Value rVal = get(key);
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
            throw executionException("Expecting map to be empty, but is not: " + this);
        }
    }

    default void requireSizeOf(int arg) {
        if (size() != arg) {
            throw executionException("Map should be size of " + arg + " but has size of " + size() + " instead: " + this);
        }
    }
}
