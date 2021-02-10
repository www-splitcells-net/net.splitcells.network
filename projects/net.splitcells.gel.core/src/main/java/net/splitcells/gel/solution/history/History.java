package net.splitcells.gel.solution.history;

import static net.splitcells.gel.data.table.attribute.AttributeI.attribute;

import net.splitcells.gel.solution.history.event.Allocation;
import net.splitcells.gel.data.allocation.Allocations;
import net.splitcells.gel.data.database.AfterAdditionSubscriber;
import net.splitcells.gel.data.database.BeforeRemovalSubscriber;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.solution.history.meta.MetaDataView;

public interface History extends Allocations, AfterAdditionSubscriber, BeforeRemovalSubscriber {
    Attribute<Integer> ALLOCATION_ID = attribute(Integer.class, "allocation-id");
    Attribute<Allocation> ALLOCATION_EVENT = attribute(Allocation.class, "allocation-notikums");
    Attribute<MetaDataView> META_DATA = attribute(MetaDataView.class, "meta-data");

    void resetTo(int index);

    int currentIndex();
}
