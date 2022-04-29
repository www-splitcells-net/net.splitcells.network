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

import net.splitcells.dem.lang.annotations.JavaLegacyBody;

import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Random;

@JavaLegacyBody
public class BuilderRandomDeterministic implements RndSrcF {

    private final Random seedGenerator;
    // HACK constructor argument should be parameterized
    private final SecureRandom seedGenerator_crypt;

    public BuilderRandomDeterministic(long seed) {
        seedGenerator = new Random(seed);
        seedGenerator_crypt = new SecureRandom(
                ByteBuffer.allocate(Long.BYTES).putLong(seed).array());
    }

    @Override
    public Randomness rnd(Long seed) {
        return new JavaRandomWrapper(new Random(seed));
    }

    @Override
    public Randomness rnd() {
        return new JavaRandomWrapper(new Random(seedGenerator.nextLong()));
    }

    @Override
    public RndSrcCrypt rndCrypt() {
        return new JavaRandomWrapper(
                new SecureRandom(seedGenerator_crypt.generateSeed(20)));
    }

}
