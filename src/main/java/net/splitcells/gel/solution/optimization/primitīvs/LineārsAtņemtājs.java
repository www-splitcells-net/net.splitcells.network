package net.splitcells.gel.solution.optimization.primitīvs;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.solution.SolutionView;
import net.splitcells.gel.solution.optimization.Optimizācija;
import net.splitcells.gel.solution.optimization.OptimizācijasNotikums;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.solution.optimization.OptimizācijasNotikums.optimizacijasNotikums;
import static net.splitcells.gel.solution.optimization.SoluTips.NOŅEMŠANA;

public class LineārsAtņemtājs implements Optimizācija {

    public static LineārsAtņemtājs linearDeallocator() {
        return new LineārsAtņemtājs();
    }

    private LineārsAtņemtājs() {

    }

    @Override
    public List<OptimizācijasNotikums> optimizē(SolutionView solution) {
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
