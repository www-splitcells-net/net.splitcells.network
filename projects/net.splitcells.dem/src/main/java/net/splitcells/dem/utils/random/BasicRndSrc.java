package net.splitcells.dem.utils.random;

public interface BasicRndSrc {

    default int integer() {
        return integer(Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    int integer(final Integer min, final Integer max);

}
