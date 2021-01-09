package net.splitcells.gel.data.database;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.gel.data.table.atribūts.Atribūts;

import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;

public class DatabaseIFactory implements DatabaseFactory {

    @Override
    public Database datuBāze(String vārds, Atribūts<? extends Object>... atribūti) {
        return new DatabaseI(vārds, null, atribūti);
    }

    @Override
    public Database datuBāze(Atribūts<? extends Object>... atribūti) {
        return new DatabaseI(atribūti);
    }

    @Override
    public Database datuBāze(List<Atribūts<?>> atribūti) {
        return new DatabaseI(atribūti);
    }

    @Override
    public Database datuBāze(List<Atribūts<? extends Object>> atribūti, List<List<Object>> rindasVertības) {
        return new DatabaseI(atribūti, rindasVertības);
    }

    @Override
    @Deprecated
    public Database datuBāze(String vārds, Discoverable vecāks, Atribūts<? extends Object>... atribūti) {
        return new DatabaseI(vārds, vecāks, listWithValuesOf(atribūti).mapped(a -> (Atribūts<Object>) a));
    }

    @Override
    public Database datuBāze(String vārds, Discoverable vecāks, List<Atribūts<? extends Object>> atribūti) {
        return new DatabaseI(vārds, vecāks, atribūti.mapped(a -> (Atribūts<Object>) a));
    }

    @Override
    public void close() {

    }

    @Override
    public void flush() {

    }
}
