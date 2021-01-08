package net.splitcells.gel.data.piešķiršanas;

import net.splitcells.dem.environment.resource.ResourceI;
import net.splitcells.gel.data.datubāze.DatuBāze;

import static net.splitcells.dem.Dem.environment;

public class Piešķiršanass extends ResourceI<PiešķiršanasVeidotajs> {
    public Piešķiršanass() {
        super(() -> new PiešķiršanasIVeidotajs());
    }

    public static Piešķiršanas piešķiršanas(String vārds, DatuBāze prāsibas, DatuBāze piedāvājumi) {
        return environment().config().configValue(Piešķiršanass.class).piešķiršanas(vārds, prāsibas, piedāvājumi);
    }
}
