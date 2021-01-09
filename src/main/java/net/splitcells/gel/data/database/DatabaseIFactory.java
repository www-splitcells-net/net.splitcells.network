package net.splitcells.gel.data.database;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.gel.data.table.attribute.Attribute;

import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;

public class DatabaseIFactory implements DatabaseFactory {

    @Override
    public Database datuBāze(String vārds, Attribute<? extends Object>... atribūti) {
        return new DatabaseI(vārds, null, atribūti);
    }

    @Override
    public Database datuBāze(Attribute<? extends Object>... atribūti) {
        return new DatabaseI(atribūti);
    }

    @Override
    public Database datuBāze(List<Attribute<?>> atribūti) {
        return new DatabaseI(atribūti);
    }

    @Override
    public Database datuBāze(List<Attribute<? extends Object>> atribūti, List<List<Object>> rindasVertības) {
        return new DatabaseI(atribūti, rindasVertības);
    }

    @Override
    @Deprecated
    public Database datuBāze(String vārds, Discoverable vecāks, Attribute<? extends Object>... atribūti) {
        return new DatabaseI(vārds, vecāks, listWithValuesOf(atribūti).mapped(a -> (Attribute<Object>) a));
    }

    @Override
    public Database datuBāze(String vārds, Discoverable vecāks, List<Attribute<? extends Object>> atribūti) {
        return new DatabaseI(vārds, vecāks, atribūti.mapped(a -> (Attribute<Object>) a));
    }

    @Override
    public void close() {

    }

    @Override
    public void flush() {

    }
}
