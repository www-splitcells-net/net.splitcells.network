package net.splitcells.gel.data.allocation;

import net.splitcells.dem.environment.resource.ResourceI;
import net.splitcells.gel.data.database.Database;

import static net.splitcells.dem.Dem.environment;

public class Allocationss extends ResourceI<AllocationsFactory> {
    public Allocationss() {
        super(() -> new AllocationsIFactory());
    }

    public static Allocations piešķiršanas(String vārds, Database prāsibas, Database piedāvājumi) {
        return environment().config().configValue(Allocationss.class).piešķiršanas(vārds, prāsibas, piedāvājumi);
    }
}
