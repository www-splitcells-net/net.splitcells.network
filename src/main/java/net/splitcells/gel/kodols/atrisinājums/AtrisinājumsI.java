package net.splitcells.gel.kodols.atrisinājums;

import net.splitcells.dem.utils.Not_implemented_yet;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.ListView;
import net.splitcells.gel.kodols.atrisinājums.vēsture.Vēstures;
import net.splitcells.gel.kodols.ierobežojums.Ierobežojums;
import net.splitcells.gel.kodols.dati.piešķiršanas.Piešķiršanas;
import net.splitcells.gel.kodols.dati.tabula.kolonna.Kolonna;
import net.splitcells.gel.kodols.dati.tabula.kolonna.KolonnaSkats;
import net.splitcells.gel.kodols.dati.datubāze.PapildinājumsKlausītājs;
import net.splitcells.gel.kodols.dati.datubāze.DatuBāze;
import net.splitcells.gel.kodols.dati.datubāze.PirmsNoņemšanasKlausītājs;
import net.splitcells.gel.kodols.dati.tabula.Rinda;
import net.splitcells.gel.kodols.dati.tabula.atribūts.Atribūts;
import net.splitcells.gel.kodols.problēma.Problēma;
import net.splitcells.gel.kodols.problēma.atvasināts.AtvasinātsAtrisinājums;
import net.splitcells.gel.kodols.novērtējums.struktūra.RefleksijaNovērtējums;
import net.splitcells.gel.kodols.atrisinājums.vēsture.Vēsture;

import java.util.function.Function;

import static net.splitcells.dem.utils.Not_implemented_yet.not_implemented_yet;

public class AtrisinājumsI implements Atrisinājums {
	private final Problēma problēma;
	private final Vēsture vēsture;

	public AtrisinājumsI(Problēma problēma) {
		this.problēma = problēma;
		vēsture = Vēstures.vēsture(this);
	}

	@Override
	public boolean irPilns() {
		return prasības_nelietotas().izmērs() == 0
				|| prasība().izmērs() < piedāvājums().izmērs()
				&& prasība_lietots().izmērs() == piedāvājums().izmērs();
	}

	public boolean līdzsvarots() {
		return prasība().jēlaRindasSkats().size() == piedāvājums().jēlaRindasSkats().size();
	}

	public boolean pieļaujams(final long kļudas_slieksnis) {
		throw new Not_implemented_yet();
	}

	@Override
	public Vēsture vēsture() {
		return vēsture;
	}

	@java.lang.SuppressWarnings("all")
	public Ierobežojums ierobežojums() {
		return this.problēma.ierobežojums();
	}

	@Override
	public Piešķiršanas piešķiršanas() {
		return problēma.piešķiršanas();
	}

	@java.lang.SuppressWarnings("all")
	public Atrisinājums uzProblēma() {
		return this.problēma.uzProblēma();
	}

	@java.lang.SuppressWarnings("all")
	public Atrisinājums kāProblēma() {
		return this.problēma.kāProblēma();
	}

	@Override
	public AtvasinātsAtrisinājums atvasinājums(Function<RefleksijaNovērtējums, RefleksijaNovērtējums> atvasinātaijs) {
		return problēma.atvasinājums(atvasinātaijs);
	}

	@java.lang.SuppressWarnings("all")
	public DatuBāze piedāvājums() {
		return this.problēma.piedāvājums();
	}

	@java.lang.SuppressWarnings("all")
	public DatuBāze piedāvājumi_lietoti() {
		return this.problēma.piedāvājumi_lietoti();
	}

	@java.lang.SuppressWarnings("all")
	public DatuBāze piedāvājums_nelietots() {
		return this.problēma.piedāvājums_nelietots();
	}

	@java.lang.SuppressWarnings("all")
	public DatuBāze prasība() {
		return this.problēma.prasība();
	}

	@java.lang.SuppressWarnings("all")
	public DatuBāze prasība_lietots() {
		return this.problēma.prasība_lietots();
	}

	@java.lang.SuppressWarnings("all")
	public DatuBāze prasības_nelietotas() {
		return this.problēma.prasības_nelietotas();
	}

	@java.lang.SuppressWarnings("all")
	public Rinda piešķirt(final Rinda prasība, final Rinda piedāvājums) {
		return this.problēma.piešķirt(prasība, piedāvājums);
	}

	@java.lang.SuppressWarnings("all")
	public Rinda prasība_no_piešķiršana(final Rinda piešķiršana) {
		return this.problēma.prasība_no_piešķiršana(piešķiršana);
	}

	@java.lang.SuppressWarnings("all")
	public Rinda piedāvājums_no_piešķiršana(final Rinda piešķiršana) {
		return this.problēma.piedāvājums_no_piešķiršana(piešķiršana);
	}

	@java.lang.SuppressWarnings("all")
	public java.util.Set<Rinda> piešķiršanas_no_piedāvājuma(final Rinda peidāvājums) {
		return this.problēma.piešķiršanas_no_piedāvājuma(peidāvājums);
	}

