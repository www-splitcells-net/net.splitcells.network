package net.splitcells.gel.data.database;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.Table;

public interface Database extends Table {

	Line pieliktUnPārtulkot(List<? extends Object> values);

	Line pielikt(Line rinda);

	@Deprecated
	void noņemt(int rindasIndekss);

	void noņemt(Line rinda);

	default void aizvietot(Line jaunaRinda) {
		if (null != jēlaRindasSkats().get(jaunaRinda.indekss())) {
			noņemt(jaunaRinda.indekss());
		}
		pielikt(jaunaRinda);
	}

	default <T extends AfterAdditionSubscriber & BeforeRemovalSubscriber> void sinhronizē(T klausītājs) {
		abonē_uz_papildinājums(klausītājs);
		abonē_uz_iepriekšNoņemšana(klausītājs);
	}

	void abonē_uz_papildinājums(AfterAdditionSubscriber papildinājumuKlausītājs);

	void abonē_uz_iepriekšNoņemšana(BeforeRemovalSubscriber pirmsNoņemšanasKlausītājs);

	void abonē_uz_pēcNoņemšana(BeforeRemovalSubscriber pirmsNoņemšanasKlausītājs);
}