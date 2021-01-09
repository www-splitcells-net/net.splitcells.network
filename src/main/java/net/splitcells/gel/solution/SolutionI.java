package net.splitcells.gel.solution;

import net.splitcells.dem.utils.Not_implemented_yet;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.ListView;
import net.splitcells.gel.solution.history.History;
import net.splitcells.gel.solution.history.Histories;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.column.Column;
import net.splitcells.gel.data.table.column.ColumnView;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.data.allocation.Allocations;
import net.splitcells.gel.data.database.AfterAdditionSubscriber;
import net.splitcells.gel.data.database.Database;
import net.splitcells.gel.data.database.BeforeRemovalSubscriber;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.problem.Problem;
import net.splitcells.gel.problem.derived.DerivedSolution;
import net.splitcells.gel.rating.structure.MetaRating;

import java.util.function.Function;

import static net.splitcells.dem.utils.Not_implemented_yet.not_implemented_yet;

public class SolutionI implements Solution {
	private final Problem problēma;
	private final History vēsture;
	
	public static Solution atrisinājums(Problem problēma) {
		return new SolutionI(problēma);
	}

	public SolutionI(Problem problēma) {
		this.problēma = problēma;
		vēsture = Histories.vēsture(this);
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
	public History vēsture() {
		return vēsture;
	}

	@java.lang.SuppressWarnings("all")
	public Constraint ierobežojums() {
		return this.problēma.ierobežojums();
	}

	@Override
	public Allocations piešķiršanas() {
		return problēma.piešķiršanas();
	}

	@java.lang.SuppressWarnings("all")
	public Solution uzAtrisinājumu() {
		return this.problēma.uzAtrisinājumu();
	}

	@java.lang.SuppressWarnings("all")
	public Solution kāAtrisinājums() {
		return this.problēma.kāAtrisinājums();
	}

	@Override
	public DerivedSolution atvasinājums(Function<MetaRating, MetaRating> atvasinātaijs) {
		return problēma.atvasinājums(atvasinātaijs);
	}

	@java.lang.SuppressWarnings("all")
	public Database piedāvājums() {
		return this.problēma.piedāvājums();
	}

	@java.lang.SuppressWarnings("all")
	public Database piedāvājumi_lietoti() {
		return this.problēma.piedāvājumi_lietoti();
	}

	@java.lang.SuppressWarnings("all")
	public Database piedāvājums_nelietots() {
		return this.problēma.piedāvājums_nelietots();
	}

	@java.lang.SuppressWarnings("all")
	public Database prasība() {
		return this.problēma.prasība();
	}

	@java.lang.SuppressWarnings("all")
	public Database prasība_lietots() {
		return this.problēma.prasība_lietots();
	}

	@java.lang.SuppressWarnings("all")
	public Database prasības_nelietotas() {
		return this.problēma.prasības_nelietotas();
	}

	@java.lang.SuppressWarnings("all")
	public Line piešķirt(final Line prasība, final Line piedāvājums) {
		return this.problēma.piešķirt(prasība, piedāvājums);
	}

	@java.lang.SuppressWarnings("all")
	public Line prasība_no_piešķiršana(final Line piešķiršana) {
		return this.problēma.prasība_no_piešķiršana(piešķiršana);
	}

	@java.lang.SuppressWarnings("all")
	public Line piedāvājums_no_piešķiršana(final Line piešķiršana) {
		return this.problēma.piedāvājums_no_piešķiršana(piešķiršana);
	}

	@java.lang.SuppressWarnings("all")
	public java.util.Set<Line> piešķiršanas_no_piedāvājuma(final Line peidāvājums) {
		return this.problēma.piešķiršanas_no_piedāvājuma(peidāvājums);
	}

	@java.lang.SuppressWarnings("all")
	public java.util.Set<Line> piešķiršanasNo(final Line prasība, final Line piedāvājums) {
		return this.problēma.piešķiršanasNo(prasība, piedāvājums);
	}

	@java.lang.SuppressWarnings("all")
	public java.util.Set<Line> piešķiršanas_no_prasības(final Line prasība) {
		return this.problēma.piešķiršanas_no_prasības(prasība);
	}

	@java.lang.SuppressWarnings("all")
	public java.util.Set<Line> peidāvājumi_no_prasībam(final Line prasība) {
		return this.problēma.peidāvājumi_no_prasībam(prasība);
	}

	@java.lang.SuppressWarnings("all")
	public Line pieliktUnPārtulkot(final List<?> vertības) {
		return this.problēma.pieliktUnPārtulkot(vertības);
	}

	@java.lang.SuppressWarnings("all")
	public Line pielikt(final Line rinda) {
		return this.problēma.pielikt(rinda);
	}

	@java.lang.SuppressWarnings("all")
	public void noņemt(final int rindasIndekss) {
		this.problēma.noņemt(rindasIndekss);
	}

	@java.lang.SuppressWarnings("all")
	public void noņemt(final Line rinda) {
		this.problēma.noņemt(rinda);
	}

	@java.lang.SuppressWarnings("all")
	public void aizvietot(final Line jaunaRinda) {
		this.problēma.aizvietot(jaunaRinda);
	}

	@java.lang.SuppressWarnings("all")
	public <T extends AfterAdditionSubscriber & BeforeRemovalSubscriber> void sinhronizē(final T klausītājs) {
		this.problēma.sinhronizē(klausītājs);
	}

	@java.lang.SuppressWarnings("all")
	public void abonē_uz_papildinājums(final AfterAdditionSubscriber klausītājs) {
		this.problēma.abonē_uz_papildinājums(klausītājs);
	}

	@java.lang.SuppressWarnings("all")
	public void abonē_uz_iepriekšNoņemšana(final BeforeRemovalSubscriber pirmsNoņemšanasKlausītājs) {
		this.problēma.abonē_uz_iepriekšNoņemšana(pirmsNoņemšanasKlausītājs);
	}

	@java.lang.SuppressWarnings("all")
	public void abonē_uz_pēcNoņemšana(final BeforeRemovalSubscriber listener) {
		this.problēma.abonē_uz_pēcNoņemšana(listener);
	}

	@java.lang.SuppressWarnings("all")
	public List<Attribute<Object>> nosaukumuSkats() {
		return this.problēma.nosaukumuSkats();
	}

	@java.lang.SuppressWarnings("all")
	public <T extends Object> ColumnView<T> kolonnaSkats(final Attribute<T> atribūts) {
		return this.problēma.<T>kolonnaSkats(atribūts);
	}

	@java.lang.SuppressWarnings("all")
	public List<Column<Object>> kolonnaSkats() {
		return this.problēma.kolonnaSkats();
	}

	@Deprecated
	@java.lang.SuppressWarnings("all")
	public ListView<Line> jēlaRindasSkats() {
		return this.problēma.jēlaRindasSkats();
	}

	@java.lang.SuppressWarnings("all")
	public boolean satur(final Line rinda) {
		return this.problēma.satur(rinda);
	}

	@java.lang.SuppressWarnings("all")
	public List<Line> gūtRindas() {
		return this.problēma.gūtRindas();
	}

	@java.lang.SuppressWarnings("all")
	public Line gūtJēluRindas(final int indekss) {
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
	public List<Line> jēlasRindas() {
		return this.problēma.jēlasRindas();
	}

	@java.lang.SuppressWarnings("all")
	public java.lang.String uzCSV() {
		return this.problēma.uzCSV();
	}

	@java.lang.SuppressWarnings("all")
	public Line uzmeklēVienādus(final Attribute<Line> atribūts, final Line cits) {
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

	@Override
	public String toString() {
		return path().toString();
	}
}
