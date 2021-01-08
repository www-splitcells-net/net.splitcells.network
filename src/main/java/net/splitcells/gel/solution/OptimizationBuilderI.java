package net.splitcells.gel.solution;

import net.splitcells.gel.problem.Problēma;

public class OptimizationBuilderI extends OptimizationBuilder {
    @Override
    public Atrisinājums atrisinājum(Problēma problēma) {
        return joinAspects(OptimizationI.atrisinājums(problēma));
    }

    @Override
    public void close() {

    }

    @Override
    public void flush() {

    }
}
