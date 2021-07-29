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

import static net.splitcells.dem.data.atom.Bools.bool;

/**
 * natural order is ascending (from lower to higher)
 * <p>
 * TODO Helper Method in order to generate instances of this interface via
 * lambdas.
 *
 * @param <T>
 */
public interface Ordered<T> extends Comparable<T>, OrderingCheck<T> {
	default boolean equalz(T other) {
		return compare_to(other) == Ordering.EQUAL;
	}

	/**
	 * TODO check if compare_to is implemented for subclass (i.e. interfaces) of T.
	 * 
	 * One of the default methods have to be implemented. If this is not done this
	 * will cause an Exception because of an infinite recursion.
	 * 
	 * The relation of arg to this: this is <EQUAL|LESSER_THAN|GREATER_THANT> arg.
	 * Default order is ascending.
	 */
	default Ordering compare_to(T arg) {
		final int rBase = compareTo(arg);
		if (rBase == 0) {
			bool(equals(arg)).required();
			return Ordering.EQUAL;
		} else if (rBase < 0) {
			return Ordering.LESSER_THAN;
		} else {
			assert rBase > 0;
			return Ordering.GREATER_THAN;
		}
	}

	@Override
	default int compareTo(T arg) {
		final Ordering rBase = compare_to(arg);
		if (rBase == Ordering.EQUAL) {
			bool(equals(arg)).required();
			return 0;
		} else if (rBase == Ordering.LESSER_THAN) {
			return -1;
		} else {
			assert rBase == Ordering.GREATER_THAN;
			return 1;
		}
	}

	@Override
	default boolean smallerThan(T other) {
		return compare_to(other).equals(Ordering.LESSER_THAN);
	}

	@Override
	default boolean smallerThanOrEqual(T other) {
		var rBase = compare_to(other);
		return rBase.equals(Ordering.LESSER_THAN) || rBase.equals(Ordering.EQUAL);
	}

	@Override
	default boolean greaterThan(T other) {
		return compare_to(other).equals(Ordering.GREATER_THAN);
	}

	@Override
	default boolean greaterThanOrEqual(T other) {
		var rBase = compare_to(other);
		return rBase.equals(Ordering.GREATER_THAN) || rBase.equals(Ordering.EQUAL);
	}

}
