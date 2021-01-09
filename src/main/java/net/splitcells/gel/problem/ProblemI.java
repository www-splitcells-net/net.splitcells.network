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
        synchronize(ierobežojums);
    }

    @Override
    public Constraint constraint() {
        return ierobežojums;
    }

    @Override
    public Allocations allocations() {
        return piešķiršanas;
    }

    @Override
    public Solution toSolution() {
        throw not_implemented_yet();
    }

    @Override
    public Solution asSolution() {
        if (kāSolution == null) {
            kāSolution = Solutions.solution(this);
        }
        return kāSolution;
    }

    @Override
    public DerivedSolution derived(Function<MetaRating, MetaRating> konversija) {
        return atvasinātaProblema(() -> list(), piešķiršanas, ierobežojums, konversija);
    }

    @Override
    public Database supplies() {
        return this.piešķiršanas.supplies();
    }

    @Override
    public Database supplies_used() {
        return this.piešķiršanas.supplies_used();
    }

    @Override
    public Database supplies_unused() {
        return this.piešķiršanas.supplies_unused();
    }

    @Override
    public Database demands() {
        return this.piešķiršanas.demands();
    }

    @Override
    public Database demands_used() {
        return this.piešķiršanas.demands_used();
    }

    @Override
    public Database demands_unused() {
        return this.piešķiršanas.demands_unused();
    }

    @Override
    public Line allocate(final Line prasība, final Line piedāvājums) {
        return this.piešķiršanas.allocate(prasība, piedāvājums);
    }

    @Override
    public Line demand_of_allocation(final Line piešķiršana) {
        return this.piešķiršanas.demand_of_allocation(piešķiršana);
    }

    @Override
    public Line supply_of_allocation(final Line piešķiršana) {
        return this.piešķiršanas.supply_of_allocation(piešķiršana);
    }

    @Override
    public java.util.Set<Line> allocations_of_supply(final Line piedāvājuma) {
        return this.piešķiršanas.allocations_of_supply(piedāvājuma);
    }

    @Override
    public java.util.Set<Line> allocationsOf(final Line prasība, final Line piedāvājums) {
        return this.piešķiršanas.allocationsOf(prasība, piedāvājums);
    }

    @Override
    public java.util.Set<Line> allocations_of_demand(final Line prasība) {
        return this.piešķiršanas.allocations_of_demand(prasība);
    }

    @Override
    public java.util.Set<Line> supply_of_demand(final Line prasība) {
        return this.piešķiršanas.supply_of_demand(prasība);
    }

    @Override
    public Line addTranslated(List<?> vērtība) {
        return this.piešķiršanas.addTranslated(vērtība);
    }

    @Override
    public Line add(final Line rinda) {
        return this.piešķiršanas.add(rinda);
    }

    @Override
    public void remove(final int rindasIndekss) {
        this.piešķiršanas.remove(rindasIndekss);
    }

    @Override
    public void remove(final Line rinda) {
        this.piešķiršanas.remove(rinda);
    }

    @Override
    public void replace(final Line newRinda) {
        this.piešķiršanas.replace(newRinda);
    }

    @Override
    public <T extends AfterAdditionSubscriber & BeforeRemovalSubscriber> void synchronize(final T klausītājs) {
        this.piešķiršanas.<T>synchronize(klausītājs);
    }

    @Override
    public void subscribe_to_afterAddtions(final AfterAdditionSubscriber klausītājs) {
        this.piešķiršanas.subscribe_to_afterAddtions(klausītājs);
    }

    @Override
    public void subscriber_to_beforeRemoval(final BeforeRemovalSubscriber pirmsNoņemšanasKlausītājs) {
        this.piešķiršanas.subscriber_to_beforeRemoval(pirmsNoņemšanasKlausītājs);
    }

    @Override
    public void subscriber_to_afterRemoval(final BeforeRemovalSubscriber klausītājs) {
        this.piešķiršanas.subscriber_to_afterRemoval(klausītājs);
    }

    @Override
    public List<Attribute<Object>> headerView() {
        return this.piešķiršanas.headerView();
    }

    @Override
    public <T extends Object> ColumnView<T> columnView(final Attribute<T> atribūts) {
        return this.piešķiršanas.<T>columnView(atribūts);
    }

    @Override
    public List<Column<Object>> columnsView() {
        return this.piešķiršanas.columnsView();
    }

    @Deprecated
    public ListView<Line> rawLinesView() {
        return this.piešķiršanas.rawLinesView();
    }

    @Override
    public boolean contains(final Line rinda) {
        return this.piešķiršanas.contains(rinda);
    }

    @Override
    public List<Line> getLines() {
        return this.piešķiršanas.getLines();
    }

    @Override
    public Line getRawLines(final int indekss) {
        return this.piešķiršanas.getRawLines(indekss);
    }

    @Override
    public int size() {
        return this.piešķiršanas.size();
    }

    @Override
    public boolean isEmpty() {
        return this.piešķiršanas.isEmpty();
    }

    @Override
    public boolean hasContent() {
        return this.piešķiršanas.hasContent();
    }

    @Override
    public List<Line> rawLines() {
        return this.piešķiršanas.rawLines();
    }

    public String toCSV() {
        return this.piešķiršanas.toCSV();
    }

    @Override
    public Line lookupEquals(final Attribute<Line> atribūts, final Line other) {
        return this.piešķiršanas.lookupEquals(atribūts, other);
    }

    @Override
    public org.w3c.dom.Element toFods() {
        return this.piešķiršanas.toFods();
    }

    @Override
    public List<String> path() {
        return this.piešķiršanas.path();
    }

    public org.w3c.dom.Node toDom() {
        return this.piešķiršanas.toDom();
    }
}
