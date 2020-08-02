package net.splitcells.gel.kodols.dati.piešķiršanas;

import net.splitcells.dem.environment.resource.ResourceI;
import net.splitcells.gel.kodols.dati.datubāze.DatuBāze;
import net.splitcells.gel.kodols.dati.datubāze.DatuBāzes;

import java.util.function.Supplier;

import static net.splitcells.dem.Dem.environment;

public class Piešķiršanass extends ResourceI<PiešķiršanasVeidotajs> {
    public Piešķiršanass() {
        super(() -> new PiešķiršanasIVeidotajs());
    }

    public static Piešķiršanas piešķiršanas(String vārds, DatuBāze prāsibas, DatuBāze piedāvājumi) {
        return environment().config().configValue(Piešķiršanass.class).piešķiršanas(vārds, prāsibas, piedāvājumi);
    }
}
