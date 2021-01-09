package net.splitcells.gel.data.database;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.data.table.Rinda;
import net.splitcells.gel.data.table.Tabula;

public interface Database extends Tabula {

	Rinda pieliktUnPārtulkot(List<? extends Object> values);

	Rinda pielikt(Rinda rinda);

	@Deprecated
	void noņemt(int rindasIndekss);

	void noņemt(Rinda rinda);

	default void aizvietot(Rinda jaunaRinda) {
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