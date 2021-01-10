package net.splitcells.gel.data.database;

import java.util.Collection;

import net.splitcells.gel.data.table.Line;

@FunctionalInterface
public interface BeforeRemovalSubscriber {
	void register_before_removal(Line rinda);

	default void register_before_removal(Collection<Line> rindas) {
		rindas.forEach(line -> register_before_removal(line));
	}

}
