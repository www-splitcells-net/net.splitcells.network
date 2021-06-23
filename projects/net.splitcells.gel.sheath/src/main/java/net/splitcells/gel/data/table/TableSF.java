package net.splitcells.gel.data.table;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.data.allocation.Allocations;
import net.splitcells.gel.data.database.DatabaseI;
import net.splitcells.gel.data.database.DatabaseSF;
import net.splitcells.gel.data.database.Databases;
import net.splitcells.gel.data.table.attribute.Attribute;

import java.util.function.Function;

import static net.splitcells.dem.data.set.list.Lists.list;

public class TableSF {

    /**
     * TODO Support different table type mixing, in order to create {@link Allocations} instances.
     */
    public static List<Table> tukšaTabulas() {
        return DatabaseSF.emptyDatabase2().mapped(a -> (Table) a);

    }

    public static List<Function<List<Attribute<? extends Object>>, Table>> testTableFactory() {
        return list((a) -> Databases.database(a));
    }
}
