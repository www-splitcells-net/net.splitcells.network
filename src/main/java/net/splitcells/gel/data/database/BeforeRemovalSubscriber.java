package net.splitcells.gel.data.database;

import java.util.Collection;

import net.splitcells.gel.data.table.Rinda;

@FunctionalInterface
public interface BeforeRemovalSubscriber {
	void rēgistrē_pirms_noņemšanas(Rinda rinda);

	default void rēgistrē_pirms_noņemšanas(Collection<Rinda> rindas) {
		rindas.forEach(line -> rēgistrē_pirms_noņemšanas(line));
	}

}