	@java.lang.SuppressWarnings("all")
	public java.util.Set<Rinda> piešķiršanasNo(final Rinda prasība, final Rinda piedāvājums) {
		return this.problēma.piešķiršanasNo(prasība, piedāvājums);
	}

	@java.lang.SuppressWarnings("all")
	public java.util.Set<Rinda> piešķiršanas_no_prasības(final Rinda prasība) {
		return this.problēma.piešķiršanas_no_prasības(prasība);
	}

	@java.lang.SuppressWarnings("all")
	public java.util.Set<Rinda> peidāvājumi_no_prasībam(final Rinda prasība) {
		return this.problēma.peidāvājumi_no_prasībam(prasība);
	}

	@java.lang.SuppressWarnings("all")
	public Rinda pieliktUnPārtulkot(final List<?> vertības) {
		return this.problēma.pieliktUnPārtulkot(vertības);
	}

	@java.lang.SuppressWarnings("all")
	public Rinda pielikt(final Rinda rinda) {
		return this.problēma.pielikt(rinda);
	}

	@java.lang.SuppressWarnings("all")
	public void noņemt(final int rindasIndekss) {
		this.problēma.noņemt(rindasIndekss);
	}

	@java.lang.SuppressWarnings("all")
	public void noņemt(final Rinda rinda) {
		this.problēma.noņemt(rinda);
	}

	@java.lang.SuppressWarnings("all")
	public void aizvietot(final Rinda jaunaRinda) {
		this.problēma.aizvietot(jaunaRinda);
	}

	@java.lang.SuppressWarnings("all")
	public <T extends PapildinājumsKlausītājs & PirmsNoņemšanasKlausītājs> void sinhronizē(final T klausītājs) {
		this.problēma.sinhronizē(klausītājs);
	}

	@java.lang.SuppressWarnings("all")
	public void abonē_uz_papildinājums(final PapildinājumsKlausītājs klausītājs) {
		this.problēma.abonē_uz_papildinājums(klausītājs);
	}

	@java.lang.SuppressWarnings("all")
	public void abonē_uz_iepriekšNoņemšana(final PirmsNoņemšanasKlausītājs pirmsNoņemšanasKlausītājs) {
		this.problēma.abonē_uz_iepriekšNoņemšana(pirmsNoņemšanasKlausītājs);
	}

	@java.lang.SuppressWarnings("all")
	public void abonē_uy_pēcNoņemšana(final PirmsNoņemšanasKlausītājs listener) {
		this.problēma.abonē_uy_pēcNoņemšana(listener);
	}

	@java.lang.SuppressWarnings("all")
	public List<Atribūts<Object>> nosaukumuSkats() {
		return this.problēma.nosaukumuSkats();
	}

	@java.lang.SuppressWarnings("all")
	public <T extends Object> KolonnaSkats<T> kolonnaSkats(final Atribūts<T> atribūts) {
		return this.problēma.<T>kolonnaSkats(atribūts);
	}

	@java.lang.SuppressWarnings("all")
	public List<Kolonna<Object>> kolonnaSkats() {
		return this.problēma.kolonnaSkats();
	}

	@Deprecated
	@java.lang.SuppressWarnings("all")
	public ListView<Rinda> jēlaRindasSkats() {
		return this.problēma.jēlaRindasSkats();
	}

	@java.lang.SuppressWarnings("all")
	public boolean satur(final Rinda rinda) {
		return this.problēma.satur(rinda);
	}

	@java.lang.SuppressWarnings("all")
	public List<Rinda> gūtRindas() {
		return this.problēma.gūtRindas();
	}

	@java.lang.SuppressWarnings("all")
	public Rinda gūtJēluRindas(final int indekss) {
		return this.problēma.gūtJēluRindas(indekss);
	}

	@java.lang.SuppressWarnings("all")
	public int izmērs() {
		return this.problēma.izmērs();
	}

	@java.lang.SuppressWarnings("all")
	public boolean irTukšs() {
		return this.problēma.irTukšs();
	}

	@java.lang.SuppressWarnings("all")
	public boolean navTukšs() {
		return this.problēma.navTukšs();
	}

	@java.lang.SuppressWarnings("all")
	public List<Rinda> jēlasRindas() {
		return this.problēma.jēlasRindas();
	}

	@java.lang.SuppressWarnings("all")
	public java.lang.String uzCSV() {
		return this.problēma.uzCSV();
	}

	@java.lang.SuppressWarnings("all")
	public Rinda uzmeklēVienādus(final Atribūts<Rinda> atribūts, final Rinda cits) {
		return this.problēma.uzmeklēVienādus(atribūts, cits);
	}

	@java.lang.SuppressWarnings("all")
	public org.w3c.dom.Element uzFods() {
		return this.problēma.uzFods();
	}

	@java.lang.SuppressWarnings("all")
	public List<String> path() {
		return this.problēma.path();
	}

	@java.lang.SuppressWarnings("all")
	public org.w3c.dom.Node toDom() {
		return this.problēma.toDom();
	}
}
