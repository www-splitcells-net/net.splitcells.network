package net.splitcells.gel.data.table.column;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.ListView;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.gel.data.lookup.LookupComponents;

public interface ColumnView<T> extends ListView<T>, LookupComponents<T> {
    /**
     * TODO PERFORMANCE
     */
    default List<T> values() {
        final List<T> values = Lists.<T>list();
        this.stream().filter(e -> e != null).forEach(e -> values.add(e));
        return values;
    }
}
