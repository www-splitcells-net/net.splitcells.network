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
package net.splitcells.dem.data.set;

import net.splitcells.dem.lang.annotations.ReturnsThis;

import java.util.Collection;

public interface SetWA<T> {

	/**
	 * TODO Create method which adds one if not present and otherwise throws exception.
	 */
	@ReturnsThis
	<R extends SetWA<T>> R add(T value);

	@SuppressWarnings("unchecked")
	@ReturnsThis
	default <R extends SetWA<T>> R addAll(T... values) {
		for (int i = 0; i < values.length; ++i) {
			add(values[i]);
		}
		return (R) this;
	}

	@SuppressWarnings("unchecked")
	@ReturnsThis
	default <R extends SetWA<T>> R addAll(Collection<T> values) {
		values.forEach((value) -> add(value));
		return (R) this;
	}

	static <B> SetWA<B> extend(final B prefix, final SetWA<B> sender) {
		return new SetWA<B>() {
			@SuppressWarnings("unchecked")
			@ReturnsThis
			public <R extends SetWA<B>> R add(B arg) {
				sender.addAll(prefix, arg);
				return (R) this;
			}
		};
	}

	static <A, B> SetWA<B> extend(final B first_prefix, final B default_prefix, final SetWA<B> sender) {
		return new SetWA<B>() {

			private boolean wasFirst_added = false;

			@SuppressWarnings("unchecked")
			@ReturnsThis
			public <R extends SetWA<B>> R add(B arg) {
				if (wasFirst_added) {
					sender.addAll(default_prefix, arg);
				} else {
					sender.addAll(first_prefix, arg);
					wasFirst_added = true;
				}
				return (R) this;
			}
		};
	}
}
