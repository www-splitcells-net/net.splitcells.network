package net.splitcells.gel.solution.history;

import net.splitcells.dem.environment.resource.ResourceI;
import net.splitcells.gel.solution.Solution;

import static net.splitcells.dem.Dem.environment;

public class Histories extends ResourceI<HistoryFactory> {
    public Histories() {
        super(() -> new HistoryIFactory());
    }

    public static History vēsture(Solution solution) {
        return environment().config().configValue(Histories.class).vēsture(solution);
    }
}
