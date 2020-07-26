package net.splitcells.gel.kodols.dati.piešķiršanas;

import net.splitcells.gel.kodols.dati.datubāze.DatuBāze;
import net.splitcells.gel.kodols.dati.tabula.Rinda;
import net.splitcells.gel.kodols.dati.tabula.Tabula;

import java.util.Set;

import static net.splitcells.dem.data.set.Sets.setOfUniques;

public interface PiešķiršanasTiešraidesSkats extends Tabula {
    DatuBāze piedāvājums();

    DatuBāze piedāvājumi_lietoti();

    DatuBāze piedāvājums_nelietots();

    DatuBāze prasība();

    DatuBāze prasība_lietots();

    DatuBāze prasības_nelietotas();

    Rinda prasība_no_piešķiršana(Rinda piešķiršana);

    Rinda piedāvājums_no_piešķiršana(Rinda piešķiršana);

    Set<Rinda> piešķiršanas_no_piedāvājuma(Rinda peidāvājums);

    Set<Rinda> piešķiršanas_no_prasības(Rinda prasība);

    default Set<Rinda> peidāvājumi_no_prasībam(Rinda prasība) {
        final Set<Rinda> peidāvājumi_no_prasībam = setOfUniques();
        piešķiršanas_no_prasības(prasība)
                .forEach(piešķiršana -> peidāvājumi_no_prasībam.add(piedāvājums_no_piešķiršana(piešķiršana)));
        return peidāvājumi_no_prasībam;
    }
}