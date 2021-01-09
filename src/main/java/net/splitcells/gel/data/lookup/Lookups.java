package net.splitcells.gel.data.lookup;

import net.splitcells.dem.environment.resource.ResourceI;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.data.table.attribute.Attribute;

public class Lookups extends ResourceI<LookupFactory> {
    public Lookups() {
        super(() -> new LookupIFactory());
    }

    public static <R> Lookup<R> uzmeklē(Table tabula, Attribute<R> atribūts) {
        return new LookupI<>(tabula, atribūts);
    }
}
