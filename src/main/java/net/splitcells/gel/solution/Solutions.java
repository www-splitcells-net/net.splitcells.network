package net.splitcells.gel.solution;

import net.splitcells.dem.environment.resource.ResourceI;
import net.splitcells.gel.problem.Problēma;

import static net.splitcells.dem.Dem.environment;

public class Solutions extends ResourceI<OptimizationBuilder> {
    public Solutions() {
        super(() -> new OptimizationBuilderI());
    }

    public static Solution atrisinājum(Problēma problēma) {
        return environment().config().configValue(Solutions.class).atrisinājum(problēma);
    }
}
