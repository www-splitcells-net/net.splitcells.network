package net.splitcells.gel.atrisinājums.optimizācija.refleksija;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.atrisinājums.AtrisinājumaSkats;
import net.splitcells.gel.atrisinājums.optimizācija.Optimizācija;
import net.splitcells.gel.atrisinājums.optimizācija.OptimizācijasNotikums;

import static net.splitcells.dem.utils.Not_implemented_yet.not_implemented_yet;
import static net.splitcells.dem.data.set.list.Lists.list;

public class Atkārtotājs implements Optimizācija {
    public static Atkārtotājs atkārtotājs(Optimizācija optimizācija, int maksimalsAtkārtošanasSkaitlis) {
        return new Atkārtotājs(optimizācija, maksimalsAtkārtošanasSkaitlis);
    }

    private final Optimizācija optimizācija;
    private final int maksimalsAtkārtošanasSkaitlis;
    private int atkārtošanasSkaitlis = 0;

    private Atkārtotājs(Optimizācija optimizācija, int maksimalsAtkārtošanasSkaitlis) {
        this.optimizācija = optimizācija;
        this.maksimalsAtkārtošanasSkaitlis = maksimalsAtkārtošanasSkaitlis;
    }

    @Override
    public List<OptimizācijasNotikums> optimizē(AtrisinājumaSkats atrisinājums) {
        if (atkārtošanasSkaitlis >= maksimalsAtkārtošanasSkaitlis) {
            return list();
        }
        atkārtošanasSkaitlis += 1;
        return optimizē(atrisinājums);
    }
}
