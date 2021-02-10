package net.splitcells.gel.data.lookup;

import net.splitcells.dem.resource.communication.Closeable;
import net.splitcells.dem.resource.communication.Flushable;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.data.table.attribute.Attribute;

public interface LookupFactory extends Closeable, Flushable {
    <R> Lookup<R> lookup(Table table, Attribute<R> attribute);
}
