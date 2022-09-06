/*
 * Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License,
 * which is available at https://spdx.org/licenses/MIT.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 */
package net.splitcells.gel.solution.history;

import static java.util.stream.IntStream.rangeClosed;
import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.dem.environment.config.StaticFlags.ENFORCING_UNIT_CONSISTENCY;
import static net.splitcells.dem.lang.perspective.Perspective.toStringPathsDescription;
import static net.splitcells.dem.lang.perspective.PerspectiveI.perspective;
import static net.splitcells.dem.utils.ExecutionException.executionException;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.utils.StreamUtils.reverse;
import static net.splitcells.gel.common.Language.*;
import static net.splitcells.gel.solution.history.event.Allocation.allocations;
import static net.splitcells.gel.solution.history.event.AllocationChangeType.ADDITION;
import static net.splitcells.gel.solution.history.event.AllocationChangeType.REMOVAL;
import static net.splitcells.gel.solution.history.meta.MetaDataI.metaData;
import static net.splitcells.gel.solution.history.meta.type.AllocationNaturalArgumentation.allocationNaturalArgumentation;
import static net.splitcells.gel.solution.history.meta.type.AllocationRating.allocationRating;
import static net.splitcells.gel.solution.history.meta.type.CompleteRating.completeRating;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.not;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.ListView;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.gel.data.database.Databases;
import net.splitcells.gel.data.table.LinePointer;
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
import net.splitcells.gel.solution.history.meta.type.AllocationNaturalArgumentation;
import net.splitcells.gel.solution.history.meta.type.AllocationRating;
import net.splitcells.gel.solution.history.meta.type.CompleteRating;
import org.w3c.dom.Node;

import java.util.function.Supplier;

/**
 * TODO Limit or disable meta data in order to improve runtime performance.
 */
public class HistoryI implements History {

    private final Solution solution;
    private int lastEventId = -1;
    private Allocations allocations;
    private boolean isRegisterEventIsEnabled = false;
    private boolean isHistoryConsistent = false;
    private boolean synchronizes = false;

    protected HistoryI(Solution solution) {
        allocations = Allocationss.allocations
                (HISTORY.value()
                        , Databases.database
                                (EVENT.value()
                                        , () -> solution.path().withAppended(HISTORY.value())
                                        , list(ALLOCATION_ID, ALLOCATION_EVENT))
                        , Databases.database
                                (RESULT.value()
                                        , () -> solution.path().withAppended(HISTORY.value())
                                        , list(META_DATA)));
        this.solution = solution;
        solution.subscribeToAfterAdditions(this);
        solution.subscribeToBeforeRemoval(this);
    }

    @Override
    public void registerAddition(Line allocationValues) {
        if (isRegisterEventIsEnabled) {
            final var metaData = metaData();
            metaData.with(CompleteRating.class
                    , completeRating(solution.constraint().rating()));
            metaData.with(AllocationRating.class
                    , allocationRating(solution.constraint().rating(allocationValues)));
            {
                final var naturalArgumentation = solution.constraint()
                        .naturalArgumentation(allocationValues, solution.constraint().injectionGroup());
                if (naturalArgumentation.isPresent()) {
                    metaData.with(AllocationNaturalArgumentation.class
                            , allocationNaturalArgumentation(
                                    toStringPathsDescription(naturalArgumentation.get().toStringPaths())));
                }
            }
            final Line allocation
                    = demands().addTranslated(list(
                    moveLastEventIdForward()
                    , allocations(ADDITION
                            , solution.demandOfAllocation(allocationValues)
                            , solution.supplyOfAllocation(allocationValues))));
            allocations.allocate(allocation, this.supplies().addTranslated(list(metaData)));
        } else {
            if (!synchronizes) {
                isHistoryConsistent = true;
            }
        }
    }

    @Override
    public void registerBeforeRemoval(Line removal) {
        if (isRegisterEventIsEnabled) {
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
        } else {
            if (!synchronizes) {
                isHistoryConsistent = true;
            }
        }
    }

