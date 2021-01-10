package net.splitcells.gel.solution.history;

import static net.splitcells.gel.data.table.attribute.AttributeI.atributs;

import net.splitcells.gel.solution.history.event.Allocation;
import net.splitcells.gel.data.allocation.Allocations;
import net.splitcells.gel.data.database.AfterAdditionSubscriber;
import net.splitcells.gel.data.database.BeforeRemovalSubscriber;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.solution.history.meta.MetaDataView;

public interface History extends Allocations, AfterAdditionSubscriber, BeforeRemovalSubscriber {
    Attribute<Integer> ALLOCATION_ID = atributs(Integer.class, "allocation-id");
    Attribute<Allocation> ALLOCATION_EVENT = atributs(Allocation.class, "allocation-notikums");
    Attribute<MetaDataView> META_DATA = atributs(MetaDataView.class, "meta-data");

    void resetTo(int index);

    int currentIndex();
}
