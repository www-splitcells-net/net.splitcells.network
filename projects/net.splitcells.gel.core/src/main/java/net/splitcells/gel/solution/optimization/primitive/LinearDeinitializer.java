package net.splitcells.gel.solution.optimization.primitive;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.solution.SolutionView;
import net.splitcells.gel.solution.optimization.Optimization;
import net.splitcells.gel.solution.optimization.OptimizationEvent;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.solution.optimization.OptimizationEvent.optimizationEvent;
import static net.splitcells.gel.solution.optimization.StepType.REMOVAL;

public class LinearDeinitializer implements Optimization {

    public static LinearDeinitializer linearDeinitializer() {
        return new LinearDeinitializer();
    }

    private LinearDeinitializer() {

    }

    @Override
    public List<OptimizationEvent> optimize(SolutionView solution) {
        if (solution.demandsUsed().hasContent() && solution.suppliesUsed().hasContent()) {
            return
                    list(
                            optimizationEvent
                                    (REMOVAL
                                            , solution.demandsUsed().getLines().get(0).toLinePointer()
                                            , solution.suppliesUsed().getLines().get(0).toLinePointer()));
        }
        return list();
    }
}
