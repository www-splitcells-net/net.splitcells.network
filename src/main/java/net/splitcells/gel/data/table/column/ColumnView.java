package net.splitcells.gel.data.table.column;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.ListView;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.gel.data.lookup.LookupComponents;

public interface ColumnView<T> extends ListView<T>, LookupComponents<T> {
    /**
     * JAUDA
     */
    default List<T> values() {
        final List<T> vertība = Lists.<T>list();
        this.stream().filter(e -> e != null).forEach(e -> vertība.add(e));
        return vertība;
    }
}
