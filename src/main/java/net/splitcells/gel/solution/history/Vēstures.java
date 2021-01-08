package net.splitcells.gel.solution.history;

import net.splitcells.dem.environment.resource.ResourceI;
import net.splitcells.gel.solution.Optimization;

import static net.splitcells.dem.Dem.environment;

public class Vēstures extends ResourceI<VēsturesVeidotajs> {
    public Vēstures() {
        super(() -> new VēsturesIVeidotajs());
    }

    public static Vēsture vēsture(Optimization optimization) {
        return environment().config().configValue(Vēstures.class).vēsture(optimization);
    }
}
