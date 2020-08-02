package net.splitcells.gel.kodols.dati.piešķiršanas;

import net.splitcells.gel.kodols.dati.datubāze.DatuBāze;

public class PiešķiršanasIVeidotajs implements PiešķiršanasVeidotajs {
    @Override
    public void close() {

    }

    @Override
    public void flush() {

    }

    @Override
    public Piešķiršanas piešķiršanas(String vārds, DatuBāze prāsibas, DatuBāze piedāvājumi) {
        return new PiešķiršanasI(vārds, prāsibas, piedāvājumi);
    }
}
