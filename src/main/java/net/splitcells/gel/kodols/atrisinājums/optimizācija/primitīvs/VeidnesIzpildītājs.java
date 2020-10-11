package net.splitcells.gel.kodols.atrisinājums.optimizācija.primitīvs;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.kodols.atrisinājums.AtrisinājumaSkats;
import net.splitcells.gel.kodols.atrisinājums.optimizācija.Optimizācija;
import net.splitcells.gel.kodols.atrisinājums.optimizācija.OptimizācijasNotikums;
import net.splitcells.gel.kodols.dati.tabula.Tabula;

import java.nio.file.Path;

import static net.splitcells.dem.data.set.list.Lists.list;

public class VeidnesIzpildītājs implements Optimizācija {
    public static VeidnesIzpildītājs veidnesIzpildītājs(Tabula veidne) {
        return new VeidnesIzpildītājs(veidne);
    }

    private final Tabula veidne;

    private VeidnesIzpildītājs(Tabula veidne) {
        this.veidne = veidne;
    }

    @Override
    public List<OptimizācijasNotikums> optimizē(AtrisinājumaSkats atrisinājums) {
        final List<OptimizācijasNotikums> optimicaija = list();
        return optimicaija;
    }
}
