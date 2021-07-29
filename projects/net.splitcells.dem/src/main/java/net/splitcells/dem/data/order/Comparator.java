/*
 * Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License,
 * which is available at https://spdx.org/licenses/MIT.html.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License, version 2
 * or any later versions with the GNU Classpath Exception which is
 * available at https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT OR GPL-2.0-or-later WITH Classpath-exception-2.0
 */
package net.splitcells.dem.data.order;

import java.util.function.BiFunction;

public interface Comparator<T> extends java.util.Comparator<T> {

	static <T> Comparator<T> comparator(BiFunction<T, T, Ordering> comparator) {
		return new Comparator<T>() {
			@Override
			public Ordering compareTo(T a, T b) {
				return comparator.apply(a, b);
			}
		};
	}

	/**
	 * RENAME
	 * 
	 * @param comparator
	 * @return
	 */
	static <T> Comparator<T> comparator_(BiFunction<T, T, Integer> comparator) {
		return new Comparators<>(comparator);

	}

	default Ordering compareTo(T a, T b) {
		final int rBase = compare(a, b);
		if (rBase == 0) {
			return Ordering.EQUAL;
		} else if (rBase < 0) {
			return Ordering.LESSER_THAN;
		} else {
			assert rBase > 0;
			return Ordering.GREATER_THAN;
		}
	}

	@Override
	default int compare(T a, T b) {
		final Ordering rBase = compareTo(a, b);
		if (rBase == Ordering.EQUAL) {
			return 0;
		} else if (rBase == Ordering.LESSER_THAN) {
			return -1;
		} else {
			assert rBase == Ordering.GREATER_THAN;
			return 1;
		}
	}

	/**
	 * PERFORMANCE improvement of multiple inversions via public parent Comparator
	 * access method
	 */
	default Comparator<T> inverted() {
		var fThis = this;
		return new Comparator<T>() {

			@Override
			public Ordering compareTo(T a, T b) {
				return fThis.compareTo(b, a);
			}

			@Override
			public int compare(T a, T b) {
				return fThis.compare(b, a);
			}
		};
	}

}
