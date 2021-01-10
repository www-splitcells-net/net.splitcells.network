package net.splitcells.gel.solution.optimization.primitive;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.solution.optimization.OptimizationEvent.optimizacijasNotikums;
import static net.splitcells.gel.solution.optimization.StepType.ADDITION;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.solution.SolutionView;
import net.splitcells.gel.solution.optimization.Optimization;
import net.splitcells.gel.solution.optimization.OptimizationEvent;

public class LinearInitialization implements Optimization {

    public static LinearInitialization lineārsInicializētājs() {
        return new LinearInitialization();
    }

    private LinearInitialization() {

    }

    @Override
    public List<OptimizationEvent> optimize(SolutionView solution) {
        if (solution.demands_unused().hasContent() && solution.supplies_unused().hasContent()) {
            return list(
                    optimizacijasNotikums
                            (ADDITION
                                    , solution.demands_unused().getLines().get(0).uzRindaRādītājs()
                                    , solution.supplies_unused().getLines().get(0).uzRindaRādītājs()));

        }
        return list();
    }

}
