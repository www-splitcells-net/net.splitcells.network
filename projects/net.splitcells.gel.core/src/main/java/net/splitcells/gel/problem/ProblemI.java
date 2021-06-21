package net.splitcells.gel.problem;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.ListView;
import net.splitcells.gel.data.database.DatabaseSynchronization;
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
import net.splitcells.gel.rating.framework.MetaRating;
import net.splitcells.gel.solution.Solution;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.function.Function;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.gel.problem.derived.DerivedSolution.derivedSolution;

public class ProblemI implements Problem {

    private final Constraint constraint;
    private final Allocations allocations;
    protected Solution asSolution;

    public static Problem problem(Allocations allocations, Constraint constraint) {
        final var problem = new ProblemI(allocations, constraint);
        problem.synchronize(constraint);
        return problem;
    }

    protected ProblemI(Allocations allocations, Constraint constraint) {
        this.allocations = allocations;
        this.constraint = constraint;
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
    public Solution asSolution() {
        if (asSolution == null) {
            asSolution = Solutions.solution(this);
        }
        return asSolution;
    }

    @Override
    public DerivedSolution derived(Function<MetaRating, MetaRating> derivation) {
        return derivedSolution(() -> list(), allocations, constraint, derivation);
    }

    @Override
    public Database supplies() {
        return this.allocations.supplies();
    }

    @Override
    public Database suppliesUsed() {
        return this.allocations.suppliesUsed();
    }

    @Override
    public Database suppliesFree() {
        return this.allocations.suppliesFree();
    }

    @Override
    public Database demands() {
        return this.allocations.demands();
    }

    @Override
    public Database demandsUsed() {
        return this.allocations.demandsUsed();
    }

    @Override
    public Database demandsUnused() {
        return this.allocations.demandsUnused();
    }

    @Override
    public Line allocate(final Line demand, final Line supply) {
        return this.allocations.allocate(demand, supply);
    }

    @Override
    public Line demandOfAllocation(final Line allocation) {
        return this.allocations.demandOfAllocation(allocation);
    }

    @Override
    public Line supplyOfAllocation(final Line allocation) {
        return this.allocations.supplyOfAllocation(allocation);
    }

    @Override
    public java.util.Set<Line> allocationsOfSupply(final Line supply) {
        return this.allocations.allocationsOfSupply(supply);
    }

    @Override
    public java.util.Set<Line> allocationsOf(final Line demand, final Line supply) {
        return this.allocations.allocationsOf(demand, supply);
    }

    @Override
    public java.util.Set<Line> allocationsOfDemand(final Line demand) {
        return this.allocations.allocationsOfDemand(demand);
    }

    @Override
    public java.util.Set<Line> supply_of_demand(final Line demand) {
        return this.allocations.supply_of_demand(demand);
    }

    @Override
    public Line addTranslated(List<?> values) {
        return this.allocations.addTranslated(values);
    }

    @Override
    public Line add(final Line line) {
        return this.allocations.add(line);
    }

    @Deprecated
    @Override
    public void remove(final int allocationIndex) {
        this.allocations.remove(allocationIndex);
    }

    @Override
    public void remove(final Line line) {
        this.allocations.remove(line);
    }

    @Override
    public void replace(final Line newLine) {
        this.allocations.replace(newLine);
    }

    @Override
    public void synchronize(DatabaseSynchronization subscriber) {
        this.allocations.synchronize(subscriber);
    }

    @Override
    public void subscribeToAfterAdditions(final AfterAdditionSubscriber subscriber) {
        this.allocations.subscribeToAfterAdditions(subscriber);
    }

    @Override
    public void subscriberToBeforeRemoval(final BeforeRemovalSubscriber subscriber) {
        this.allocations.subscriberToBeforeRemoval(subscriber);
    }

    @Override
    public void subscriberToAfterRemoval(final BeforeRemovalSubscriber subscriber) {
        this.allocations.subscriberToAfterRemoval(subscriber);
    }

    @Override
    public List<Attribute<Object>> headerView() {
        return this.allocations.headerView();
    }

    @Override
    public <T extends Object> ColumnView<T> columnView(final Attribute<T> attribute) {
        return this.allocations.<T>columnView(attribute);
    }

    @Override
    public List<Column<Object>> columnsView() {
        return this.allocations.columnsView();
    }

    @Deprecated
    public ListView<Line> rawLinesView() {
        return this.allocations.rawLinesView();
    }

    @Override
    public boolean contains(final Line line) {
        return this.allocations.contains(line);
    }

    @Override
    public List<Line> getLines() {
        return this.allocations.getLines();
    }

    @Override
    public Line getRawLine(final int index) {
        return this.allocations.getRawLine(index);
    }

    @Override
    public int size() {
        return this.allocations.size();
    }

    @Override
    public boolean isEmpty() {
        return this.allocations.isEmpty();
    }

    @Override
    public boolean hasContent() {
        return this.allocations.hasContent();
    }

    @Override
    public List<Line> rawLines() {
        return this.allocations.rawLines();
    }

    public String toCSV() {
        return this.allocations.toCSV();
    }

    @Override
    public Line lookupEquals(final Attribute<Line> attribute, final Line value) {
        return this.allocations.lookupEquals(attribute, value);
    }

    @Override
    public Element toFods() {
        return this.allocations.toFods();
    }

    @Override
    public List<String> path() {
        return this.allocations.path();
    }

    public Node toDom() {
        return this.allocations.toDom();
    }
}
