package net.splitcells.gel.problem.derived;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.ListView;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.gel.solution.Solution;
import net.splitcells.gel.solution.history.History;
import net.splitcells.gel.solution.history.Histories;
import net.splitcells.gel.data.database.AfterAdditionSubscriber;
import net.splitcells.gel.data.allocation.Allocations;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.data.table.column.Column;
import net.splitcells.gel.data.table.column.ColumnView;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.constraint.type.Derivation;
import net.splitcells.gel.data.database.Database;
import net.splitcells.gel.data.database.BeforeRemovalSubscriber;
import net.splitcells.gel.rating.structure.MetaRating;
import org.w3c.dom.Node;

import java.util.Set;
import java.util.function.Function;

import static net.splitcells.dem.utils.Not_implemented_yet.not_implemented_yet;


public class DerivedSolution implements Solution {

    protected Constraint ierobežojums;
    private final Allocations piešķiršanas;
    private final History vēsture;
    private final Discoverable konteksts;
    
    public static DerivedSolution atvasinātaProblema(Discoverable konteksts, Allocations piešķiršanas, Constraint originalIerobežojums, Function<MetaRating, MetaRating> atvasinātsFunkcija) {
        return new DerivedSolution(konteksts, piešķiršanas, originalIerobežojums, Derivation.atvasināšana(originalIerobežojums, atvasinātsFunkcija));
    }

    protected DerivedSolution(Discoverable konteksts, Allocations piešķiršanas, Constraint oriģinālaisIerobežojums, Function<MetaRating, MetaRating> atvasinātsFunkcija) {
        this(konteksts, piešķiršanas, oriģinālaisIerobežojums, Derivation.atvasināšana(oriģinālaisIerobežojums, atvasinātsFunkcija));
    }

    public static DerivedSolution atvasinātsProblēma(Discoverable konteksts, Allocations piešķiršanas, Constraint ierobežojums, Constraint atvasināšana) {
        return new DerivedSolution(konteksts, piešķiršanas, ierobežojums, atvasināšana);
    }

    protected DerivedSolution(Discoverable konteksts, Allocations piešķiršanas, Constraint ierobežojums, Constraint atvasināšana) {
        this.piešķiršanas = piešķiršanas;
        this.ierobežojums = atvasināšana;
        vēsture = Histories.history(this);
        this.konteksts = konteksts;
    }

    protected DerivedSolution(Discoverable konteksts, Allocations piešķiršanas) {
        this.piešķiršanas = piešķiršanas;
        vēsture = Histories.history(this);
        this.konteksts = konteksts;
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
        throw not_implemented_yet();
    }

    @Override
    public DerivedSolution derived(Function<MetaRating, MetaRating> atvasināšana) {
        throw not_implemented_yet();
    }

    @Override
    public Database supplies() {
        return piešķiršanas.supplies();
    }

    @Override
    public Database supplies_used() {
        return piešķiršanas.supplies_used();
    }

    @Override
    public Database supplies_free() {
        return piešķiršanas.supplies_free();
    }

    @Override
    public Database demands() {
        return piešķiršanas.demands();
    }

    @Override
    public Database demands_used() {
        return piešķiršanas.demands_used();
    }

    @Override
    public Database demands_unused() {
        return piešķiršanas.demands_unused();
    }

    @Override
    public Line allocate(Line prasība, Line piedāvājums) {
        return piešķiršanas.allocate(prasība, piedāvājums);
    }

    @Override
    public Line demand_of_allocation(Line piešķiršana) {
        return piešķiršanas.demand_of_allocation(piešķiršana);
    }

    @Override
    public Line supply_of_allocation(Line piešķiršana) {
        return piešķiršanas.supply_of_allocation(piešķiršana);
    }

    @Override
    public Set<Line> allocations_of_supply(Line piedāvājums) {
        return piešķiršanas.allocations_of_supply(piedāvājums);
    }

    @Override
    public Set<Line> allocations_of_demand(Line prasība) {
        return piešķiršanas.allocations_of_demand(prasība);
    }

    @Override
    public Line addTranslated(List<?> vērtība) {
        return piešķiršanas.addTranslated(vērtība);
    }

    @Override
    public Line add(Line rinda) {
        return piešķiršanas.add(rinda);
    }

    @Override
    public void remove(int lineIndex) {
        piešķiršanas.remove(lineIndex);
    }

    @Override
    public void remove(Line rinda) {
        piešķiršanas.remove(rinda);
    }

    @Override
    public void subscribe_to_afterAddtions(AfterAdditionSubscriber klausītājs) {
        piešķiršanas.subscribe_to_afterAddtions(klausītājs);
    }

    @Override
    public void subscriber_to_beforeRemoval(BeforeRemovalSubscriber pirmsNoņemšanasKlausītājs) {
        piešķiršanas.subscriber_to_beforeRemoval(pirmsNoņemšanasKlausītājs);
    }

    @Override
    public void subscriber_to_afterRemoval(BeforeRemovalSubscriber klausītājs) {
        piešķiršanas.subscriber_to_afterRemoval(klausītājs);
    }

    @Override
    public List<Attribute<Object>> headerView() {
        return piešķiršanas.headerView();
    }

    @Override
    public <T> ColumnView<T> columnView(Attribute<T> atribūts) {
        return piešķiršanas.columnView(atribūts);
    }

    @Override
    public List<Column<Object>> columnsView() {
        return piešķiršanas.columnsView();
    }

    @Override
    public ListView<Line> rawLinesView() {
        return piešķiršanas.rawLinesView();
    }

    @Override
    public int size() {
        return piešķiršanas.size();
    }

    @Override
    public List<Line> rawLines() {
        return piešķiršanas.rawLines();
    }

    @Override
    public Line lookupEquals(Attribute<Line> atribūts, Line other) {
        return piešķiršanas.lookupEquals(atribūts, other);
    }

    @Override
    public Node toDom() {
        throw not_implemented_yet();
    }

    @Override
    public net.splitcells.dem.data.set.list.List<String> path() {
        return konteksts.path().withAppended(DerivedSolution.class.getSimpleName());
    }

    @Override
    public History history() {
        return vēsture;
    }
}
