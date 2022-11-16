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

import net.splitcells.dem.lang.annotations.JavaLegacyArtifact;

public class MutablePair<K, V> {

    public static <A, B> MutablePair<A, B> mutablePair(A key, B value) {
        return new MutablePair<A, B>(key, value);
    }

    private K key;
    private V value;

    private MutablePair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K key() {
        return key;
    }

    public MutablePair<K, V> withKey(K arg) {
        key = arg;
        return this;
    }

    public V value() {
        return value;
    }

    public MutablePair<K, V> withValue(V arg) {
        value = arg;
        return this;
    }

}