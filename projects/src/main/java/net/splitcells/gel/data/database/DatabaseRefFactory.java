package net.splitcells.gel.data.database;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.gel.data.table.attribute.Attribute;

import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;

public class DatabaseRefFactory implements DatabaseFactory {
    @Override
    public Database database(String name, Attribute<?>... header) {
        return new DatabaseIRef(name, null, header);
    }

    @Override
    public Database database(Attribute<?>... header) {
        return new DatabaseIRef(header);
    }

    @Override
    public Database database(List<Attribute<?>> header) {
        return new DatabaseIRef(header);
    }

    @Override
    public Database database(List<Attribute<?>> headers, List<List<Object>> linesValues) {
        return new DatabaseIRef(headers, linesValues);
    }

    @Override
    public Database database(String name, Discoverable parent, Attribute<? extends Object>... header) {
        return new DatabaseIRef(name, parent, listWithValuesOf(header).mapped(a -> (Attribute<Object>) a));
    }

    @Override
    public Database database(String name, Discoverable parent, List<Attribute<? extends Object>> header) {
        return new DatabaseIRef(name, parent, header.mapped(a -> (Attribute<Object>) a));
    }

    @Override
    public void close() {

    }

    @Override
    public void flush() {

    }
}
