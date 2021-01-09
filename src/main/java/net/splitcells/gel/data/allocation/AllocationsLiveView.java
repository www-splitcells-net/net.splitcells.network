package net.splitcells.gel.data.allocation;

import net.splitcells.gel.data.table.Rinda;
import net.splitcells.gel.data.table.Tabula;
import net.splitcells.gel.data.database.Database;

import java.util.Set;

import static net.splitcells.dem.data.set.Sets.setOfUniques;

public interface AllocationsLiveView extends Tabula {
    Database piedāvājums();

    Database piedāvājumi_lietoti();

    Database piedāvājums_nelietots();

    Database prasība();

    Database prasība_lietots();

    Database prasības_nelietotas();

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