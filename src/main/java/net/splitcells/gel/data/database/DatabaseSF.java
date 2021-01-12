package net.splitcells.gel.data.database;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.data.table.attribute.Attribute;

import java.util.function.Function;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.data.table.attribute.Attributes.attributeATO;


public class DatabaseSF {

    public static List<Database> emptyDatabase() {
        return list(Databases.database());
    }

    public static List<Database> emptyDatabase2() {
        return list(Databases.database(attributeATO()));
    }

    public static List<Function<List<Attribute<?>>, Database>> testDatabaseFactory() {
        List<Function<List<Attribute<?>>, Database>> tester = list((a) -> new DatabaseRefFactory().database(a));
        return tester;
    }

}
