package net.splitcells.gel.solution.optimization.primitive;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.utils.random.Randomness;
import net.splitcells.gel.solution.SolutionView;
import net.splitcells.gel.solution.optimization.Optimization;
import net.splitcells.gel.solution.optimization.OptimizationEvent;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.utils.random.RandomnessSource.randomness;
import static net.splitcells.gel.solution.optimization.OptimizationEvent.optimizacijasNotikums;
import static net.splitcells.gel.solution.optimization.StepType.ADDITION;

public class RandomInitialization implements Optimization {
    public static RandomInitialization nejaušsInicialiyētājs() {
        return new RandomInitialization(randomness());
    }

    private final Randomness nejaušiba;

    private RandomInitialization(Randomness nejaušiba) {
        this.nejaušiba = nejaušiba;
    }

    @Override
    public List<OptimizationEvent> optimize(SolutionView atrisinājums) {
        if (atrisinājums.demands_unused().hasContent() && atrisinājums.supplies_unused().hasContent()) {
            return list(
                    optimizacijasNotikums
                            (ADDITION
                                    , nejaušiba.chooseOneOf(atrisinājums.demands_unused().getLines()).uzRindaRādītājs()
                                    , nejaušiba.chooseOneOf(atrisinājums.supplies_unused().getLines()).uzRindaRādītājs()));

        }
        return list();
    }
}
