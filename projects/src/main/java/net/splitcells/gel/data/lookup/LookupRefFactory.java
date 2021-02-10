package net.splitcells.gel.data.lookup;

import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.data.table.attribute.Attribute;

public class LookupRefFactory implements LookupFactory {
    @Override
    public <R> Lookup<R> lookup(Table table, Attribute<R> attribute) {
        return new LookupIRef(table, attribute);
    }

    @Override
    public void close() {

    }

    @Override
    public void flush() {

    }
}
