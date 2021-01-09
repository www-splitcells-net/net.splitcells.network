package net.splitcells.gel.data.database;

import java.util.Collection;

import net.splitcells.gel.data.table.Line;

@FunctionalInterface
public interface BeforeRemovalSubscriber {
	void rēgistrē_pirms_noņemšanas(Line rinda);

	default void rēgistrē_pirms_noņemšanas(Collection<Line> rindas) {
		rindas.forEach(line -> rēgistrē_pirms_noņemšanas(line));
	}

}
