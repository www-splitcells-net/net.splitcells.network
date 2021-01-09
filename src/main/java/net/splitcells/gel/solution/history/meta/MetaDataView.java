package net.splitcells.gel.solution.history.meta;

import net.splitcells.dem.lang.dom.Domable;

import java.util.Optional;

public interface MetaDataView extends Domable {

	<T> Optional<T> vertība(Class<T> type);

}
