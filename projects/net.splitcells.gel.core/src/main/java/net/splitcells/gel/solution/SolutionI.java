package net.splitcells.gel.solution;

import net.splitcells.dem.utils.NotImplementedYet;
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

import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;

public class SolutionI implements Solution {
	private final Problem problem;
	private final History history;
	
	public static Solution solution(Problem problem) {
		return new SolutionI(problem);
	}

	public SolutionI(Problem problem) {
		this.problem = problem;
		history = Histories.history(this);
	}

	@Override
	public boolean isComplete() {
		return demands_unused().size() == 0
				|| demands().size() < supplies().size()
				&& demands_used().size() == supplies().size();
	}

	public boolean balanced() {
		return demands().rawLinesView().size() == supplies().rawLinesView().size();
	}

	public boolean tolerable(final long error_threshold) {
		throw new NotImplementedYet();
	}

	@Override
	public History history() {
		return history;
	}

	@java.lang.SuppressWarnings("all")
	public Constraint constraint() {
		return this.problem.constraint();
	}

	@Override
	public Allocations allocations() {
		return problem.allocations();
	}

	@java.lang.SuppressWarnings("all")
	public Solution toSolution() {
		return this.problem.toSolution();
	}

	@java.lang.SuppressWarnings("all")
	public Solution asSolution() {
		return this.problem.asSolution();
	}

	@Override
	public DerivedSolution derived(Function<MetaRating, MetaRating> derivation) {
		return problem.derived(derivation);
	}

	@java.lang.SuppressWarnings("all")
	public Database supplies() {
		return this.problem.supplies();
	}

	@java.lang.SuppressWarnings("all")
	public Database supplies_used() {
		return this.problem.supplies_used();
	}

	@java.lang.SuppressWarnings("all")
	public Database supplies_free() {
		return this.problem.supplies_free();
	}

	@java.lang.SuppressWarnings("all")
	public Database demands() {
		return this.problem.demands();
	}

	@java.lang.SuppressWarnings("all")
	public Database demands_used() {
		return this.problem.demands_used();
	}

	@java.lang.SuppressWarnings("all")
	public Database demands_unused() {
		return this.problem.demands_unused();
	}

	@java.lang.SuppressWarnings("all")
	public Line allocate(final Line demand, final Line supply) {
		return this.problem.allocate(demand, supply);
	}

	@java.lang.SuppressWarnings("all")
	public Line demandOfAllocation(final Line allocation) {
		return this.problem.demandOfAllocation(allocation);
	}

	@java.lang.SuppressWarnings("all")
	public Line supplyOfAllocation(final Line allocation) {
		return this.problem.supplyOfAllocation(allocation);
	}

	@java.lang.SuppressWarnings("all")
	public java.util.Set<Line> allocations_of_supply(final Line supply) {
		return this.problem.allocations_of_supply(supply);
	}

	@java.lang.SuppressWarnings("all")
	public java.util.Set<Line> allocationsOf(final Line demand, final Line supply) {
		return this.problem.allocationsOf(demand, supply);
	}

	@java.lang.SuppressWarnings("all")
	public java.util.Set<Line> allocations_of_demand(final Line demand) {
		return this.problem.allocations_of_demand(demand);
	}

	@java.lang.SuppressWarnings("all")
	public java.util.Set<Line> supply_of_demand(final Line demand) {
		return this.problem.supply_of_demand(demand);
	}

	@java.lang.SuppressWarnings("all")
	public Line addTranslated(final List<?> values) {
		return this.problem.addTranslated(values);
	}

	@java.lang.SuppressWarnings("all")
	public Line add(final Line line) {
		return this.problem.add(line);
	}

	@java.lang.SuppressWarnings("all")
	public void remove(final int lineIndex) {
		this.problem.remove(lineIndex);
	}

	@java.lang.SuppressWarnings("all")
	public void remove(final Line line) {
		this.problem.remove(line);
	}

	@java.lang.SuppressWarnings("all")
	public void replace(final Line newLine) {
		this.problem.replace(newLine);
	}

	@java.lang.SuppressWarnings("all")
	public <T extends AfterAdditionSubscriber & BeforeRemovalSubscriber> void synchronize(final T subscriber) {
		this.problem.synchronize(subscriber);
	}

	@java.lang.SuppressWarnings("all")
	public void subscribe_to_afterAdditions(final AfterAdditionSubscriber subscriber) {
		this.problem.subscribe_to_afterAdditions(subscriber);
	}

	@java.lang.SuppressWarnings("all")
	public void subscriber_to_beforeRemoval(final BeforeRemovalSubscriber subscriber) {
		this.problem.subscriber_to_beforeRemoval(subscriber);
	}

	@java.lang.SuppressWarnings("all")
	public void subscriber_to_afterRemoval(final BeforeRemovalSubscriber listener) {
		this.problem.subscriber_to_afterRemoval(listener);
	}

	@java.lang.SuppressWarnings("all")
	public List<Attribute<Object>> headerView() {
		return this.problem.headerView();
	}

	@java.lang.SuppressWarnings("all")
	public <T extends Object> ColumnView<T> columnView(final Attribute<T> atribūts) {
		return this.problem.<T>columnView(atribūts);
	}

	@java.lang.SuppressWarnings("all")
	public List<Column<Object>> columnsView() {
		return this.problem.columnsView();
	}

	@Deprecated
	@java.lang.SuppressWarnings("all")
	public ListView<Line> rawLinesView() {
		return this.problem.rawLinesView();
	}

	@java.lang.SuppressWarnings("all")
	public boolean contains(final Line line) {
		return this.problem.contains(line);
	}

	@java.lang.SuppressWarnings("all")
	public List<Line> getLines() {
		return this.problem.getLines();
	}

	@java.lang.SuppressWarnings("all")
	public Line getRawLine(final int index) {
		return this.problem.getRawLine(index);
	}

	@java.lang.SuppressWarnings("all")
	public int size() {
		return this.problem.size();
	}

	@java.lang.SuppressWarnings("all")
	public boolean isEmpty() {
		return this.problem.isEmpty();
	}

	@java.lang.SuppressWarnings("all")
	public boolean hasContent() {
		return this.problem.hasContent();
	}

	@java.lang.SuppressWarnings("all")
	public List<Line> rawLines() {
		return this.problem.rawLines();
	}

	@java.lang.SuppressWarnings("all")
	public java.lang.String toCSV() {
		return this.problem.toCSV();
	}

	@java.lang.SuppressWarnings("all")
	public Line lookupEquals(final Attribute<Line> atribūts, final Line cits) {
		return this.problem.lookupEquals(atribūts, cits);
	}

	@java.lang.SuppressWarnings("all")
	public org.w3c.dom.Element toFods() {
		return this.problem.toFods();
	}

	@java.lang.SuppressWarnings("all")
	public List<String> path() {
		return this.problem.path();
	}

	@java.lang.SuppressWarnings("all")
	public org.w3c.dom.Node toDom() {
		return this.problem.toDom();
	}

	@Override
	public String toString() {
		return path().toString();
	}
}
