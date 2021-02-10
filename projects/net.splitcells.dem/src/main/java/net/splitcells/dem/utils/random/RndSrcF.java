package net.splitcells.dem.utils.random;

public interface RndSrcF {

    Randomness rnd(Long seed);

    Randomness rnd();

    RndSrcCrypt rndCrypt();
}
