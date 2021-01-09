package net.splitcells.gel.data.allocation;

import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.data.database.Database;

import java.util.Set;

import static net.splitcells.dem.data.set.Sets.setOfUniques;

public interface AllocationsLiveView extends Table {
    Database piedāvājums();

    Database piedāvājumi_lietoti();

    Database supplies_unused();

    Database demands();

    Database prasība_lietots();

    Database demands_unused();

    Line prasība_no_piešķiršana(Line piešķiršana);

    Line piedāvājums_no_piešķiršana(Line piešķiršana);

    Set<Line> piešķiršanas_no_piedāvājuma(Line peidāvājums);

    Set<Line> piešķiršanas_no_prasības(Line prasība);

    default Set<Line> peidāvājumi_no_prasībam(Line prasība) {
        final Set<Line> peidāvājumi_no_prasībam = setOfUniques();
        piešķiršanas_no_prasības(prasība)
                .forEach(piešķiršana -> peidāvājumi_no_prasībam.add(piedāvājums_no_piešķiršana(piešķiršana)));
        return peidāvājumi_no_prasībam;
    }
}