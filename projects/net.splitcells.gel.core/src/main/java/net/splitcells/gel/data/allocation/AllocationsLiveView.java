package net.splitcells.gel.data.allocation;

import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.data.database.Database;

import java.util.Set;

import static net.splitcells.dem.data.set.Sets.setOfUniques;

public interface AllocationsLiveView extends Table {
    Database supplies();

    Database suppliesUsed();

    Database suppliesFree();

    Database demands();

    Database demandsUsed();

    Database demandsUnused();

    Line demandOfAllocation(Line piešķiršana);

    Line supplyOfAllocation(Line piešķiršana);

    Set<Line> allocationsOfSupply(Line peidāvājums);

    Set<Line> allocationsOfDemand(Line prasība);

    default Set<Line> supply_of_demand(Line prasība) {
        final Set<Line> peidāvājumi_no_prasībam = setOfUniques();
        allocationsOfDemand(prasība)
                .forEach(piešķiršana -> peidāvājumi_no_prasībam.add(supplyOfAllocation(piešķiršana)));
        return peidāvājumi_no_prasībam;
    }
}