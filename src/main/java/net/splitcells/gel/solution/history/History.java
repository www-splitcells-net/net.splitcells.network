package net.splitcells.gel.solution.history;

import static net.splitcells.gel.data.table.attribute.AttributeI.atributs;

import net.splitcells.gel.solution.history.event.Allocation;
import net.splitcells.gel.data.allocation.Allocations;
import net.splitcells.gel.data.database.AfterAdditionSubscriber;
import net.splitcells.gel.data.database.BeforeRemovalSubscriber;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.solution.history.meta.MetaDataView;

public interface History extends Allocations, AfterAdditionSubscriber, BeforeRemovalSubscriber {
    Attribute<Integer> PIEŠĶIRŠANA_ID = atributs(Integer.class, "pieškiršana-id");
    Attribute<Allocation> PIEŠĶIRŠANAS_NOTIKUMS = atributs(Allocation.class, "pieškiršana-notikums");
    Attribute<MetaDataView> META_DATA = atributs(MetaDataView.class, "refleksijas-dati");

    void atiestatUz(int index);
    int currentIndex();
}
