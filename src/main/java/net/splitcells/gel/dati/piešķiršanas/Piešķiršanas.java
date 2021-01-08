package net.splitcells.gel.dati.piešķiršanas;

import static java.util.stream.Collectors.toSet;
import static net.splitcells.dem.data.set.Sets.setOfUniques;

import java.util.Set;

import net.splitcells.gel.dati.tabula.Rinda;
import net.splitcells.gel.dati.datubāze.DatuBāze;

public interface Piešķiršanas extends DatuBāze, PiešķiršanasTiešraidesSkats {
    Rinda piešķirt(Rinda prasība, Rinda piedāvājums);

    default Set<Rinda> piešķiršanasNo(Rinda prasība, Rinda piedāvājums) {
        final var piešķiršanasNo = piešķiršanas_no_piedāvājuma(piedāvājums);
        return piešķiršanas_no_prasības(prasība)
                .stream()
                .filter(piešķiršanasNo::contains)
                .collect(toSet());
    }
}