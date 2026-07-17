/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.utils.random;

import net.splitcells.dem.lang.annotations.JavaLegacy;

import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Random;

@JavaLegacy
public class BuilderRandomDeterministic implements RndSrcF {

    private final Random seedGenerator;
    // HACK constructor argument should be parameterized
    private final SecureRandom seedGeneratorCrypt;

    public BuilderRandomDeterministic(long seed) {
        seedGenerator = new Random(seed);
        seedGeneratorCrypt = new SecureRandom(
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
                new SecureRandom(seedGeneratorCrypt.generateSeed(20)));
    }

}
