package net.splitcells.gel.kodols.atrisinājums.optimizācija.refleksija;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.kodols.atrisinājums.AtrisinājumaSkats;
import net.splitcells.gel.kodols.atrisinājums.Atrisinājums;
import net.splitcells.gel.kodols.atrisinājums.optimizācija.Optimizācija;
import net.splitcells.gel.kodols.atrisinājums.optimizācija.OptimizācijaTiešsaistē;
import net.splitcells.gel.kodols.atrisinājums.optimizācija.OptimizācijasNotikums;

import java.util.function.Function;

import static net.splitcells.dem.data.set.list.Lists.list;

public class Eskalācija implements OptimizācijaTiešsaistē {

    public static Eskalācija eskalācija(Function<Integer, Optimizācija> optimzācijas) {
        return new Eskalācija(optimzācijas);
    }

    private final Function<Integer, Optimizācija> optimzācijas;
    private int eskalācijasLīmens = 0;

    private Eskalācija(Function<Integer, Optimizācija> optimzācijas) {
        this.optimzācijas = optimzācijas;
    }

    @Override
    public List<OptimizācijasNotikums> optimizē(Atrisinājums atrisinājums) {
        final var saknesNovērtejums = atrisinājums.ierobežojums().novērtējums();
        final var sanknesVēsturesIndekss = atrisinājums.vēsture().momentansIndekss();
        if (eskalācijasLīmens < 0) {
            return list();
        }
        final var optimizācija = optimzācijas.apply(eskalācijasLīmens).optimizē(atrisinājums);
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