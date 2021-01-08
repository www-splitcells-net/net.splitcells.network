package net.splitcells.gel.solution.optimization.refleksija.kalnā.kāpējs;

import java.util.Optional;
import java.util.function.Supplier;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.solution.SolutionView;
import net.splitcells.gel.solution.Solution;
import net.splitcells.gel.solution.history.Vēsture;
import net.splitcells.gel.solution.history.refleksija.tips.PilnsNovērtejums;
import net.splitcells.gel.solution.optimization.Optimizācija;
import net.splitcells.gel.solution.optimization.OptimizācijasNotikums;

import static net.splitcells.dem.utils.Not_implemented_yet.not_implemented_yet;

@Deprecated
public class ZarošanāsKalnāKāpējs implements Optimizācija {

    public static ZarošanāsKalnāKāpējs zarošanāsKalnāKāpējs() {
        return new ZarošanāsKalnāKāpējs();
    }

    private final Supplier<Boolean> plānotajs = () -> true;

    private ZarošanāsKalnāKāpējs() {

    }

    @Override
    public List<OptimizācijasNotikums> optimizē(SolutionView atrisinājums) {
        final var nakamaisZars = nakamaisZars(atrisinājums);
        return nakamaOperācija(nakamaisZars.get());
    }

    private List<OptimizācijasNotikums> nakamaOperācija(Solution zars) {
        throw not_implemented_yet();
    }

    private Optional<Solution> nakamaisZars(SolutionView atrisinājums) {
        final var saknesNovērtejums = atrisinājums.ierobežojums().novērtējums();
        var labakaisKaimiņs = Optional.<Solution>empty();
        while (plānotajs.get()) {
            final var momentansKaimiņs = atrisinājums.zars();
            final var momentansNovērtejums = atrisinājums
                    .vēsture()
                    .gūtRindas()
                    .lastValue()
                    .get()
                    .vērtība(Vēsture.REFLEKSIJAS_DATI)
                    .vertība(PilnsNovērtejums.class)
                    .get()
                    .vertība();
            if (momentansNovērtejums.labākNekā(saknesNovērtejums)) {
                labakaisKaimiņs = Optional.of(momentansKaimiņs);
            }
        }
        return labakaisKaimiņs;
    }
}
