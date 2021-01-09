package net.splitcells.gel.solution;

import net.splitcells.dem.environment.resource.ResourceI;
import net.splitcells.gel.problem.Problem;

import static net.splitcells.dem.Dem.environment;

public class Solutions extends ResourceI<SolutionFactory> {
    public Solutions() {
        super(() -> new SolutionFactoryI());
    }

    public static Solution atrisinājum(Problem problēma) {
        return environment().config().configValue(Solutions.class).atrisinājum(problēma);
    }
}
