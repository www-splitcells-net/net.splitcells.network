package net.splitcells.gel.solution.history.refleksija;

import net.splitcells.dem.lang.dom.Domable;

import java.util.Optional;

public interface RefleksijaSkats extends Domable {

	<T> Optional<T> vertība(Class<T> type);

}
