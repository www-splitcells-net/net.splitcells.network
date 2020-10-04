package net.splitcells.gel.kodols.atrisinājums.optimizācija.refleksija;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.kodols.atrisinājums.AtrisinājumaSkats;
import net.splitcells.gel.kodols.atrisinājums.Atrisinājums;
import net.splitcells.gel.kodols.atrisinājums.optimizācija.Optimizācija;
import net.splitcells.gel.kodols.atrisinājums.optimizācija.OptimizācijaTiešsaistē;
import net.splitcells.gel.kodols.atrisinājums.optimizācija.OptimizācijasNotikums;

import java.util.Optional;
import java.util.function.Function;

import static net.splitcells.dem.data.set.list.Lists.list;

public class Eskalācija implements OptimizācijaTiešsaistē {

    public static Eskalācija eskalācija(Function<Integer, Optimizācija> optimzācijas) {
        return new Eskalācija(Optional.of(optimzācijas), Optional.empty());
    }

    public static Eskalācija eskalācija2(Function<Integer, OptimizācijaTiešsaistē> optimzācijas) {
        return new Eskalācija(Optional.empty(), Optional.of(optimzācijas));
    }

    private final Optional<Function<Integer, Optimizācija>> optimzācijas;
    private final Optional<Function<Integer, OptimizācijaTiešsaistē>> optimzācijas2;
    private int eskalācijasLīmens = 0;

    private Eskalācija
            (Optional<Function<Integer, Optimizācija>> optimzācijas
                    , Optional<Function<Integer, OptimizācijaTiešsaistē>> optimzācijas2) {
        this.optimzācijas = optimzācijas;
        this.optimzācijas2 = optimzācijas2;
    }

    @Override
    public List<OptimizācijasNotikums> optimizē(Atrisinājums atrisinājums) {
        final var saknesNovērtejums = atrisinājums.ierobežojums().novērtējums();
        final var sanknesVēsturesIndekss = atrisinājums.vēsture().momentansIndekss();
        if (eskalācijasLīmens < 0) {
            return list();
        }
        final List<OptimizācijasNotikums> optimizācija;
        if (optimzācijas.isPresent()) {
            optimizācija = optimzācijas.get().apply(eskalācijasLīmens).optimizē(atrisinājums);
        } else if (optimzācijas2.isPresent()) {
            optimizācija = optimzācijas2.get().apply(eskalācijasLīmens).optimizē(atrisinājums);
        } else {
            throw new IllegalStateException();
        }
        atrisinājums.optimizē(optimizācija);
        final var momentansNovērtējums = atrisinājums.ierobežojums().novērtējums();
        atrisinājums.vēsture().atiestatUz(sanknesVēsturesIndekss);
        if (momentansNovērtējums.labākNekā(saknesNovērtejums)) {
            eskalācijasLīmens += 1;
        } else {
            eskalācijasLīmens -= 1;
        }
        return optimizācija;
    }
}