    private Integer moveLastEventIdBackwards() {
        return lastEventId -= 1;
    }

    private Integer moveLastEventIdForward() {
        return lastEventId += 1;
    }

    @Override
    public void resetTo(int index) {
        if (!isRegisterEventIsEnabled) {
            throw executionException("History is disabled.");
        }
        if (ENFORCING_UNIT_CONSISTENCY) {
            assertThat(index).isGreaterThanOrEqualTo(-1);
            if (allocations.size() > 0) {
                assertThat(index).isLessThanOrEqualTo(allocations.size() - 1);
            }
        }
        if (allocations.size() == 0 && index == -1) {
            return;
        }
        if (allocations.size() - 1 == index) {
            return;
        }
        if (allocations.size() == 0) {
            if (ENFORCING_UNIT_CONSISTENCY) {
                assertThat(index).isEqualTo(-1);
            }
            return;
        }
        isRegisterEventIsEnabled = false;
        /**
         * Omit unnecessary allocations to {@link #allocations},
         * when allocations are removed from {@link #solution} during reset.
         */
        final var indexToReversal = reverse
                (rangeClosed(index, allocations.size() - 1)
                        .boxed()
                        .filter(i -> i != -1)
                        .filter(i -> i != index)
                ).collect(toList());
        resetToInOrder(indexToReversal);
        isRegisterEventIsEnabled = true;
    }

    @Override
    public void processWithoutHistory(Runnable runnable) {
        runnable.run();
    }

    @Override
    public void processWithHistory(Runnable runnable) {
        isRegisterEventIsEnabled = true;
        runnable.run();
    }

    @Override
    public <T> T supplyWithHistory(Supplier<T> supplier) {
        isRegisterEventIsEnabled = true;
        return supplier.get();
    }

    private void resetToInOrder(List<Integer> indexes) {
        if (ENFORCING_UNIT_CONSISTENCY) {
            if (allocations.size() != 0) {
                assertThat(indexes).isNotEmpty();
            } else {
                assertThat(indexes).isEmpty();
            }
        }
        indexes.forEach(i -> resetLast());
    }

    private void resetLast() {
        synchronizes = true;
        if (ENFORCING_UNIT_CONSISTENCY) {
            assertThat(allocations.columnView(ALLOCATION_ID).lookup(allocations.size() - 1).size()).isEqualTo(1);
        }
        final var index = allocations.size() - 1;
        final var eventToRemove = allocations.columnView(ALLOCATION_ID)
                .lookup(index)
                .line(0)
                .value(ALLOCATION_EVENT);
        final var eventType = eventToRemove.type();
        if (eventType.equals(ADDITION)) {
            solution.remove(solution.allocationOf
                    (eventToRemove.demand().toLinePointer()
                            , eventToRemove.supply().toLinePointer()));
        } else if (eventType.equals(REMOVAL)) {
            solution.allocate
                    (eventToRemove.demand().toLinePointer().interpret(solution.demands()).get()
                            , eventToRemove.supply().toLinePointer().interpret(solution.supplies()).get());
        } else {
            throw new UnsupportedOperationException();
        }
        resetLastRemoval(index);
        synchronizes = false;
    }

    private void resetLastRemoval(int index) {
        if (ENFORCING_UNIT_CONSISTENCY) {
            assertThat(allocations.size() - 1).isEqualTo(index);
            try {
                assertThat(allocations.columnView(ALLOCATION_ID).lookup(index + 1).size()).isEqualTo(0);
                assertThat(allocations.columnView(ALLOCATION_ID).lookup(index).size()).isEqualTo(1);
            } catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }
        removal_(allocations.columnView(ALLOCATION_ID).lookup(index).line(0));
    }

    /**
     * TODO FIX Remove unused demands and supply.
     * <p/>
     * TODO Automatically remove allocations from solution, via subscription.
     *
     * @param line
     */
    private void removal_(Line line) {
        allocations.remove(line);
        --lastEventId;
    }

