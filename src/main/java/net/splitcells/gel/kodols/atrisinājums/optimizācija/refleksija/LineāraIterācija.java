package net.splitcells.gel.kodols.atrisinājums.optimizācija.refleksija;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.kodols.atrisinājums.AtrisinājumaSkats;
import net.splitcells.gel.kodols.atrisinājums.optimizācija.Optimizācija;
import net.splitcells.gel.kodols.atrisinājums.optimizācija.OptimizācijasNotikums;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.environment.config.StaticFlags.ENFORCING_UNIT_CONSISTENCY;
import static net.splitcells.gel.kodols.atrisinājums.optimizācija.primitīvs.LineārsAtņemtājs.linearDeallocator;
import static net.splitcells.gel.kodols.atrisinājums.optimizācija.primitīvs.LineārsInicializētājs.lineārsInicializētājs;
import static org.assertj.core.api.Assertions.assertThat;

public class LineāraIterācija implements Optimizācija {

    private final List<Optimizācija> optimizācijas;
    private int skaitītājs = -1;

    public LineāraIterācija(List<Optimizācija> optimizācijas) {
        this.optimizācijas = optimizācijas;
    }

    @Override
    public List<OptimizācijasNotikums> optimizē(AtrisinājumaSkats atrisinājums) {
        List<OptimizācijasNotikums> optimizācijas = list();
        int mēģinājums = 0;
        while (optimizācijas.isEmpty() && mēģinājums < this.optimizācijas.size()) {
            optimizācijas = atlasitNakamoOptimicājiu().optimizē(atrisinājums);
            ++mēģinājums;
        }
        return optimizācijas;
    }

    private Optimizācija atlasitNakamoOptimicājiu() {
        skaitītājs += 1;
        if (skaitītājs >= optimizācijas.size()) {
            skaitītājs = 0;
        }
        return optimizācijas.get(skaitītājs);
    }
}
