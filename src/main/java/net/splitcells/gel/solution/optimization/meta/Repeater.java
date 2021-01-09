package net.splitcells.gel.solution.optimization.meta;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.solution.SolutionView;
import net.splitcells.gel.solution.optimization.Optimization;
import net.splitcells.gel.solution.optimization.OptimizationEvent;

import static net.splitcells.dem.utils.Not_implemented_yet.not_implemented_yet;
import static net.splitcells.dem.data.set.list.Lists.list;

public class Repeater implements Optimization {
    public static Repeater atkārtotājs(Optimization optimization, int maksimalsAtkārtošanasSkaitlis) {
        return new Repeater(optimization, maksimalsAtkārtošanasSkaitlis);
    }

    private final Optimization optimization;
    private final int maksimalsAtkārtošanasSkaitlis;
    private int atkārtošanasSkaitlis = 0;

    private Repeater(Optimization optimization, int maksimalsAtkārtošanasSkaitlis) {
        this.optimization = optimization;
        this.maksimalsAtkārtošanasSkaitlis = maksimalsAtkārtošanasSkaitlis;
    }

    @Override
    public List<OptimizationEvent> optimizē(SolutionView atrisinājums) {
        if (atkārtošanasSkaitlis >= maksimalsAtkārtošanasSkaitlis) {
            return list();
        }
        atkārtošanasSkaitlis += 1;
        return optimizē(atrisinājums);
    }
}
