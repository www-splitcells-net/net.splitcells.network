package net.splitcells.gel.problem;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.ListView;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.column.Column;
import net.splitcells.gel.data.table.column.ColumnView;
import net.splitcells.gel.problem.derived.DerivedSolution;
import net.splitcells.gel.solution.Solutions;
import net.splitcells.gel.data.database.BeforeRemovalSubscriber;
import net.splitcells.gel.data.database.Database;
import net.splitcells.gel.data.database.AfterAdditionSubscriber;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.data.allocation.Allocations;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.rating.structure.MetaRating;
import net.splitcells.gel.solution.Solution;

import java.util.function.Function;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.utils.Not_implemented_yet.not_implemented_yet;
import static net.splitcells.gel.problem.derived.DerivedSolution.atvasinātaProblema;

public class ProblemI implements Problem {

    private final Constraint ierobežojums;
    private final Allocations piešķiršanas;
    protected Solution kāSolution;

    public static Problem problēma(Allocations piešķiršanas, Constraint ierobežojums) {
        return new ProblemI(piešķiršanas, ierobežojums);
    }

    protected ProblemI(Allocations piešķiršanas, Constraint ierobežojums) {
        this.piešķiršanas = piešķiršanas;
        this.ierobežojums = ierobežojums;
        sinhronizē(ierobežojums);
    }

    @Override
    public Constraint ierobežojums() {
        return ierobežojums;
    }

    @Override
    public Allocations piešķiršanas() {
        return piešķiršanas;
    }

    @Override
    public Solution uzAtrisinājumu() {
        throw not_implemented_yet();
    }

    @Override
    public Solution kāAtrisinājums() {
        if (kāSolution == null) {
            kāSolution = Solutions.atrisinājum(this);
        }
        return kāSolution;
    }

    @Override
    public DerivedSolution atvasinājums(Function<MetaRating, MetaRating> konversija) {
        return atvasinātaProblema(() -> list(), piešķiršanas, ierobežojums, konversija);
    }

    @Override
    public Database piedāvājums() {
        return this.piešķiršanas.piedāvājums();
    }

    @Override
    public Database piedāvājumi_lietoti() {
        return this.piešķiršanas.piedāvājumi_lietoti();
    }

    @Override
    public Database piedāvājums_nelietots() {
        return this.piešķiršanas.piedāvājums_nelietots();
    }

    @Override
    public Database prasība() {
        return this.piešķiršanas.prasība();
    }

    @Override
    public Database prasība_lietots() {
        return this.piešķiršanas.prasība_lietots();
    }

    @Override
    public Database prasības_nelietotas() {
        return this.piešķiršanas.prasības_nelietotas();
    }

    @Override
    public Line piešķirt(final Line prasība, final Line piedāvājums) {
        return this.piešķiršanas.piešķirt(prasība, piedāvājums);
    }

    @Override
    public Line prasība_no_piešķiršana(final Line piešķiršana) {
        return this.piešķiršanas.prasība_no_piešķiršana(piešķiršana);
    }

    @Override
    public Line piedāvājums_no_piešķiršana(final Line piešķiršana) {
        return this.piešķiršanas.piedāvājums_no_piešķiršana(piešķiršana);
    }

    @Override
    public java.util.Set<Line> piešķiršanas_no_piedāvājuma(final Line piedāvājuma) {
        return this.piešķiršanas.piešķiršanas_no_piedāvājuma(piedāvājuma);
    }

    @Override
    public java.util.Set<Line> piešķiršanasNo(final Line prasība, final Line piedāvājums) {
        return this.piešķiršanas.piešķiršanasNo(prasība, piedāvājums);
    }

    @Override
    public java.util.Set<Line> piešķiršanas_no_prasības(final Line prasība) {
        return this.piešķiršanas.piešķiršanas_no_prasības(prasība);
    }

    @Override
    public java.util.Set<Line> peidāvājumi_no_prasībam(final Line prasība) {
        return this.piešķiršanas.peidāvājumi_no_prasībam(prasība);
    }

    @Override
    public Line pieliktUnPārtulkot(List<?> vērtība) {
        return this.piešķiršanas.pieliktUnPārtulkot(vērtība);
    }

    @Override
    public Line pielikt(final Line rinda) {
        return this.piešķiršanas.pielikt(rinda);
    }

    @Override
    public void noņemt(final int rindasIndekss) {
        this.piešķiršanas.noņemt(rindasIndekss);
    }

    @Override
    public void noņemt(final Line rinda) {
        this.piešķiršanas.noņemt(rinda);
    }

    @Override
    public void aizvietot(final Line newRinda) {
        this.piešķiršanas.aizvietot(newRinda);
    }

    @Override
    public <T extends AfterAdditionSubscriber & BeforeRemovalSubscriber> void sinhronizē(final T klausītājs) {
        this.piešķiršanas.<T>sinhronizē(klausītājs);
    }

    @Override
    public void abonē_uz_papildinājums(final AfterAdditionSubscriber klausītājs) {
        this.piešķiršanas.abonē_uz_papildinājums(klausītājs);
    }

    @Override
    public void abonē_uz_iepriekšNoņemšana(final BeforeRemovalSubscriber pirmsNoņemšanasKlausītājs) {
        this.piešķiršanas.abonē_uz_iepriekšNoņemšana(pirmsNoņemšanasKlausītājs);
    }

    @Override
    public void abonē_uz_pēcNoņemšana(final BeforeRemovalSubscriber klausītājs) {
        this.piešķiršanas.abonē_uz_pēcNoņemšana(klausītājs);
    }

    @Override
    public List<Attribute<Object>> nosaukumuSkats() {
        return this.piešķiršanas.nosaukumuSkats();
    }

    @Override
    public <T extends Object> ColumnView<T> kolonnaSkats(final Attribute<T> atribūts) {
        return this.piešķiršanas.<T>kolonnaSkats(atribūts);
    }

    @Override
    public List<Column<Object>> kolonnaSkats() {
        return this.piešķiršanas.kolonnaSkats();
    }

    @Deprecated
    public ListView<Line> jēlaRindasSkats() {
        return this.piešķiršanas.jēlaRindasSkats();
    }

    @Override
    public boolean satur(final Line rinda) {
        return this.piešķiršanas.satur(rinda);
    }

    @Override
    public List<Line> gūtRindas() {
        return this.piešķiršanas.gūtRindas();
    }

    @Override
    public Line gūtJēluRindas(final int indekss) {
        return this.piešķiršanas.gūtJēluRindas(indekss);
    }

    @Override
    public int izmērs() {
        return this.piešķiršanas.izmērs();
    }

    @Override
    public boolean irTukšs() {
        return this.piešķiršanas.irTukšs();
    }

    @Override
    public boolean navTukšs() {
        return this.piešķiršanas.navTukšs();
    }

    @Override
    public List<Line> jēlasRindas() {
        return this.piešķiršanas.jēlasRindas();
    }

    public String uzCSV() {
        return this.piešķiršanas.uzCSV();
    }

    @Override
    public Line uzmeklēVienādus(final Attribute<Line> atribūts, final Line other) {
        return this.piešķiršanas.uzmeklēVienādus(atribūts, other);
    }

    @Override
    public org.w3c.dom.Element uzFods() {
        return this.piešķiršanas.uzFods();
    }

    @Override
    public List<String> path() {
        return this.piešķiršanas.path();
    }

    public org.w3c.dom.Node toDom() {
        return this.piešķiršanas.toDom();
    }
}
