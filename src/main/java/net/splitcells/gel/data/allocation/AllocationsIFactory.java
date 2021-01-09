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
    public Allocations piešķiršanas(String vārds, Database prāsibas, Database piedāvājumi) {
        return new AllocationsI(vārds, prāsibas, piedāvājumi);
    }
}
