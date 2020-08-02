package net.splitcells.gel.kodols.atrisinājums.vēsture;

import net.splitcells.dem.resource.communication.Closeable;
import net.splitcells.dem.resource.communication.Flushable;
import net.splitcells.gel.kodols.atrisinājums.Atrisinājums;

public interface VēsturesVeidotajs extends Closeable, Flushable {

    Vēsture vēsture(Atrisinājums atrisinājums);
}
