package net.splitcells.gel.data.allocation;

import static java.util.stream.Collectors.toSet;
import static net.splitcells.dem.data.set.Sets.setOfUniques;

import java.util.Set;

import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.database.Database;

public interface Allocations extends Database, AllocationsLiveView {
    Line piešķirt(Line prasība, Line piedāvājums);

    default Set<Line> piešķiršanasNo(Line prasība, Line piedāvājums) {
        final var piešķiršanasNo = piešķiršanas_no_piedāvājuma(piedāvājums);
        return piešķiršanas_no_prasības(prasība)
                .stream()
                .filter(piešķiršanasNo::contains)
                .collect(toSet());
    }
}