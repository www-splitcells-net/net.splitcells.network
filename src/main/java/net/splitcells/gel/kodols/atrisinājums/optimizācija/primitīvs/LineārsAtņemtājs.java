package net.splitcells.gel.kodols.atrisinājums.optimizācija.primitīvs;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.kodols.atrisinājums.AtrisinājumaSkats;
import net.splitcells.gel.kodols.atrisinājums.optimizācija.Optimizācija;
import net.splitcells.gel.kodols.atrisinājums.optimizācija.OptimizācijasNotikums;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.kodols.atrisinājums.optimizācija.OptimizācijasNotikums.optimizacijasNotikums;
import static net.splitcells.gel.kodols.atrisinājums.optimizācija.SoluTips.NOŅEMŠANA;

public class LineārsAtņemtājs implements Optimizācija {

    public static LineārsAtņemtājs linearDeallocator() {
        return new LineārsAtņemtājs();
    }

    private LineārsAtņemtājs() {

    }

    @Override
    public List<OptimizācijasNotikums> optimizē(AtrisinājumaSkats solution) {
        if (solution.prasība_lietots().navTukšs() && solution.piedāvājumi_lietoti().navTukšs()) {
            return
                    list(
                            optimizacijasNotikums
                                    (NOŅEMŠANA
                                            , solution.prasība_lietots().gūtRindas().get(0).uzRindaRādītājs()
                                            , solution.piedāvājumi_lietoti().gūtRindas().get(0).uzRindaRādītājs()));

        }
        return list();
    }
}
