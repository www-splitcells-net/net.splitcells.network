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

import net.splitcells.dem.environment.resource.ResourceOptionImpl;
import net.splitcells.dem.lang.annotations.JavaLegacy;

import java.util.concurrent.ConcurrentHashMap;

import static net.splitcells.dem.Dem.configValue;
import static net.splitcells.dem.data.set.map.DeterministicMapFactory.deterministicMapFactory;
import static net.splitcells.dem.data.set.map.MapLegacyWrapper.mapLegacyWrapper;

public class Maps extends ResourceOptionImpl<MapFactory> {

    public Maps() {
        super(() -> deterministicMapFactory());
    }

    public static <K, V> Map<K, V> map() {
        return configValue(Maps.class).map();
    }

    public static <K2, V1> Map<K2, V1> map(Map<K2, V1> arg) {
        var rVal = configValue(Maps.class).<K2, V1>map();
        arg.entrySet().forEach(entry -> rVal.put(entry.getKey(), entry.getValue()));
        return rVal;
    }

    @JavaLegacy
    public static <K, V> java.util.stream.Collector<Pair<K, V>, ?, Map<K, V>> toMap() {
        return java.util.stream.Collector.of(
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
    @JavaLegacy
    @Deprecated
    public static <T> Map<Class<? extends T>, T> variadicTypeMapping(@SuppressWarnings("unchecked") T... values) {
        return typeMapping(java.util.Arrays.asList(values));
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
    @JavaLegacy
    @SuppressWarnings("unchecked")
    @Deprecated
    public static <T> Map<Class<? extends T>, T> typeMapping(java.util.Collection<T> values) {
        final Map<Class<? extends T>, T> rVal = configValue(Maps.class).map();
        values.forEach(value -> {
            if (rVal.containsKey(value.getClass())) {
                throw new RuntimeException();
            }
            rVal.put((Class<? extends T>) value.getClass(), value);
        });
        return rVal;
    }

    public static <K,V> Map<K,V> synchronizedMap() {
        return mapLegacyWrapper(new ConcurrentHashMap<>(), false);
    }

}
