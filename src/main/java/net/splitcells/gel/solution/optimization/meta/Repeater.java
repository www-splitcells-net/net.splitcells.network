package net.splitcells.gel.solution.optimization.meta;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.solution.SolutionView;
import net.splitcells.gel.solution.optimization.Optimization;
import net.splitcells.gel.solution.optimization.OptimizationEvent;

import static net.splitcells.dem.utils.Not_implemented_yet.not_implemented_yet;
import static net.splitcells.dem.data.set.list.Lists.list;

public class Repeater implements Optimization {
    public static Repeater repeater(Optimization optimization, int maximalRepetitionCount) {
        return new Repeater(optimization, maximalRepetitionCount);
    }

    private final Optimization optimization;
    private final int maximalRepetitionCount;
    private int repetitionCount = 0;

    private Repeater(Optimization optimization, int maximalRepetitionCount) {
        this.optimization = optimization;
        this.maximalRepetitionCount = maximalRepetitionCount;
    }

    @Override
    public List<OptimizationEvent> optimize(SolutionView solution) {
        if (repetitionCount >= maximalRepetitionCount) {
            return list();
        }
        repetitionCount += 1;
        return optimize(solution);
    }
}
