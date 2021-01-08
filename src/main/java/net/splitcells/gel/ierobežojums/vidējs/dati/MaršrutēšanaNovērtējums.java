package net.splitcells.gel.ierobežojums.vidējs.dati;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.gel.ierobežojums.GrupaId;
import net.splitcells.gel.ierobežojums.Ierobežojums;
import net.splitcells.gel.novērtējums.struktūra.Novērtējums;

import java.util.Set;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.map.Maps.map;

public class MaršrutēšanaNovērtējums {
    public static MaršrutēšanaNovērtējums veidot() {
        return new MaršrutēšanaNovērtējums();
    }
    private MaršrutēšanaNovērtējums() {

    }
    private final List<Novērtējums> novērtējums = list();
    private final Map<Ierobežojums, Set<GrupaId>> bērnusUzGrupas = map();

    public List<Novērtējums> gūtNovērtējums() {
        return novērtējums;
    }

    public Map<Ierobežojums, Set<GrupaId>> gūtBērnusUzGrupas() {
        return bērnusUzGrupas;
    }
}