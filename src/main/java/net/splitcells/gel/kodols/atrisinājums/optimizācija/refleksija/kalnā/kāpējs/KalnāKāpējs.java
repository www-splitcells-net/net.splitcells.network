package net.splitcells.gel.kodols.atrisinājums.optimizācija.refleksija.kalnā.kāpējs;

import net.splitcells.dem.resource.host.interaction.LogLevel;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.kodols.novērtējums.struktūra.Novērtējums;
import net.splitcells.gel.kodols.atrisinājums.Atrisinājums;
import net.splitcells.gel.kodols.atrisinājums.optimizācija.Optimizācija;
import net.splitcells.gel.kodols.atrisinājums.optimizācija.notikums.OptimizācijasNotikums;
import net.splitcells.gel.kodols.atrisinājums.optimizācija.OptimizācijaTiešsaistē;

import java.util.Optional;
import java.util.function.Supplier;

import static net.splitcells.dem.resource.host.interaction.Domsole.domsole;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.environment.config.StaticFlags.TRACING;

public class KalnāKāpējs implements OptimizācijaTiešsaistē {

    public static KalnāKāpējs funkcionālsKalnāKāpējs(Optimizācija optimizācija, int i) {
        return new KalnāKāpējs(optimizācija, new Supplier<Boolean>() {
            int counter = 0;

            @Override
            public Boolean get() {
                final var rVal = counter < i;
                counter += 1;
                return rVal;
            }
        });
    }

    private final Supplier<Boolean> plānotājs;
    private final Optimizācija atrisinājumuKaimiņi;

    private KalnāKāpējs(Optimizācija optimizācija, Supplier<Boolean> plānotājs) {
        this.plānotājs = plānotājs;
        this.atrisinājumuKaimiņi = optimizācija;
    }

    @Override
    public List<OptimizācijasNotikums> optimizē(Atrisinājums atrisinājums) {
        final var saknesNovērtejums = atrisinājums.ierobežojums().novērtējums();
        final var sanknesVēsturesIndekss = atrisinājums.vēsture().momentansIndekss();
        Optional<Novērtējums> labakaisKaimiņuNovērtējums = Optional.empty();
        List<OptimizācijasNotikums> labakaKaimiņuOperācija = list();
        while (plānotājs.get()) {
            final var ieteikumi = atrisinājumuKaimiņi.optimizē(atrisinājums);
            if (ieteikumi.isEmpty()) {
                continue;
            }
            if (TRACING) {
                ieteikumi.forEach
                        (suggestion -> domsole().append
                                (suggestion.toDom()
                                        , () -> atrisinājums.path().withAppended
                                                (Optimizācija.class.getSimpleName()
                                                        , getClass().getSimpleName())
                                        , LogLevel.TRACE)
                        );
            }
            atrisinājums.optimizē(ieteikumi);
            final var momentansNovērtējums = atrisinājums.ierobežojums().novērtējums();
            if (labakaisKaimiņuNovērtējums.isEmpty()
                    || momentansNovērtējums.labākNekā(labakaisKaimiņuNovērtējums.get())) {
                labakaisKaimiņuNovērtējums = Optional.of(momentansNovērtējums);
                labakaKaimiņuOperācija = ieteikumi;
            }
            atrisinājums.vēsture().atiestatUz(sanknesVēsturesIndekss);
        }
        if (!labakaisKaimiņuNovērtējums.isEmpty() && labakaisKaimiņuNovērtējums.get().labākNekā(saknesNovērtejums)) {
            return labakaKaimiņuOperācija;
        }
        return list();
    }
}

