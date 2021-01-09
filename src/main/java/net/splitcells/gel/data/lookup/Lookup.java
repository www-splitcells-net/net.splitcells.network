package net.splitcells.gel.data.lookup;

import net.splitcells.dem.object.Discoverable;
import net.splitcells.gel.data.table.column.ColumnSubscriber;

public interface Lookup<T> extends ColumnSubscriber<T>, LookupComponents<T>, Discoverable {

}
