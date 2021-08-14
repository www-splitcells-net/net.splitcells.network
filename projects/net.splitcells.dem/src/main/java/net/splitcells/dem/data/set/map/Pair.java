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

import java.util.AbstractMap.SimpleImmutableEntry;

public class Pair<K, V> extends SimpleImmutableEntry<K, V> {
	private static final long serialVersionUID = 6380721809354886151L;

	private Pair(K key, V value) {
		super(key, value);
	}

	public static <A, B> Pair<A, B> pair(A key, B value) {
		return new Pair<A, B>(key, value);
	}
}