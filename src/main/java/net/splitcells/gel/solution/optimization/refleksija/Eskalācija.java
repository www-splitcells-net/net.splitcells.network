package net.splitcells.gel.solution.optimization.refleksija;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.solution.SolutionView;
import net.splitcells.gel.solution.optimization.Optimizācija;
import net.splitcells.gel.solution.optimization.OptimizācijasNotikums;

import java.util.function.Function;

import static net.splitcells.dem.data.set.list.Lists.list;

public class Eskalācija implements Optimizācija {

    public static Eskalācija eskalācija(Function<Integer, Optimizācija> optimzācijas) {
        return new Eskalācija(optimzācijas);
    }

    private final Function<Integer, Optimizācija> optimzācijas;
    private int eskalācijasLīmens = 0;

    private Eskalācija
            (Function<Integer, Optimizācija> optimzācijas) {
        this.optimzācijas = optimzācijas;
    }

    @Override
    public List<OptimizācijasNotikums> optimizē(SolutionView atrisinājums) {
        final var saknesNovērtejums = atrisinājums.ierobežojums().novērtējums();
        final var sanknesVēsturesIndekss = atrisinājums.vēsture().momentansIndekss();
        if (eskalācijasLīmens < 0) {
            return list();
        }
        final var optimizācija = optimzācijas.apply(eskalācijasLīmens).optimizē(atrisinājums);
        final var momentansNovērtējums = atrisinājums.novērtējums(optimizācija);
        if (momentansNovērtējums.labākNekā(saknesNovērtejums)) {
            eskalācijasLīmens += 1;
        } else {
            eskalācijasLīmens -= 1;
        }
        return optimizācija;
    }
}