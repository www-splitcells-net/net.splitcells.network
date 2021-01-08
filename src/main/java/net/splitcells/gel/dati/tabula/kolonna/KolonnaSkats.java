package net.splitcells.gel.dati.tabula.kolonna;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.ListView;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.gel.dati.uzmeklēšana.UzmeklēšanaKomponents;

public interface KolonnaSkats<T> extends ListView<T>, UzmeklēšanaKomponents<T> {
    /**
     * JAUDA
     */
    default List<T> vertības() {
        final List<T> vertība = Lists.<T>list();
        this.stream().filter(e -> e != null).forEach(e -> vertība.add(e));
        return vertība;
    }
}
