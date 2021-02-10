package net.splitcells.gel.data.allocations;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.data.allocation.Allocations;
import net.splitcells.gel.data.database.Databases;
import net.splitcells.gel.data.table.attribute.Attribute;

import java.util.Collection;
import java.util.function.Function;

import static java.util.Arrays.asList;
import static net.splitcells.gel.common.Language.TEST;
import static net.splitcells.gel.data.allocation.Allocationss.allocations;
import static net.splitcells.gel.data.database.Databases.database;

public class AllocationsSF {
    public static Collection<Function<List<Attribute<? extends Object>>, Allocations>> databaseFactory() {
        return asList((a) -> allocations(TEST.value(), database(a), Databases.database()),
                (a) -> allocations(TEST.value(), Databases.database(), database(a)));
    }
}
