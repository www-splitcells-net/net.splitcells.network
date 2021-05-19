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

    Database demands_used();

    Database demands_unused();

    Line demandOfAllocation(Line piešķiršana);

    Line supplyOfAllocation(Line piešķiršana);

    Set<Line> allocations_of_supply(Line peidāvājums);

    Set<Line> allocations_of_demand(Line prasība);

    default Set<Line> supply_of_demand(Line prasība) {
        final Set<Line> peidāvājumi_no_prasībam = setOfUniques();
        allocations_of_demand(prasība)
                .forEach(piešķiršana -> peidāvājumi_no_prasībam.add(supplyOfAllocation(piešķiršana)));
        return peidāvājumi_no_prasībam;
    }
}