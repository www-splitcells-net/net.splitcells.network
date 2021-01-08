package net.splitcells.gel.solution;

import net.splitcells.gel.problem.Problēma;

public class SolutionFactoryI extends SolutionFactory {
    @Override
    public Solution atrisinājum(Problēma problēma) {
        return joinAspects(SolutionI.atrisinājums(problēma));
    }

    @Override
    public void close() {

    }

    @Override
    public void flush() {

    }
}
