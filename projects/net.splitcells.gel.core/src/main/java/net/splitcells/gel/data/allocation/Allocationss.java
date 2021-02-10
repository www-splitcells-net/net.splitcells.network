package net.splitcells.gel.data.allocation;

import net.splitcells.dem.environment.resource.ResourceI;
import net.splitcells.gel.data.database.Database;

import static net.splitcells.dem.Dem.environment;

public class Allocationss extends ResourceI<AllocationsFactory> {
    public Allocationss() {
        super(() -> new AllocationsIFactory());
    }

    public static Allocations allocations(String name, Database demands, Database supplies) {
        return environment().config().configValue(Allocationss.class).allocations(name, demands, supplies);
    }
}
