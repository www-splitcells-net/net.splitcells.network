/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.data.set.map;

import net.splitcells.dem.lang.annotations.JavaLegacy;

import java.util.AbstractMap.SimpleImmutableEntry;

@JavaLegacy
public class Pair<K, V> extends SimpleImmutableEntry<K, V> {
	private static final long serialVersionUID = 6380721809354886151L;

	private Pair(K key, V value) {
		super(key, value);
	}

	public static <A, B> Pair<A, B> pair(A key, B value) {
		return new Pair<>(key, value);
	}
}