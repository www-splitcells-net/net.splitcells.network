package net.splitcells.gel.kodols.atrisinājums.optimizācija.agregāts;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.resource.host.interaction.LogLevel;
import net.splitcells.gel.kodols.atrisinājums.AtrisinājumaSkats;
import net.splitcells.gel.kodols.atrisinājums.optimizācija.Optimizācija;
import net.splitcells.gel.kodols.atrisinājums.optimizācija.OptimizācijasNotikums;
import net.splitcells.gel.kodols.novērtējums.struktūra.Novērtējums;

import java.util.Optional;
import java.util.function.Function;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.environment.config.StaticFlags.TRACING;
import static net.splitcells.dem.resource.host.interaction.Domsole.domsole;

public class Eskalācija implements Optimizācija {

    private final Function<Integer, Optimizācija> optimzācijas;

    public static Eskalācija eskalācija(Function<Integer, Optimizācija> optimzācijas) {
        return new Eskalācija(optimzācijas);
    }

    private Eskalācija(Function<Integer, Optimizācija> optimzācijas) {
        this.optimzācijas = optimzācijas;
    }

    @Override
    public List<OptimizācijasNotikums> optimizē(AtrisinājumaSkats atrisinājums) {
        final var saknesNovērtejums = atrisinājums.ierobežojums().novērtējums();
        final var sanknesVēsturesIndekss = atrisinājums.vēsture().momentansIndekss();
        return optimzācijas.apply(1).optimizē(atrisinājums);
    }
}