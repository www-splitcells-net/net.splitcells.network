package net.splitcells.gel.solution;

import net.splitcells.gel.problem.Problem;

public class SolutionFactoryI extends SolutionFactory {
    @Override
    public Solution solution(Problem problēma) {
        return joinAspects(SolutionI.solution(problēma));
    }

    @Override
    public void close() {

    }

    @Override
    public void flush() {

    }
}
