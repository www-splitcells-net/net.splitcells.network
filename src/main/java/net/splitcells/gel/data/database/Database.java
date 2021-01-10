package net.splitcells.gel.data.database;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.Table;

public interface Database extends Table {

	Line addTranslated(List<? extends Object> values);

	Line add(Line rinda);

	@Deprecated
	void remove(int rindasIndekss);

	void remove(Line rinda);

	default void replace(Line jaunaRinda) {
		if (null != rawLinesView().get(jaunaRinda.index())) {
			remove(jaunaRinda.index());
		}
		add(jaunaRinda);
	}

	default <T extends AfterAdditionSubscriber & BeforeRemovalSubscriber> void synchronize(T klausītājs) {
		subscribe_to_afterAdditions(klausītājs);
		subscriber_to_beforeRemoval(klausītājs);
	}

	void subscribe_to_afterAdditions(AfterAdditionSubscriber papildinājumuKlausītājs);

	void subscriber_to_beforeRemoval(BeforeRemovalSubscriber pirmsNoņemšanasKlausītājs);

	void subscriber_to_afterRemoval(BeforeRemovalSubscriber pirmsNoņemšanasKlausītājs);
}