package net.splitcells.gel.data.lookup;

import net.splitcells.dem.environment.resource.ResourceI;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.data.table.attribute.Attribute;

public class Lookups extends ResourceI<LookupFactory> {
    public Lookups() {
        super(() -> new LookupIFactory());
    }

    public static <R> Lookup<R> lookup(Table table, Attribute<R> attribute) {
        return new LookupI<>(table, attribute);
    }
}
