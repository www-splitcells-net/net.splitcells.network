package net.splitcells.gel.solution.history;

import net.splitcells.gel.solution.Atrisinājums;

public class VēsturesIVeidotajs implements VēsturesVeidotajs {

    @Override
    public Vēsture vēsture(Atrisinājums atrisinājums) {
        return new VēstureI(atrisinājums);
    }

    @Override
    public void close() {

    }

    @Override
    public void flush() {

    }
}
