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