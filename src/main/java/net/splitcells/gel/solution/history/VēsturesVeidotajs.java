package net.splitcells.gel.solution.history;

import net.splitcells.dem.resource.communication.Closeable;
import net.splitcells.dem.resource.communication.Flushable;
import net.splitcells.gel.solution.Optimization;

public interface VēsturesVeidotajs extends Closeable, Flushable {

    Vēsture vēsture(Optimization optimization);
}
