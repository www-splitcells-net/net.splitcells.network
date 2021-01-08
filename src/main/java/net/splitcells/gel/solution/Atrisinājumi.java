package net.splitcells.gel.solution;

import net.splitcells.dem.environment.resource.ResourceI;
import net.splitcells.gel.problem.Problēma;

import static net.splitcells.dem.Dem.environment;

public class Atrisinājumi extends ResourceI<OptimizationBuilder> {
    public Atrisinājumi() {
        super(() -> new OptimizationBuilderI());
    }

    public static Optimization atrisinājum(Problēma problēma) {
        return environment().config().configValue(Atrisinājumi.class).atrisinājum(problēma);
    }
}
