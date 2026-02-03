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
package net.splitcells.dem.utils.random;

import net.splitcells.dem.lang.annotations.JavaLegacy;

import java.util.Random;

import static net.splitcells.dem.environment.config.StaticFlags.ENFORCING_UNIT_CONSISTENCY;
import static org.assertj.core.api.Assertions.assertThat;

@JavaLegacy
public class JavaRandomWrapper implements Randomness, RndSrcCrypt {
	private final Random rnd;

	protected JavaRandomWrapper(Random argRnd) {
		rnd = argRnd;
	}

	@Override
	public Random asRandom() {
		return rnd;
	}

	@Override
	public int integer() {
		return rnd.nextInt();
	}

	@Override public float floating(float min, float max) {
		return rnd.nextFloat(min, max);
	}

	/**
	 * FIXME Allow negative min and max.
	 */
	@Override
	public int integer(final Integer min, final Integer max) {
		if (ENFORCING_UNIT_CONSISTENCY) {
			assertThat(min).isLessThanOrEqualTo(max);
			assertThat(min).isGreaterThanOrEqualTo(0);
			assertThat(max).isGreaterThanOrEqualTo(0);
			assertThat(max).isLessThan(Integer.MAX_VALUE);
		}
		int rVal = asRandom().nextInt(max + 1);
		// PERFORMANCE
		while (rVal < min) {
			rVal = asRandom().nextInt(max + 1);
		}
		return rVal;
	}
}
