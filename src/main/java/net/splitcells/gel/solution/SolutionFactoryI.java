package net.splitcells.gel.solution;

import net.splitcells.gel.problem.Problem;

public class SolutionFactoryI extends SolutionFactory {
    @Override
    public Solution atrisinājum(Problem problēma) {
        return joinAspects(SolutionI.atrisinājums(problēma));
    }

    @Override
    public void close() {

    }

    @Override
    public void flush() {

    }
}
