package net.splitcells.gel.data.allocations;

import net.splitcells.gel.data.allocation.Allocations;
import net.splitcells.gel.data.allocation.AllocationsFactory;
import net.splitcells.gel.data.database.Database;

public class AllocationsIRefFactory implements AllocationsFactory {
    @Override
    public Allocations allocations(String name, Database demand, Database supply) {
        return new AllocationsIRef(name, demand, supply);    }

    @Override
    public void close() {

    }

    @Override
    public void flush() {

    }
}
