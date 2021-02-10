package net.splitcells.gel.solution.optimization.primitive;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.utils.random.Randomness;
import net.splitcells.gel.solution.SolutionView;
import net.splitcells.gel.solution.optimization.Optimization;
import net.splitcells.gel.solution.optimization.OptimizationEvent;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.utils.random.RandomnessSource.randomness;
import static net.splitcells.gel.solution.optimization.OptimizationEvent.optimizationEvent;
import static net.splitcells.gel.solution.optimization.StepType.ADDITION;

public class RandomInitialization implements Optimization {
    public static RandomInitialization randomInitialization() {
        return new RandomInitialization(randomness());
    }

    private final Randomness randomness;

    private RandomInitialization(Randomness nejaušiba) {
        this.randomness = nejaušiba;
    }

    @Override
    public List<OptimizationEvent> optimize(SolutionView solution) {
        if (solution.demands_unused().hasContent() && solution.supplies_free().hasContent()) {
            return list(
                    optimizationEvent
                            (ADDITION
                                    , randomness.chooseOneOf(solution.demands_unused().getLines()).toLinePointer()
                                    , randomness.chooseOneOf(solution.supplies_free().getLines()).toLinePointer()));

        }
        return list();
    }
}
