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
package net.splitcells.dem.utils.random;

import net.splitcells.dem.utils.ConstructorIllegal;

public class RandomnessSource {

	private static final BuilderRandomConfigurable factory = new BuilderRandomConfigurable();

	private RandomnessSource() {
		throw new ConstructorIllegal();
	}

	public static Randomness randomness() {
		return factory.rnd();
	}

	public static Randomness randomness(Long seed) {
		return factory.rnd(seed);
	}

}
