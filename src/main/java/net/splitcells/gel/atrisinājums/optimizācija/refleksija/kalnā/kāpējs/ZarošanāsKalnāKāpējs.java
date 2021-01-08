package net.splitcells.gel.atrisinājums.optimizācija.refleksija.kalnā.kāpējs;

import java.util.Optional;
import java.util.function.Supplier;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.atrisinājums.AtrisinājumaSkats;
import net.splitcells.gel.atrisinājums.Atrisinājums;
import net.splitcells.gel.atrisinājums.vēsture.Vēsture;
import net.splitcells.gel.atrisinājums.vēsture.refleksija.tips.PilnsNovērtejums;
import net.splitcells.gel.atrisinājums.optimizācija.Optimizācija;
import net.splitcells.gel.atrisinājums.optimizācija.OptimizācijasNotikums;

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
    public List<OptimizācijasNotikums> optimizē(AtrisinājumaSkats atrisinājums) {
        final var nakamaisZars = nakamaisZars(atrisinājums);
        return nakamaOperācija(nakamaisZars.get());
    }

    private List<OptimizācijasNotikums> nakamaOperācija(Atrisinājums zars) {
        throw not_implemented_yet();
    }

    private Optional<Atrisinājums> nakamaisZars(AtrisinājumaSkats atrisinājums) {
        final var saknesNovērtejums = atrisinājums.ierobežojums().novērtējums();
        var labakaisKaimiņs = Optional.<Atrisinājums>empty();
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
