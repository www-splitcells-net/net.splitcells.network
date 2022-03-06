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

import net.splitcells.dem.data.set.SetF;
import net.splitcells.dem.environment.resource.ResourceI;

import java.util.Collection;
import java.util.function.Supplier;
import java.util.stream.Collector;

import static java.util.Arrays.asList;
import static net.splitcells.dem.Dem.configValue;
import static net.splitcells.dem.data.set.map.MapFI_deterministic.mapFI_deterministic;
import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;

public class Maps extends ResourceI<MapF> {

    public Maps() {
        super(() -> mapFI_deterministic());
    }

    public static <Key, Value> Map<Key, Value> map() {
        return configValue(Maps.class).map();
    }

    public static <Key, Value> Map<Key, Value> map(Map<Key, Value> arg) {
        var rVal = configValue(Maps.class).<Key, Value>map();
        arg.entrySet().forEach(entry -> rVal.put(entry.getKey(), entry.getValue()));
        return rVal;
    }

    public static <K, V> Collector<Pair<K, V>, ?, Map<K, V>> toMap() {
        return Collector.of(
                () -> configValue(Maps.class).map(),
                (a, b) -> a.put(b.getKey(), b.getValue()),
                (a, b) -> {
                    b.entrySet().forEach(entry -> a.put(entry.getKey(), entry.getValue()));
                    return a;
                }
        );
    }

    /**
     * TODO Remove this, because it is not used anywhere.
     */
    @Deprecated
    public static <T> Map<Class<? extends T>, T> variadicTypeMapping(@SuppressWarnings("unchecked") T... values) {
        return typeMapping(asList(values));
    }

    /**
     * TODO Create dedicated typed mapping interface and implementations.
     * Create one implementation based on a {@link Map} and one implementation
     * based on a {@link net.splitcells.dem.data.set.list.List}.
     *
     * @param values values
     * @param <T>    type
     * @return return
     */
    @SuppressWarnings("unchecked")
    @Deprecated
    public static <T> Map<Class<? extends T>, T> typeMapping(Collection<T> values) {
        final Map<Class<? extends T>, T> rVal = configValue(Maps.class).map();
        values.forEach(value -> {
            if (rVal.containsKey(value.getClass())) {
                throw new RuntimeException();
            }
            rVal.put((Class<? extends T>) value.getClass(), value);
        });
        return rVal;
    }

}
