package net.splitcells.gel.solution.history;

import net.splitcells.gel.solution.Optimization;

public class VēsturesIVeidotajs implements VēsturesVeidotajs {

    @Override
    public Vēsture vēsture(Optimization optimization) {
        return new VēstureI(optimization);
    }

    @Override
    public void close() {

    }

    @Override
    public void flush() {

    }
}
