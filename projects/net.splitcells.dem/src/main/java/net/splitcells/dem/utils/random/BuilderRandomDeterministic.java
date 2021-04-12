package net.splitcells.dem.utils.random;

import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Random;

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
