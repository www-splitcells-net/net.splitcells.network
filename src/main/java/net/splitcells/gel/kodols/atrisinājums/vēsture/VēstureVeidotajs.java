package net.splitcells.gel.kodols.atrisinājums.vēsture;

import net.splitcells.dem.resource.AspectOrientedConstructor;
import net.splitcells.gel.kodols.atrisinājums.Atrisinājums;

public class VēstureVeidotajs extends AspectOrientedConstructor<Vēsture> {
    public static final VēstureVeidotajs GADĪJUMS = new VēstureVeidotajs();
    public static VēstureVeidotajs gadījums() {
        return GADĪJUMS;
    }
    public Vēsture vēsture(Atrisinājums atrisinājums) {
        return this.joinAspects(new VēstureI(atrisinājums));
    }
}
