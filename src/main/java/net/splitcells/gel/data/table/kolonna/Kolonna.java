package net.splitcells.gel.data.table.kolonna;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.data.database.AfterAdditionSubscriber;
import net.splitcells.gel.data.database.BeforeRemovalSubscriber;

public interface Kolonna<T> extends List<T>, AfterAdditionSubscriber, BeforeRemovalSubscriber, KolonnaSkats<T> {
}
