package net.splitcells.gel.kodols.dati.datubāze;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.kodols.dati.tabula.Rinda;
import net.splitcells.gel.kodols.dati.tabula.Tabula;
import net.splitcells.gel.kodols.dati.tabula.atribūts.Atribūts;

public interface DatuBāze extends Tabula {

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

	default <T extends PapildinājumsKlausītājs & NoņemšanasKlausītājs> void sinhronizē(T klausītājs) {
		abonē_uz_papildinājums(klausītājs);
		// TOCHECK TODOC Inform link after removal.
		abonē_uy_pēcNoņemšana(klausītājs);
	}

	void abonē_uz_papildinājums(PapildinājumsKlausītājs papildinājumuKlausītājs);

	void abonē_uz_iepriekšNoņemšana(NoņemšanasKlausītājs noņemšanasKlausītājs);

	void abonē_uy_pēcNoņemšana(NoņemšanasKlausītājs noņemšanasKlausītājs);
}