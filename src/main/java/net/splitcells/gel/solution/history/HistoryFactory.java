package net.splitcells.gel.solution.history;

import net.splitcells.dem.resource.communication.Closeable;
import net.splitcells.dem.resource.communication.Flushable;
import net.splitcells.gel.solution.Solution;

public interface HistoryFactory extends Closeable, Flushable {

    History vēsture(Solution solution);
}
