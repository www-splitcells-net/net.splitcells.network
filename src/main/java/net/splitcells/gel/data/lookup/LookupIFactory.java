package net.splitcells.gel.data.lookup;

import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.data.table.attribute.Attribute;

public class LookupIFactory implements LookupFactory {
    @Override
    public <R> Lookup<R> uzmeklē(Table tabula, Attribute<R> atribūts) {
        return new LookupI<>(tabula, atribūts);
    }

    @Override
    public void close() {

    }

    @Override
    public void flush() {

    }
}
