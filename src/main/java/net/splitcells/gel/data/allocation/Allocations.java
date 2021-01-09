package net.splitcells.gel.data.allocation;

import static java.util.stream.Collectors.toSet;
import static net.splitcells.dem.data.set.Sets.setOfUniques;

import java.util.Set;

import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.database.Database;

public interface Allocations extends Database, AllocationsLiveView {
    Line allocate(Line prasība, Line piedāvājums);

    default Set<Line> allocationsOf(Line prasība, Line piedāvājums) {
        final var piešķiršanasNo = allocations_of_supply(piedāvājums);
        return allocations_of_demand(prasība)
                .stream()
                .filter(piešķiršanasNo::contains)
                .collect(toSet());
    }
}