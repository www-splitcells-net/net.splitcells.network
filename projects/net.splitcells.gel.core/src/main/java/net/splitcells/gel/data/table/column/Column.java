package net.splitcells.gel.data.table.column;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.data.database.AfterAdditionSubscriber;
import net.splitcells.gel.data.database.BeforeRemovalSubscriber;

public interface Column<T> extends List<T>, AfterAdditionSubscriber, BeforeRemovalSubscriber, ColumnView<T> {
}