    @Override
    public void remove(Line line) {
        if (!isRegisterEventIsEnabled) {
            throw executionException("History is disabled.");
        }
        if (isHistoryConsistent) {
            throw executionException("History is inconsistent.");
        }
        throw notImplementedYet();
    }

    @Override
    public void subscribeToAfterAdditions(AfterAdditionSubscriber subscriber) {
        allocations.subscribeToAfterAdditions(subscriber);
    }

    @Override
    public void subscribeToBeforeRemoval(BeforeRemovalSubscriber subscriber) {
        allocations.subscribeToBeforeRemoval(subscriber);
    }

    @Override
    public void subscribeToAfterRemoval(BeforeRemovalSubscriber beforeRemovalSubscriber) {
        allocations.subscribeToAfterRemoval(beforeRemovalSubscriber);
    }

    @Override
    public Line addTranslated(List<?> values) {
        if (!isRegisterEventIsEnabled) {
            throw executionException("History is disabled.");
        }
        if (isHistoryConsistent) {
            throw executionException("History is inconsistent.");
        }
        throw notImplementedYet();
    }

    @Override
    public Line add(Line line) {
        if (!isRegisterEventIsEnabled) {
            throw executionException("History is disabled.");
        }
        if (isHistoryConsistent) {
            throw executionException("History is inconsistent.");
        }
        throw notImplementedYet();
    }

    @Override
    public void remove(int indexes) {
        if (!isRegisterEventIsEnabled) {
            throw executionException("History is disabled.");
        }
        if (isHistoryConsistent) {
            throw executionException("History is inconsistent.");
        }
        if (size() != indexes + 1) {
            throw notImplementedYet();
        }
        allocations.remove(rawLinesView().get(indexes));
    }

    @Override
    public int currentIndex() {
        if (!isRegisterEventIsEnabled) {
            throw executionException("History is disabled.");
        }
        if (isHistoryConsistent) {
            throw executionException("History is inconsistent.");
        }
        return lastEventId;
    }

    @Override
    public Line allocate(Line demand, Line supply) {
        if (!isRegisterEventIsEnabled) {
            throw executionException("History is disabled.");
        }
        if (isHistoryConsistent) {
            throw executionException("History is inconsistent.");
        }
        throw notImplementedYet();
    }

    @Override
    public Line allocationOf(LinePointer demand, LinePointer supply) {
        if (!isRegisterEventIsEnabled) {
            throw executionException("History is disabled.");
        }
        if (isHistoryConsistent) {
            throw executionException("History is inconsistent.");
        }
        return this.allocations.allocationOf(demand, supply);
    }

    @Override
    public Database supplies() {
        if (!isRegisterEventIsEnabled) {
            throw executionException("History is disabled.");
        }
        if (isHistoryConsistent) {
            throw executionException("History is inconsistent.");
        }
        return allocations.supplies();
    }

    @Override
    public Database suppliesUsed() {
        if (!isRegisterEventIsEnabled) {
            throw executionException("History is disabled.");
        }
        if (isHistoryConsistent) {
            throw executionException("History is inconsistent.");
        }
        return allocations.suppliesUsed();
    }

    @Override
    public Database suppliesFree() {
        if (!isRegisterEventIsEnabled) {
            throw executionException("History is disabled.");
        }
        if (isHistoryConsistent) {
            throw executionException("History is inconsistent.");
        }
        return allocations.suppliesFree();
    }

    @Override
    public Database demands() {
        if (!isRegisterEventIsEnabled) {
            throw executionException("History is disabled.");
        }
        if (isHistoryConsistent) {
            throw executionException("History is inconsistent.");
        }
        return allocations.demands();
    }

    @Override
    public Database demandsUsed() {
        if (!isRegisterEventIsEnabled) {
            throw executionException("History is disabled.");
        }
        if (isHistoryConsistent) {
            throw executionException("History is inconsistent.");
        }
        return allocations.demandsUsed();
    }

