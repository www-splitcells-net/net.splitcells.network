package net.splitcells.gel.solution.optimization.primitive;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.solution.optimization.OptimizationEvent.optimizationEvent;
import static net.splitcells.gel.solution.optimization.StepType.ADDITION;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.solution.SolutionView;
import net.splitcells.gel.solution.optimization.Optimization;
import net.splitcells.gel.solution.optimization.OptimizationEvent;

public class LinearInitialization implements Optimization {

    public static LinearInitialization linearInitialization() {
        return new LinearInitialization();
    }

    private LinearInitialization() {

    }

    @Override
    public List<OptimizationEvent> optimize(SolutionView solution) {
        if (solution.demands_unused().hasContent() && solution.supplies_free().hasContent()) {
            return list(
                    optimizationEvent
                            (ADDITION
                                    , solution.demands_unused().getLines().get(0).toLinePointer()
                                    , solution.supplies_free().getLines().get(0).toLinePointer()));

        }
        return list();
    }

}
