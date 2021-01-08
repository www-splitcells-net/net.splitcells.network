package net.splitcells.gel.atrisinājums.optimizācija.primitīvs;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.utils.random.Randomness;
import net.splitcells.gel.atrisinājums.AtrisinājumaSkats;
import net.splitcells.gel.atrisinājums.optimizācija.Optimizācija;
import net.splitcells.gel.atrisinājums.optimizācija.OptimizācijasNotikums;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.utils.random.RandomnessSource.randomness;
import static net.splitcells.gel.atrisinājums.optimizācija.OptimizācijasNotikums.optimizacijasNotikums;
import static net.splitcells.gel.atrisinājums.optimizācija.SoluTips.PIEŠĶIRŠANA;

public class NejaušsInicializētājs implements Optimizācija {
    public static NejaušsInicializētājs nejaušsInicialiyētājs() {
        return new NejaušsInicializētājs(randomness());
    }

    private final Randomness nejaušiba;

    private NejaušsInicializētājs(Randomness nejaušiba) {
        this.nejaušiba = nejaušiba;
    }

    @Override
    public List<OptimizācijasNotikums> optimizē(AtrisinājumaSkats atrisinājums) {
        if (atrisinājums.prasības_nelietotas().navTukšs() && atrisinājums.piedāvājums_nelietots().navTukšs()) {
            return list(
                    optimizacijasNotikums
                            (PIEŠĶIRŠANA
                                    , nejaušiba.chooseOneOf(atrisinājums.prasības_nelietotas().gūtRindas()).uzRindaRādītājs()
                                    , nejaušiba.chooseOneOf(atrisinājums.piedāvājums_nelietots().gūtRindas()).uzRindaRādītājs()));

        }
        return list();
    }
}
