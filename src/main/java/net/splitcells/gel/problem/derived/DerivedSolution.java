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
import net.splitcells.gel.data.database.Database;
import net.splitcells.gel.data.database.BeforeRemovalSubscriber;
import net.splitcells.gel.rating.structure.MetaRating;
import org.w3c.dom.Node;

import java.util.Set;
import java.util.function.Function;

import static net.splitcells.dem.utils.Not_implemented_yet.not_implemented_yet;
import static net.splitcells.gel.constraint.type.Derivation.derivation;


public class DerivedSolution implements Solution {

    protected Constraint constraint;
    private final Allocations allocations;
    private final History history;
    private final Discoverable contexts;

    public static DerivedSolution derivedSolution(Discoverable context, Allocations allocations
            , Constraint originalConstraint, Function<MetaRating, MetaRating> derivation) {
        return new DerivedSolution(context, allocations, originalConstraint
                , derivation(originalConstraint, derivation));
    }

    public static DerivedSolution derivedSolution(Discoverable context, Allocations allocations
            , Constraint constraint, Constraint derivation) {
        return new DerivedSolution(context, allocations, constraint, derivation);
    }

    protected DerivedSolution(Discoverable context, Allocations allocations, Constraint originalConstraints
            , Function<MetaRating, MetaRating> derivation) {
        this(context, allocations, originalConstraints, derivation(originalConstraints, derivation));
    }

    protected DerivedSolution(Discoverable context, Allocations allocations, Constraint constraint
            , Constraint derivation) {
        this.allocations = allocations;
        this.constraint = derivation;
        this.history = Histories.history(this);
        this.contexts = context;
    }

    protected DerivedSolution(Discoverable contexts, Allocations allocations) {
        this.allocations = allocations;
        history = Histories.history(this);
        this.contexts = contexts;
    }

    @Override
    public Constraint constraint() {
        return constraint;
    }

    @Override
    public Allocations allocations() {
        return allocations;
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
    public DerivedSolution derived(Function<MetaRating, MetaRating> derivation) {
        throw not_implemented_yet();
    }

    @Override
    public Database supplies() {
        return allocations.supplies();
    }

    @Override
    public Database supplies_used() {
        return allocations.supplies_used();
    }

    @Override
    public Database supplies_free() {
        return allocations.supplies_free();
    }

    @Override
    public Database demands() {
        return allocations.demands();
    }

    @Override
    public Database demands_used() {
        return allocations.demands_used();
    }

    @Override
    public Database demands_unused() {
        return allocations.demands_unused();
    }

    @Override
    public Line allocate(Line demand, Line supply) {
        return allocations.allocate(demand, supply);
    }

    @Override
    public Line demand_of_allocation(Line allocation) {
        return allocations.demand_of_allocation(allocation);
    }

    @Override
    public Line supply_of_allocation(Line allocation) {
        return allocations.supply_of_allocation(allocation);
    }

    @Override
    public Set<Line> allocations_of_supply(Line supply) {
        return allocations.allocations_of_supply(supply);
    }

    @Override
    public Set<Line> allocations_of_demand(Line demand) {
        return allocations.allocations_of_demand(demand);
    }

    @Override
    public Line addTranslated(List<?> values) {
        return allocations.addTranslated(values);
    }

    @Override
    public Line add(Line line) {
        return allocations.add(line);
    }

    @Override
    public void remove(int lineIndex) {
        allocations.remove(lineIndex);
    }

    @Override
    public void remove(Line line) {
        allocations.remove(line);
    }

    @Override
    public void subscribe_to_afterAdditions(AfterAdditionSubscriber subscriber) {
        allocations.subscribe_to_afterAdditions(subscriber);
    }

    @Override
    public void subscriber_to_beforeRemoval(BeforeRemovalSubscriber subscriber) {
        allocations.subscriber_to_beforeRemoval(subscriber);
    }

    @Override
    public void subscriber_to_afterRemoval(BeforeRemovalSubscriber subscriber) {
        allocations.subscriber_to_afterRemoval(subscriber);
    }

    @Override
    public List<Attribute<Object>> headerView() {
        return allocations.headerView();
    }

    @Override
    public <T> ColumnView<T> columnView(Attribute<T> attribute) {
        return allocations.columnView(attribute);
    }

    @Override
    public List<Column<Object>> columnsView() {
        return allocations.columnsView();
    }

    @Override
    public ListView<Line> rawLinesView() {
        return allocations.rawLinesView();
    }

    @Override
    public int size() {
        return allocations.size();
    }

    @Override
    public List<Line> rawLines() {
        return allocations.rawLines();
    }

    @Override
    public Line lookupEquals(Attribute<Line> attribute, Line other) {
        return allocations.lookupEquals(attribute, other);
    }

    @Override
    public Node toDom() {
        throw not_implemented_yet();
    }

    @Override
    public List<String> path() {
        return contexts.path().withAppended(DerivedSolution.class.getSimpleName());
    }

    @Override
    public History history() {
        return history;
    }
}
