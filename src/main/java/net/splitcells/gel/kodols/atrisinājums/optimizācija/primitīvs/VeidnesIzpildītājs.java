package net.splitcells.gel.kodols.atrisinājums.optimizācija.primitīvs;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.kodols.atrisinājums.AtrisinājumaSkats;
import net.splitcells.gel.kodols.atrisinājums.optimizācija.Optimizācija;
import net.splitcells.gel.kodols.atrisinājums.optimizācija.OptimizācijasNotikums;
import net.splitcells.gel.kodols.dati.tabula.Tabula;

import java.nio.file.Path;

public class VeidnesIzpildītājs implements Optimizācija {
    public static VeidnesIzpildītājs veidnesIzpildītājs(Tabula veidne) {
        return new VeidnesIzpildītājs(veidne);
    }

    private VeidnesIzpildītājs(Tabula veidne) {

    }

    @Override
    public List<OptimizācijasNotikums> optimizē(AtrisinājumaSkats atrisinājums) {
        return null;
    }
}
