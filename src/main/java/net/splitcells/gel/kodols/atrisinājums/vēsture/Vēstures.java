package net.splitcells.gel.kodols.atrisinājums.vēsture;

import net.splitcells.gel.kodols.atrisinājums.Atrisinājums;

import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;

public class Vēstures {
    private Vēstures() {
        throw constructorIllegal();
    }

    public static Vēsture vēsture(Atrisinājums atrisinājums) {
        return VēstureVeidotajs.gadījums().vēsture(atrisinājums);
    }
}
