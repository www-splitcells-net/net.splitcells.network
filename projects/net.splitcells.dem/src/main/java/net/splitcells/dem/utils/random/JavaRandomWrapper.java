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
package net.splitcells.dem.utils.random;

import java.util.Random;

import static net.splitcells.dem.environment.config.StaticFlags.ENFORCING_UNIT_CONSISTENCY;
import static org.assertj.core.api.Assertions.assertThat;

public class JavaRandomWrapper implements Randomness, RndSrcCrypt {
	private final Random rnd;

	protected JavaRandomWrapper(Random arg_rnd) {
		rnd = arg_rnd;
	}

	@Override
	public Random asRandom() {
		return rnd;
	}

	@Override
	public int integer() {
		return rnd.nextInt();
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
