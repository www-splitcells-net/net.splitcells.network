package net.splitcells.gel.solution.history;

import static net.splitcells.gel.data.table.atribūts.AtribūtsI.atributs;

import net.splitcells.gel.solution.history.event.Allocation;
import net.splitcells.gel.data.allocation.Allocations;
import net.splitcells.gel.data.database.AfterAdditionSubscriber;
import net.splitcells.gel.data.database.BeforeRemovalSubscriber;
import net.splitcells.gel.data.table.atribūts.Atribūts;
import net.splitcells.gel.solution.history.meta.MetaDataView;

public interface History extends Allocations, AfterAdditionSubscriber, BeforeRemovalSubscriber {
    Atribūts<Integer> PIEŠĶIRŠANA_ID = atributs(Integer.class, "pieškiršana-id");
    Atribūts<Allocation> PIEŠĶIRŠANAS_NOTIKUMS = atributs(Allocation.class, "pieškiršana-notikums");
    Atribūts<MetaDataView> REFLEKSIJAS_DATI = atributs(MetaDataView.class, "refleksijas-dati");

    void atiestatUz(int index);
    int momentansIndekss();
}
