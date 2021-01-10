package net.splitcells.gel.solution.optimization.meta;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.solution.SolutionView;
import net.splitcells.gel.solution.optimization.Optimization;
import net.splitcells.gel.solution.optimization.OptimizationEvent;

import static net.splitcells.dem.data.set.list.Lists.list;
import static org.assertj.core.api.Assertions.assertThat;

public class LinearIterator implements Optimization {

    private final List<Optimization> optimizations;
    private int skaitītājs = -1;

    public LinearIterator(List<Optimization> optimizations) {
        this.optimizations = optimizations;
    }

    @Override
    public List<OptimizationEvent> optimize(SolutionView atrisinājums) {
        List<OptimizationEvent> optimizācijas = list();
        int mēģinājums = 0;
        while (optimizācijas.isEmpty() && mēģinājums < this.optimizations.size()) {
            optimizācijas = atlasitNakamoOptimicājiu().optimize(atrisinājums);
            ++mēģinājums;
        }
        return optimizācijas;
    }

    private Optimization atlasitNakamoOptimicājiu() {
        skaitītājs += 1;
        if (skaitītājs >= optimizations.size()) {
            skaitītājs = 0;
        }
        return optimizations.get(skaitītājs);
    }
}
