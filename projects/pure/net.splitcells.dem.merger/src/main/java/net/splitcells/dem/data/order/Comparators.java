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
package net.splitcells.dem.data.order;

import net.splitcells.dem.lang.annotations.JavaLegacyArtifact;

import java.util.function.BiFunction;

/**
 * Provides a wrapper for {@link java.util.Comparator} instances.
 *
 * @param <T>
 */
@JavaLegacyArtifact
public class Comparators<T> implements Comparator<T> {
	
	public static <T> Comparators<T> comparator(BiFunction<T, T, Integer> comparator) {
		return new Comparators<>(comparator);
	}

	private final BiFunction<T, T, Integer> comparator;
	
	private Comparators(BiFunction<T, T, Integer> comparator) {
		this.comparator = comparator;
	}

	@Override
	public int compare(T a, T b) {
		return comparator.apply(a, b);
	}

	@Override
	public boolean equals(Object arg) {
		if (arg != null && arg instanceof Comparators) {
			return this.comparator.equals(((Comparators) arg).comparator);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return comparator.hashCode();
	}
}
