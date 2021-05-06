package net.splitcells.gel.solution.history;

import static java.util.stream.IntStream.rangeClosed;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.utils.StreamUtils.reverse;
import static net.splitcells.gel.common.Language.*;
import static net.splitcells.gel.solution.history.event.Allocation.allocations;
import static net.splitcells.gel.solution.history.event.AllocationChangeType.ADDITION;
import static net.splitcells.gel.solution.history.event.AllocationChangeType.REMOVAL;
import static net.splitcells.gel.solution.history.meta.MetaDataI.metaData;
import static net.splitcells.gel.solution.history.meta.type.AllocationRating.allocationRating;
import static net.splitcells.gel.solution.history.meta.type.CompleteRating.completeRating;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.not;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.ListView;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.gel.data.database.Databases;
import net.splitcells.gel.solution.Solution;
import net.splitcells.gel.data.table.column.Column;
import net.splitcells.gel.data.table.column.ColumnView;
import net.splitcells.gel.data.allocation.Allocations;
import net.splitcells.gel.data.database.AfterAdditionSubscriber;
import net.splitcells.gel.data.database.Database;
import net.splitcells.gel.data.database.BeforeRemovalSubscriber;
import net.splitcells.gel.data.allocation.Allocationss;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.solution.history.meta.type.AllocationRating;
import net.splitcells.gel.solution.history.meta.type.CompleteRating;
import org.w3c.dom.Node;

import java.util.Set;

public class HistoryI implements History {

    private final Solution solution;
    private int lastEventId = -1;
    private Allocations allocations;

    protected HistoryI(Solution solution) {
        allocations = Allocationss.allocations
                (HISTORY.value()
                        , Databases.database
                                (EVENT.value()
                                        , () -> solution.path().withAppended(HISTORY.value())
                                        , ALLOCATION_ID, ALLOCATION_EVENT)
                        , Databases.database
                                (RESULT.value()
                                        , () -> solution.path().withAppended(HISTORY.value())
                                        , META_DATA));
        this.solution = solution;
        solution.subscribe_to_afterAdditions(this);
        solution.subscriber_to_beforeRemoval(this);
    }

    @Override
    public void register_addition(Line allocationValues) {
        final var metaData = metaData();
        metaData.with(CompleteRating.class
                , completeRating(solution.constraint().rating()));
        metaData.with(AllocationRating.class
                , allocationRating(solution.constraint().rating(allocationValues)));
        final Line allocation
                = demands().addTranslated(list(
                moveLastEventIdForward()
                , allocations(ADDITION
                        , solution.demandOfAllocation(allocationValues)
                        , solution.supplyOfAllocation(allocationValues))));
        allocations.allocate(allocation, this.supplies().addTranslated(list(metaData)));
    }

    @Override
    public void register_before_removal(Line removal) {
        final var metaData = metaData();
        metaData.with(CompleteRating.class
                , completeRating(solution.constraint().rating()));
        metaData.with(AllocationRating.class
                , allocationRating(solution.constraint().rating(removal)));
        final Line allocation
                = demands().addTranslated(list(
                moveLastEventIdForward()
                , allocations(REMOVAL
                        , solution.demandOfAllocation(removal)
                        , solution.supplyOfAllocation(removal))));
        allocations.allocate(allocation, this.supplies().addTranslated(list(metaData)));
    }

    protected Integer moveLastEventIdBackward() {
        return lastEventId -= 1;
    }

    protected Integer moveLastEventIdForward() {
        return lastEventId += 1;
    }

    @Override
    public void resetTo(int index) {
        final var indexToReversal = reverse
                (rangeClosed(index, this.size() - 1)
                        .boxed()
                        .filter(i -> i != -1)
                        .filter(i -> i != index)
                ).collect(Lists.toList());
        resetToInOrder(indexToReversal);
    }

    protected void resetToInOrder(List<Integer> index) {
        index.forEach(i -> resetLast());
    }

    protected void resetLast() {
        final var index = size() - 1;
        final var eventToRemove = columnView(ALLOCATION_ID)
                .lookup(index)
                .getLines(0)
                .value(ALLOCATION_EVENT);
        final var eventType = eventToRemove.tips();
        if (eventType.equals(ADDITION)) {
            final var allocation = solution.allocationsOf
                    (eventToRemove.demand().toLinePointer().interpret(solution.demands()).get()
                            , eventToRemove.supply().toLinePointer().interpret(solution.supplies()).get());
            assertThat(allocation).hasSize(1);
            allocation.forEach(e -> solution.remove(e));
        } else if (eventType.equals(REMOVAL)) {
            solution.allocate
                    (eventToRemove.demand().toLinePointer().interpret(solution.demands()).get()
                            , eventToRemove.supply().toLinePointer().interpret(solution.supplies()).get());
        } else {
            throw new UnsupportedOperationException();
        }
        resetLast_removal(index);
    }

    protected void resetLast_removal(int index) {
        removal_(columnView(ALLOCATION_ID).lookup(index + 1).getLines(0));
        removal_(columnView(ALLOCATION_ID).lookup(index).getLines(0));
    }

    protected void removal_(Line rinda) {
        allocations.remove(rinda);
        --lastEventId;
    }

    @Override
    public void remove(Line rinda) {
        throw notImplementedYet();
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
    public void subscriber_to_afterRemoval(BeforeRemovalSubscriber pirmsNoņemšanasKlausītājs) {
        allocations.subscriber_to_afterRemoval(pirmsNoņemšanasKlausītājs);
    }

    @Override
    public Line addTranslated(List<?> vertības) {
        throw notImplementedYet();
    }

    @Override
    public Line add(Line rinda) {
        throw notImplementedYet();
    }

    @Override
    public void remove(int indekss) {
        if (size() != indekss + 1) {
            throw notImplementedYet();
        }
        allocations.remove(rawLinesView().get(indekss));
    }

    @Override
    public int currentIndex() {
        return lastEventId;
    }

    @Override
    public Line allocate(Line prasība, Line piedāvājums) {
        throw notImplementedYet();
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
    public Line demandOfAllocation(Line piešķiršana) {
        return allocations.demandOfAllocation(piešķiršana);
    }

    @Override
    public Line supplyOfAllocation(Line piešķiršana) {
        return allocations.supplyOfAllocation(piešķiršana);
    }

    @Override
    public Set<Line> allocations_of_supply(Line piedāvājums) {
        return allocations.allocations_of_supply(piedāvājums);
    }

    @Override
    public Set<Line> allocations_of_demand(Line prasība) {
        return allocations.allocations_of_demand(prasība);
    }

    @Override
    public List<Attribute<Object>> headerView() {
        return allocations.headerView();
    }

    @Override
    public <T> ColumnView<T> columnView(Attribute<T> atribūts) {
        return allocations.columnView(atribūts);
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
    public Line lookupEquals(Attribute<Line> atribūts, Line cits) {
        return allocations.lookupEquals(atribūts, cits);
    }

    @Override
    public Node toDom() {
        throw notImplementedYet();
    }

    @Override
    public List<String> path() {
        return allocations.path();
    }
}
