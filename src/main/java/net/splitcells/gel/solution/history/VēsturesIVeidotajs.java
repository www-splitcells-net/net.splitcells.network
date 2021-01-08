package net.splitcells.gel.solution.history;

import net.splitcells.gel.solution.Solution;

public class VēsturesIVeidotajs implements VēsturesVeidotajs {

    @Override
    public Vēsture vēsture(Solution solution) {
        return new VēstureI(solution);
    }

    @Override
    public void close() {

    }

    @Override
    public void flush() {

    }
}
