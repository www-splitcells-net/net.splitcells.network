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

import net.splitcells.dem.data.set.Set;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public interface Map<Key, Value> extends java.util.Map<Key, Value> {

    default Map<Key, Value> with(Key key, Value value) {
        put(key, value);
        return this;
    }

    default Map<Key, Value> withMerged(Map<Key, Value> args, BiFunction<Value, Value, Value> mergeFunction) {
        args.forEach((aKey, aVal) -> this.merge(aKey, aVal, mergeFunction));
        return this;
    }

    /**
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
}
