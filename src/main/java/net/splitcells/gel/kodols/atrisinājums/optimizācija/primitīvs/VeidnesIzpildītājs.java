package net.splitcells.gel.kodols.atrisinājums.optimizācija.primitīvs;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.kodols.atrisinājums.AtrisinājumaSkats;
import net.splitcells.gel.kodols.atrisinājums.optimizācija.Optimizācija;
import net.splitcells.gel.kodols.atrisinājums.optimizācija.OptimizācijasNotikums;

import java.nio.file.Path;

public class VeidnesIzpildītājs implements Optimizācija {
    public static VeidnesIzpildītājs veidnesIzpildītājs(Path veidne) {
        return new VeidnesIzpildītājs();
    }

    private VeidnesIzpildītājs() {

    }

    @Override
    public List<OptimizācijasNotikums> optimizē(AtrisinājumaSkats atrisinājums) {
        return null;
    }
}
