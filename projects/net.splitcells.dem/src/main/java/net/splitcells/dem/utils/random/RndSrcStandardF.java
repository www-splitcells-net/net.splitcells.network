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

import java.security.SecureRandom;
import java.util.Random;

public class RndSrcStandardF implements RndSrcF {

    public final Random seedSrc = new Random();

    public RndSrcStandardF() {
    }

    @Override
    public Randomness rnd(Long seed) {
        return new JavaRandomWrapper(new Random(seed));
    }

    @Override
    public Randomness rnd() {
        return new JavaRandomWrapper(new Random());
    }

    @Override
    public RndSrcCrypt rndCrypt() {
        return new JavaRandomWrapper(new SecureRandom());
    }
}