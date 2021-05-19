package net.splitcells.gel.data.allocation;

import static java.util.stream.Collectors.toSet;
import static net.splitcells.dem.data.set.Sets.setOfUniques;

import java.util.Set;

import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.database.Database;

/**
 * TODO Create usage table and use this instead of loosely coupled tables.
 * A usage table has a source table, a used table and unused table like demands, used demands and unused demands.
 * This also does reduce code duplication.
 */
public interface Allocations extends Database, AllocationsLiveView {
    Line allocate(Line demand, Line supply);

    default Set<Line> allocationsOf(Line demand, Line supply) {
        final var allocationsOf = allocationsOfSupply(supply);
        return allocationsOfDemand(demand)
                .stream()
                .filter(allocationsOf::contains)
                .collect(toSet());
    }
}