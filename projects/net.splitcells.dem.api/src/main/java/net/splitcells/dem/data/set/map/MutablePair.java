/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.data.set.map;

public class MutablePair<K, V> {

    public static <A, B> MutablePair<A, B> mutablePair(A key, B value) {
        return new MutablePair<>(key, value);
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