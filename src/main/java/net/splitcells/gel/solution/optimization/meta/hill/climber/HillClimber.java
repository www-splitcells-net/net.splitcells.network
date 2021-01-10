package net.splitcells.gel.solution.optimization.meta.hill.climber;

import net.splitcells.dem.resource.host.interaction.LogLevel;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.solution.SolutionView;
import net.splitcells.gel.rating.structure.Rating;
import net.splitcells.gel.solution.optimization.Optimization;
import net.splitcells.gel.solution.optimization.OptimizationEvent;

import java.util.Optional;
import java.util.function.Supplier;

import static net.splitcells.dem.resource.host.interaction.Domsole.domsole;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.environment.config.StaticFlags.TRACING;

public class HillClimber implements Optimization {

    public static HillClimber funkcionālsKalnāKāpējs(Optimization optimization, int i) {
        return new HillClimber(optimization, new Supplier<Boolean>() {
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
    private final Optimization atrisinājumuKaimiņi;

    private HillClimber(Optimization optimization, Supplier<Boolean> plānotājs) {
        this.plānotājs = plānotājs;
        this.atrisinājumuKaimiņi = optimization;
    }

    @Override
    public List<OptimizationEvent> optimize(SolutionView atrisinājums) {
        final var saknesNovērtejums = atrisinājums.constraint().rating();
        final var sanknesVēsturesIndekss = atrisinājums.history().currentIndex();
        Optional<Rating> labakaisKaimiņuNovērtējums = Optional.empty();
        List<OptimizationEvent> labakaKaimiņuOperācija = list();
        while (plānotājs.get()) {
            final var ieteikumi = atrisinājumuKaimiņi.optimize(atrisinājums);
            if (ieteikumi.isEmpty()) {
                continue;
            }
            if (TRACING) {
                ieteikumi.forEach
                        (suggestion -> domsole().append
                                (suggestion.toDom()
                                        , () -> atrisinājums.path().withAppended
                                                (Optimization.class.getSimpleName()
                                                        , getClass().getSimpleName())
                                        , LogLevel.TRACE)
                        );
            }
            final var momentansNovērtējums = atrisinājums.rating(ieteikumi);
            if (labakaisKaimiņuNovērtējums.isEmpty()
                    || momentansNovērtējums.labākNekā(labakaisKaimiņuNovērtējums.get())) {
                labakaisKaimiņuNovērtējums = Optional.of(momentansNovērtējums);
                labakaKaimiņuOperācija = ieteikumi;
            }
            atrisinājums.history().atiestatUz(sanknesVēsturesIndekss);
        }
        if (!labakaisKaimiņuNovērtējums.isEmpty() && labakaisKaimiņuNovērtējums.get().labākNekā(saknesNovērtejums)) {
            return labakaKaimiņuOperācija;
        }
        return list();
    }
}

