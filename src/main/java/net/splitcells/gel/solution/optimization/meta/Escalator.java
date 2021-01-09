package net.splitcells.gel.solution.optimization.meta;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.solution.SolutionView;
import net.splitcells.gel.solution.optimization.Optimization;
import net.splitcells.gel.solution.optimization.OptimizationEvent;

import java.util.function.Function;

import static net.splitcells.dem.data.set.list.Lists.list;

public class Escalator implements Optimization {

    public static Escalator eskalācija(Function<Integer, Optimization> optimzācijas) {
        return new Escalator(optimzācijas);
    }

    private final Function<Integer, Optimization> optimzācijas;
    private int eskalācijasLīmens = 0;

    private Escalator
            (Function<Integer, Optimization> optimzācijas) {
        this.optimzācijas = optimzācijas;
    }

    @Override
    public List<OptimizationEvent> optimizē(SolutionView atrisinājums) {
        final var saknesNovērtejums = atrisinājums.constraint().rating();
        final var sanknesVēsturesIndekss = atrisinājums.history().momentansIndekss();
        if (eskalācijasLīmens < 0) {
            return list();
        }
        final var optimizācija = optimzācijas.apply(eskalācijasLīmens).optimizē(atrisinājums);
        final var momentansNovērtējums = atrisinājums.rating(optimizācija);
        if (momentansNovērtējums.labākNekā(saknesNovērtejums)) {
            eskalācijasLīmens += 1;
        } else {
            eskalācijasLīmens -= 1;
        }
        return optimizācija;
    }
}