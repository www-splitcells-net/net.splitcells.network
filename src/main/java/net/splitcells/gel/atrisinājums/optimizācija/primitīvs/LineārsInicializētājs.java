package net.splitcells.gel.atrisinājums.optimizācija.primitīvs;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.atrisinājums.optimizācija.OptimizācijasNotikums.optimizacijasNotikums;
import static net.splitcells.gel.atrisinājums.optimizācija.SoluTips.PIEŠĶIRŠANA;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.atrisinājums.AtrisinājumaSkats;
import net.splitcells.gel.atrisinājums.optimizācija.Optimizācija;
import net.splitcells.gel.atrisinājums.optimizācija.OptimizācijasNotikums;

public class LineārsInicializētājs implements Optimizācija {

    public static LineārsInicializētājs lineārsInicializētājs() {
        return new LineārsInicializētājs();
    }

    private LineārsInicializētājs() {

    }

    @Override
    public List<OptimizācijasNotikums> optimizē(AtrisinājumaSkats solution) {
        if (solution.prasības_nelietotas().navTukšs() && solution.piedāvājums_nelietots().navTukšs()) {
            return list(
                    optimizacijasNotikums
                            (PIEŠĶIRŠANA
                                    , solution.prasības_nelietotas().gūtRindas().get(0).uzRindaRādītājs()
                                    , solution.piedāvājums_nelietots().gūtRindas().get(0).uzRindaRādītājs()));

        }
        return list();
    }

}
