package net.splitcells.gel.solution;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.ListView;
import net.splitcells.dem.environment.config.StaticFlags;
import net.splitcells.dem.lang.annotations.Returns_this;
import net.splitcells.dem.resource.host.interaction.LogLevel;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.data.allocation.Allocations;
import net.splitcells.gel.data.database.AfterAdditionSubscriber;
import net.splitcells.gel.data.database.BeforeRemovalSubscriber;
import net.splitcells.gel.data.database.Database;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.data.table.column.Column;
import net.splitcells.gel.data.table.column.ColumnView;
import net.splitcells.gel.problem.derived.DerivedSolution;
import net.splitcells.gel.rating.framework.MetaRating;
import net.splitcells.gel.rating.framework.Rating;
import net.splitcells.gel.solution.history.History;
import net.splitcells.gel.solution.optimization.OptimizationEvent;
import org.w3c.dom.Node;

import java.util.Set;
import java.util.function.Function;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.resource.host.interaction.Domsole.domsole;
import static net.splitcells.gel.common.Language.RATING;
import static net.splitcells.gel.solution.optimization.StepType.REMOVAL;
import static org.assertj.core.api.Assertions.assertThat;

public class SolutionAspect implements Solution {
    public static SolutionAspect solutionAspect(Solution solution) {
        return new SolutionAspect(solution);
    }

    private final Solution solution;

    private SolutionAspect(Solution solution) {
        this.solution = solution;
    }

    @Override
    public Solution optimize(List<OptimizationEvent> events) {
        final var solution = Solution.super.optimize(events);
        if (StaticFlags.TRACING) {
            domsole().append
                    (constraint().rating()
                            , () -> solution.path().withAppended(RATING.value())
                            , LogLevel.TRACE);
        }
        return solution;
    }

    @Override
    public Rating rating(List<OptimizationEvent> event) {
        final var rating = Solution.super.rating(event);
        if (StaticFlags.TRACING) {
            domsole().append
                    (rating
                            , () -> solution.path().withAppended(RATING.value())
                            , LogLevel.TRACE);
        }
        return rating;
    }

    @Returns_this
    public Solution optimize(OptimizationEvent event, OptimizationParameters parameters) {
        if (event.demand().interpret().isEmpty()) {
            throw new IllegalArgumentException("Unknown demand: " + event.demand().index() + ", " + event.demand().context().path());
        }
        if (event.supply().interpret().isEmpty()) {
            throw new IllegalArgumentException("Unknown supply: " + event.supply().index() + ", " + event.supply().context().path());
        }
        if (event.stepType().equals(REMOVAL)) {
            event.demand()
                    .interpret()
                    .ifPresent(demandsForRemoval
                            -> assertThat(list(demandsForRemoval.context()))
                            .containsAnyOf(solution.demands(), solution.demandsUsed()));
            event.supply()
                    .interpret()
                    .ifPresent(supplyForRemoval
                            -> assertThat(list(supplyForRemoval.context()))
                            .containsAnyOf(solution.supplies(), solution.suppliesUsed()));
        }
        return solution.optimize(event, parameters);
    }

    @Override
    public History history() {
        return solution.history();
    }

    @Override
    public Solution toSolution() {
        return solution.toSolution();
    }

    @Override
    public Solution asSolution() {
        return solution.asSolution();
    }

    @Override
    public Line allocate(Line demand, Line supply) {
        return solution.allocate(demand, supply);
    }

    @Override
    public Line addTranslated(List<?> values) {
        return solution.addTranslated(values);
    }

    @Override
    public Line add(Line line) {
        return solution.add(line);
    }

    @Override
    public void remove(int lineIndex) {
        solution.remove(lineIndex);
    }

    @Override
    public void remove(Line line) {
        solution.remove(line);
    }

    @Override
    public void subscribeToAfterAdditions(AfterAdditionSubscriber subscriber) {
        solution.subscribeToAfterAdditions(subscriber);
    }

    @Override
    public void subscriberToBeforeRemoval(BeforeRemovalSubscriber subscriber) {
        solution.subscriberToBeforeRemoval(subscriber);
    }

    @Override
    public void subscriberToAfterRemoval(BeforeRemovalSubscriber subscriber) {
        solution.subscriberToAfterRemoval(subscriber);
    }

    @Override
    public Constraint constraint() {
        return solution.constraint();
    }

    @Override
    public Allocations allocations() {
        return solution.allocations();
    }

    @Override
    public DerivedSolution derived(Function<MetaRating, MetaRating> derivation) {
        return solution.derived(derivation);
    }

    @Override
    public Database supplies() {
        return solution.supplies();
    }

    @Override
    public Database suppliesUsed() {
        return solution.suppliesUsed();
    }

    @Override
    public Database suppliesFree() {
        return solution.suppliesFree();
    }

    @Override
    public Database demands() {
        return solution.demands();
    }

    @Override
    public Database demandsUsed() {
        return solution.demandsUsed();
    }

    @Override
    public Database demandsUnused() {
        return solution.demandsUnused();
    }

    @Override
    public Line demandOfAllocation(Line allocation) {
        return solution.demandOfAllocation(allocation);
    }

    @Override
    public Line supplyOfAllocation(Line allocation) {
        return solution.supplyOfAllocation(allocation);
    }

    @Override
    public Set<Line> allocationsOfSupply(Line supply) {
        return solution.allocationsOfSupply(supply);
    }

    @Override
    public Set<Line> allocationsOfDemand(Line demand) {
        return solution.allocationsOfDemand(demand);
    }

    @Override
    public List<Attribute<Object>> headerView() {
        return solution.headerView();
    }

    @Override
    public <T> ColumnView<T> columnView(Attribute<T> attribute) {
        return solution.columnView(attribute);
    }

    @Override
    public List<Column<Object>> columnsView() {
        return solution.columnsView();
    }

    @Override
    public ListView<Line> rawLinesView() {
        return solution.rawLinesView();
    }

    @Override
    public int size() {
        return solution.size();
    }

    @Override
    public List<Line> rawLines() {
        return solution.rawLines();
    }

    @Override
    public Line lookupEquals(Attribute<Line> attribute, Line value) {
        return solution.lookupEquals(attribute, value);
    }

    @Override
    public Node toDom() {
        return solution.toDom();
    }

    @Override
    public List<String> path() {
        return solution.path();
    }
}