    @Override
    public Database demandsFree() {
        if (!isRegisterEventIsEnabled) {
            throw executionException("History is disabled.");
        }
        if (isHistoryConsistent) {
            throw executionException("History is inconsistent.");
        }
        return allocations.demandsFree();
    }

    @Override
    public Line demandOfAllocation(Line allocation) {
        if (!isRegisterEventIsEnabled) {
            throw executionException("History is disabled.");
        }
        if (isHistoryConsistent) {
            throw executionException("History is inconsistent.");
        }
        return allocations.demandOfAllocation(allocation);
    }

    @Override
    public Line supplyOfAllocation(Line allocation) {
        if (!isRegisterEventIsEnabled) {
            throw executionException("History is disabled.");
        }
        if (isHistoryConsistent) {
            throw executionException("History is inconsistent.");
        }
        return allocations.supplyOfAllocation(allocation);
    }

    @Override
    public Set<Line> allocationsOfSupply(Line supply) {
        if (!isRegisterEventIsEnabled) {
            throw executionException("History is disabled.");
        }
        if (isHistoryConsistent) {
            throw executionException("History is inconsistent.");
        }
        return allocations.allocationsOfSupply(supply);
    }

    @Override
    public Set<Line> allocationsOfDemand(Line demand) {
        if (!isRegisterEventIsEnabled) {
            throw executionException("History is disabled.");
        }
        if (isHistoryConsistent) {
            throw executionException("History is inconsistent.");
        }
        return allocations.allocationsOfDemand(demand);
    }

    @Override
    public List<Attribute<Object>> headerView() {
        if (!isRegisterEventIsEnabled) {
            throw executionException("History is disabled.");
        }
        if (isHistoryConsistent) {
            throw executionException("History is inconsistent.");
        }
        return allocations.headerView();
    }

    @Override
    public <T> ColumnView<T> columnView(Attribute<T> attribute) {
        if (!isRegisterEventIsEnabled) {
            throw executionException("History is disabled.");
        }
        if (isHistoryConsistent) {
            throw executionException("History is inconsistent.");
        }
        return allocations.columnView(attribute);
    }

    @Override
    public List<Column<Object>> columnsView() {
        if (!isRegisterEventIsEnabled) {
            throw executionException("History is disabled.");
        }
        if (isHistoryConsistent) {
            throw executionException("History is inconsistent.");
        }
        return allocations.columnsView();
    }

    @Override
    public ListView<Line> rawLinesView() {
        if (!isRegisterEventIsEnabled) {
            throw executionException("History is disabled.");
        }
        if (isHistoryConsistent) {
            throw executionException("History is inconsistent.");
        }
        return allocations.rawLinesView();
    }

    @Override
    public int size() {
        if (!isRegisterEventIsEnabled) {
            throw executionException("History is disabled.");
        }
        if (isHistoryConsistent) {
            throw executionException("History is inconsistent.");
        }
        return allocations.size();
    }

    @Override
    public List<Line> rawLines() {
        if (!isRegisterEventIsEnabled) {
            throw executionException("History is disabled.");
        }
        if (isHistoryConsistent) {
            throw executionException("History is inconsistent.");
        }
        return allocations.rawLines();
    }

    @Override
    public Line lookupEquals(Attribute<Line> attribute, Line other) {
        if (!isRegisterEventIsEnabled) {
            throw executionException("History is disabled.");
        }
        if (isHistoryConsistent) {
            throw executionException("History is inconsistent.");
        }
        return allocations.lookupEquals(attribute, other);
    }

    @Override
    public Node toDom() {
        throw notImplementedYet();
    }

    @Override
    public List<String> path() {
        return allocations.path();
    }

    public boolean isHistoryConsistent() {
        return isHistoryConsistent;
    }

    @Override
    public boolean isRegisterEventIsEnabled() {
        return isRegisterEventIsEnabled;
    }
}
