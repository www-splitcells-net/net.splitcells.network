package net.splitcells.gel.solution.optimization.primitive;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.solution.optimization.OptimizationEvent.optimizacijasNotikums;
import static net.splitcells.gel.solution.optimization.StepType.PIEŠĶIRŠANA;

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
    public List<OptimizationEvent> optimizē(SolutionView solution) {
        if (solution.prasības_nelietotas().navTukšs() && solution.piedāvājums_nelietots().navTukšs()) {
            return list(
                    optimizacijasNotikums
                            (PIEŠĶIRŠANA
                                    , solution.prasības_nelietotas().gūtRindas().get(0).uzRindaRādītājs()
                                    , solution.piedāvājums_nelietots().gūtRindas().get(0).uzRindaRādītājs()));

        }
        return list();
    }

}
