package net.splitcells.gel.data.allocation;


import net.splitcells.dem.resource.communication.Closeable;
import net.splitcells.dem.resource.communication.Flushable;
import net.splitcells.gel.data.database.Database;

public interface AllocationsFactory extends Closeable, Flushable {
    Allocations allocations(String name, Database demands, Database supply);
}
