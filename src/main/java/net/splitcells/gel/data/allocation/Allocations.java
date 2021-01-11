package net.splitcells.gel.data.allocation;

import static java.util.stream.Collectors.toSet;
import static net.splitcells.dem.data.set.Sets.setOfUniques;

import java.util.Set;

import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.database.Database;

public interface Allocations extends Database, AllocationsLiveView {
    Line allocate(Line demand, Line supply);

    default Set<Line> allocationsOf(Line demand, Line supply) {
        final var allocationsOf = allocations_of_supply(supply);
        return allocations_of_demand(demand)
                .stream()
                .filter(allocationsOf::contains)
                .collect(toSet());
    }
}