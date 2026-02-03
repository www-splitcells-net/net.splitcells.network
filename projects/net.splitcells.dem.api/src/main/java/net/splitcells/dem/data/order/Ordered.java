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
package net.splitcells.dem.data.order;

import net.splitcells.dem.environment.config.StaticFlags;
import net.splitcells.dem.lang.annotations.JavaLegacy;

import static net.splitcells.dem.data.atom.Bools.bool;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * The natural (default) order is ascending (from lower to higher).
 * One of the default methods have to be implemented. If this is not done,
 * this will cause an Exception because of an infinite recursion.
 *
 * @param <T>
 */
public interface Ordered<T> extends Comparable<T>, OrderingCheck<T> {
	default boolean equalz(T other) {
		return compare_to(other) == Ordering.EQUAL;
	}

	/**
	 * TODO Test if {@link #compare_to} is implemented for subclass of T.
	 * 
	 * The relation of {@param arg} to this:
	 * this is &lt;EQUAL|LESSER_THAN|GREATER_THANT&gt; to arg.
	 * Default order is ascending.
	 */
	default Ordering compare_to(T arg) {
		final int rBase = compareTo(arg);
		if (rBase == 0) {
			if (StaticFlags.ENFORCING_UNIT_CONSISTENCY) {
				assertThat(equals(arg)).isTrue();
			}
			return Ordering.EQUAL;
		} else if (rBase < 0) {
			return Ordering.LESSER_THAN;
		} else {
			if (StaticFlags.ENFORCING_UNIT_CONSISTENCY) {
				assertThat(rBase).isGreaterThan(0);
			}
			return Ordering.GREATER_THAN;
		}
	}

	@JavaLegacy
	@Override
	default int compareTo(T arg) {
		final Ordering rBase = compare_to(arg);
		if (rBase == Ordering.EQUAL) {
			bool(equals(arg)).required();
			return 0;
		} else if (rBase == Ordering.LESSER_THAN) {
			return -1;
		} else {
			if (StaticFlags.ENFORCING_UNIT_CONSISTENCY) {
				assertThat(rBase).isEqualTo(Ordering.GREATER_THAN);
			}
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
