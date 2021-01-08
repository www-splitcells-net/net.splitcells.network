package net.splitcells.gel.solution.history;

import net.splitcells.dem.resource.communication.Closeable;
import net.splitcells.dem.resource.communication.Flushable;
import net.splitcells.gel.solution.Atrisinājums;

public interface VēsturesVeidotajs extends Closeable, Flushable {

    Vēsture vēsture(Atrisinājums atrisinājums);
}
