/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.data.set;

import net.splitcells.dem.lang.annotations.JavaLegacy;
import net.splitcells.dem.lang.annotations.ReturnsThis;

/**
 * This is a set interface that contains only methods to add values.
 *
 * @param <T> The type of values contained in this set.
 */
public interface AppendableSet<T> {

	/**
	 * TODO Create method which adds one if not present and otherwise throws exception.
	 */
	@ReturnsThis
	<R extends AppendableSet<T>> R add(T value);

	@SuppressWarnings("unchecked")
	@ReturnsThis
	default <R extends AppendableSet<T>> R addAll(T... values) {
		for (int i = 0; i < values.length; ++i) {
			add(values[i]);
		}
		return (R) this;
	}

	@JavaLegacy
	@SuppressWarnings("unchecked")
	@ReturnsThis
	default <R extends AppendableSet<T>> R addAll(java.util.Collection<T> values) {
		values.forEach(value -> add(value));
		return (R) this;
	}

	static <B> AppendableSet<B> extend(final B prefix, final AppendableSet<B> sender) {
		return new AppendableSet<B>() {
			@SuppressWarnings("unchecked")
			@ReturnsThis
			public <R extends AppendableSet<B>> R add(B arg) {
				sender.addAll(prefix, arg);
				return (R) this;
			}
		};
	}

	static <A, B> AppendableSet<B> extend(final B firstPrefix, final B defaultPrefix, final AppendableSet<B> sender) {
		return new AppendableSet<B>() {

			private boolean wasFirstAdded = false;

			@SuppressWarnings("unchecked")
			@ReturnsThis
			public <R extends AppendableSet<B>> R add(B arg) {
				if (wasFirstAdded) {
					sender.addAll(defaultPrefix, arg);
				} else {
					sender.addAll(firstPrefix, arg);
					wasFirstAdded = true;
				}
				return (R) this;
			}
		};
	}
}
