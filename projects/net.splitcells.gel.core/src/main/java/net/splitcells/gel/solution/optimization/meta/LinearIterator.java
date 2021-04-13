package net.splitcells.gel.solution.optimization.meta;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.gel.solution.SolutionView;
import net.splitcells.gel.solution.optimization.Optimization;
import net.splitcells.gel.solution.optimization.OptimizationEvent;

import java.util.Arrays;

import static net.splitcells.dem.data.set.list.Lists.list;
import static org.assertj.core.api.Assertions.assertThat;

public class LinearIterator implements Optimization {

    private final List<Optimization> optimizations;
    private int counter = -1;

    public static LinearIterator linearIterator(Optimization... optimization) {
        return new LinearIterator(list(optimization));
    }

    public static LinearIterator linearIterator(List<Optimization> optimization) {
        return new LinearIterator(optimization);
    }

    private LinearIterator(List<Optimization> optimizations) {
        this.optimizations = optimizations;
    }

    @Override
    public List<OptimizationEvent> optimize(SolutionView solution) {
        List<OptimizationEvent> optimization = list();
        int tryCounter = 0;
        while (optimization.isEmpty() && tryCounter < this.optimizations.size()) {
            optimization = selectNextOptimization().optimize(solution);
            ++tryCounter;
        }
        return optimization;
    }

    private Optimization selectNextOptimization() {
        counter += 1;
        if (counter >= optimizations.size()) {
            counter = 0;
        }
        return optimizations.get(counter);
    }
}
