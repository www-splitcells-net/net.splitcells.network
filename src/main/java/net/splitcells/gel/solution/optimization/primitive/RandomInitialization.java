package net.splitcells.gel.solution.optimization.primitive;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.utils.random.Randomness;
import net.splitcells.gel.solution.SolutionView;
import net.splitcells.gel.solution.optimization.Optimization;
import net.splitcells.gel.solution.optimization.OptimizationEvent;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.utils.random.RandomnessSource.randomness;
import static net.splitcells.gel.solution.optimization.OptimizationEvent.optimizacijasNotikums;
import static net.splitcells.gel.solution.optimization.StepType.PIEŠĶIRŠANA;

public class RandomInitialization implements Optimization {
    public static RandomInitialization nejaušsInicialiyētājs() {
        return new RandomInitialization(randomness());
    }

    private final Randomness nejaušiba;

    private RandomInitialization(Randomness nejaušiba) {
        this.nejaušiba = nejaušiba;
    }

    @Override
    public List<OptimizationEvent> optimizē(SolutionView atrisinājums) {
        if (atrisinājums.prasības_nelietotas().navTukšs() && atrisinājums.piedāvājums_nelietots().navTukšs()) {
            return list(
                    optimizacijasNotikums
                            (PIEŠĶIRŠANA
                                    , nejaušiba.chooseOneOf(atrisinājums.prasības_nelietotas().gūtRindas()).uzRindaRādītājs()
                                    , nejaušiba.chooseOneOf(atrisinājums.piedāvājums_nelietots().gūtRindas()).uzRindaRādītājs()));

        }
        return list();
    }
}
