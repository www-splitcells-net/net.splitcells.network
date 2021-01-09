package net.splitcells.gel.constraint.vidējs.dati;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.rating.structure.Rating;

import java.util.Set;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.map.Maps.map;

public class MaršrutēšanaNovērtējums {
    public static MaršrutēšanaNovērtējums veidot() {
        return new MaršrutēšanaNovērtējums();
    }
    private MaršrutēšanaNovērtējums() {

    }
    private final List<Rating> novērtējums = list();
    private final Map<Constraint, Set<GroupId>> bērnusUzGrupas = map();

    public List<Rating> gūtNovērtējums() {
        return novērtējums;
    }

    public Map<Constraint, Set<GroupId>> gūtBērnusUzGrupas() {
        return bērnusUzGrupas;
    }
}