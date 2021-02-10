package net.splitcells.gel.data.allocation;

import net.splitcells.gel.data.database.Database;

public class AllocationsIFactory implements AllocationsFactory {
    @Override
    public void close() {

    }

    @Override
    public void flush() {

    }

    @Override
    public Allocations allocations(String name, Database demands, Database supplies) {
        return new AllocationsI(name, demands, supplies);
    }
}
