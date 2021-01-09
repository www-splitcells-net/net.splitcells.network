package net.splitcells.gel.solution.optimization.primitive;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.solution.SolutionView;
import net.splitcells.gel.solution.optimization.Optimization;
import net.splitcells.gel.solution.optimization.OptimizationEvent;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.solution.optimization.OptimizationEvent.optimizacijasNotikums;
import static net.splitcells.gel.solution.optimization.StepType.NOŅEMŠANA;

public class LinearDeinitializer implements Optimization {

    public static LinearDeinitializer linearDeallocator() {
        return new LinearDeinitializer();
    }

    private LinearDeinitializer() {

    }

    @Override
    public List<OptimizationEvent> optimizē(SolutionView solution) {
        if (solution.demands_used().hasContent() && solution.supplies_used().hasContent()) {
            return
                    list(
                            optimizacijasNotikums
                                    (NOŅEMŠANA
                                            , solution.demands_used().getLines().get(0).uzRindaRādītājs()
                                            , solution.supplies_used().getLines().get(0).uzRindaRādītājs()));

        }
        return list();
    }
}
