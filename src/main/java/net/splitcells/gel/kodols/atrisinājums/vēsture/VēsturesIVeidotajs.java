package net.splitcells.gel.kodols.atrisinājums.vēsture;

import net.splitcells.gel.kodols.atrisinājums.Atrisinājums;

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
