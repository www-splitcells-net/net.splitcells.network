package net.splitcells.gel.data.lookup;

import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.data.table.attribute.Attribute;

public class LookupIFactory implements LookupFactory {
    @Override
    public <R> Lookup<R> lookup(Table table, Attribute<R> attribute) {
        return new LookupI<>(table, attribute);
    }

    @Override
    public void close() {

    }

    @Override
    public void flush() {

    }
}
