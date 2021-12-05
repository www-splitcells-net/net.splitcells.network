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

import net.splitcells.dem.environment.config.StaticFlags;
import net.splitcells.dem.lang.annotations.JavaLegacyArtifact;

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
	 * The relation of {@param arg} to {@link this}:
	 * this is <EQUAL|LESSER_THAN|GREATER_THANT> to arg.
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
			if (StaticFlags.ENFORCING_UNIT_CONSISTENCY) {
				assertThat(rBase).isGreaterThan(0);
			}
			return Ordering.GREATER_THAN;
		}
	}

	@JavaLegacyArtifact
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
