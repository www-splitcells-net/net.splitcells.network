/*
 * Copyright (c) 2021 Contributors To The `net.splitcells.*` Projects
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License v2.0 or later
 * which is available at https://www.gnu.org/licenses/old-licenses/gpl-2.0-standalone.html
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.solution.history;

import static java.util.stream.IntStream.rangeClosed;
import static net.splitcells.dem.data.atom.Bools.require;
import static net.splitcells.dem.data.atom.Integers.requireEqualInts;
import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.dem.environment.config.StaticFlags.ENFORCING_UNIT_CONSISTENCY;
import static net.splitcells.dem.lang.tree.Tree.toStringPathsDescription;
import static net.splitcells.dem.lang.tree.TreeI.tree;
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

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.ListView;
import net.splitcells.gel.data.assignment.Assignments;
import net.splitcells.gel.data.table.Tables;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.data.view.LinePointer;
import net.splitcells.gel.solution.Solution;
import net.splitcells.gel.data.view.column.ColumnView;
import net.splitcells.gel.data.table.AfterAdditionSubscriber;
import net.splitcells.gel.data.table.BeforeRemovalSubscriber;
import net.splitcells.gel.data.assignment.Assignmentss;
import net.splitcells.gel.data.view.Line;
import net.splitcells.gel.data.view.attribute.Attribute;
import net.splitcells.gel.solution.history.meta.type.AllocationNaturalArgumentation;
import net.splitcells.gel.solution.history.meta.type.AllocationRating;
import net.splitcells.gel.solution.history.meta.type.CompleteRating;
import net.splitcells.website.server.project.renderer.DiscoverableRenderer;

import java.util.function.Supplier;

/**
 * <p>TODO TOFIX The variable isRegisterEventIsEnabled is used incorrectly.</p>
 * <p>TODO Limit or disable meta data in order to improve runtime performance.</p>
 */
public class HistoryI implements History {

    public static History historyI(Solution solution) {
        return new HistoryI(solution);
    }

    private static final String ERROR_HISTORY_DISABLED = "History is disabled.";
    private static final String ERROR_HISTORY_INCONSISTENT = "History is inconsistent.";

    private final Solution solution;
    private int lastEventId = -1;
    private Assignments assignments;
    private boolean isRegisterEventIsEnabled = false;
    private boolean isHistoryConsistent = false;
    private boolean synchronizes = false;
    private boolean logNaturalArgumentation = false;

    private HistoryI(Solution solution) {
        assignments = Assignmentss.assignments
                (HISTORY.value()
                        , Tables.table
                                (EVENT.value()
                                        , () -> solution.path().withAppended(HISTORY.value())
                                        , list(ALLOCATION_ID, ALLOCATION_EVENT))
                        , Tables.table
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
            if (logNaturalArgumentation) {
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
                            , solution.demandOfAssignment(allocationValues)
                            , solution.supplyOfAssignment(allocationValues))));
            assignments.assign(allocation, this.supplies().addTranslated(list(metaData)));
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
                            , solution.demandOfAssignment(removal)
                            , solution.supplyOfAssignment(removal))));
            assignments.assign(allocation, this.supplies().addTranslated(list(metaData)));
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
            throw executionException(ERROR_HISTORY_DISABLED);
        }
        if (ENFORCING_UNIT_CONSISTENCY) {
            require(index >= -1);
            if (assignments.size() > 0) {
                require(index <= assignments.size() - 1);
            }
        }
        if (assignments.size() == 0 && index == -1) {
            return;
        }
        if (assignments.size() - 1 == index) {
            return;
        }
        if (assignments.size() == 0) {
            if (ENFORCING_UNIT_CONSISTENCY) {
                requireEqualInts(index, -1);
            }
            return;
        }
        isRegisterEventIsEnabled = false;
        /**
         * Omit unnecessary allocations to {@link #assignments},
         * when allocations are removed from {@link #solution} during reset.
         */
        final var indexToReversal = reverse
                (rangeClosed(index, assignments.size() - 1)
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
            if (assignments.size() != 0) {
                indexes.requireAnyContent();
            } else {
                indexes.requireEmptySet();
            }
        }
        indexes.forEach(i -> resetLast());
    }

    private void resetLast() {
        synchronizes = true;
        if (ENFORCING_UNIT_CONSISTENCY) {
            assignments.columnView(ALLOCATION_ID)
                    .persistedLookup(assignments.size() - 1)
                    .unorderedLines()
                    .requireSizeOf(1);
        }
        final var index = assignments.size() - 1;
        final var eventToRemove = assignments.columnView(ALLOCATION_ID)
                .persistedLookup(index)
                .unorderedLinesStream()
                .findFirst()
                .orElseThrow()
                .value(ALLOCATION_EVENT);
        final var eventType = eventToRemove.type();
        if (eventType.equals(ADDITION)) {
            solution.remove(solution.anyAssignmentOf
                    (eventToRemove.demand().toLinePointer()
                            , eventToRemove.supply().toLinePointer()));
        } else if (eventType.equals(REMOVAL)) {
            solution.assign
                    (eventToRemove.demand().toLinePointer().interpret(solution.demands()).orElseThrow()
                            , eventToRemove.supply().toLinePointer().interpret(solution.supplies()).orElseThrow());
        } else {
            throw new UnsupportedOperationException();
        }
        resetLastRemoval(index);
        synchronizes = false;
    }

    private void resetLastRemoval(int index) {
        if (ENFORCING_UNIT_CONSISTENCY) {
            assignments.unorderedLines().requireSizeOf(index + 1);
            try {
                assignments.columnView(ALLOCATION_ID).persistedLookup(index + 1).unorderedLines().requireEmptySet();
                assignments.columnView(ALLOCATION_ID).persistedLookup(index).unorderedLines().requireSizeOf(1);
            } catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }
        removal_(assignments.columnView(ALLOCATION_ID).persistedLookup(index).unorderedLinesStream().findFirst().orElseThrow());
    }

    /**
     * TODO FIX Remove unused demands and supply.
     * <p/>
     * TODO Automatically remove allocations from solution, via subscription.
     *
     * @param line
     */
    private void removal_(Line line) {
        assignments.remove(line);
        --lastEventId;
    }

    @Override
    public void remove(Line line) {
        if (!isRegisterEventIsEnabled) {
            throw executionException(ERROR_HISTORY_DISABLED);
        }
        if (isHistoryConsistent) {
            throw executionException(ERROR_HISTORY_INCONSISTENT);
        }
        throw notImplementedYet();
    }

    @Override
    public void subscribeToAfterAdditions(AfterAdditionSubscriber subscriber) {
        assignments.subscribeToAfterAdditions(subscriber);
    }

    @Override
    public void subscribeToBeforeRemoval(BeforeRemovalSubscriber subscriber) {
        assignments.subscribeToBeforeRemoval(subscriber);
    }

    @Override
    public void subscribeToAfterRemoval(BeforeRemovalSubscriber beforeRemovalSubscriber) {
        assignments.subscribeToAfterRemoval(beforeRemovalSubscriber);
    }

    @Override
    public Line addTranslated(ListView<Object> lineValues, int index) {
        return assignments.addTranslated(lineValues, index);
    }

    @Override
    public Line addTranslated(ListView<?> values) {
        if (!isRegisterEventIsEnabled) {
            throw executionException(ERROR_HISTORY_DISABLED);
        }
        if (isHistoryConsistent) {
            throw executionException(ERROR_HISTORY_INCONSISTENT);
        }
        throw notImplementedYet();
    }

    @Override
    public Line add(Line line) {
        if (!isRegisterEventIsEnabled) {
            throw executionException(ERROR_HISTORY_DISABLED);
        }
        if (isHistoryConsistent) {
            throw executionException(ERROR_HISTORY_INCONSISTENT);
        }
        throw notImplementedYet();
    }

    @Override
    public Line addWithSameHeaderPrefix(Line line) {
        throw notImplementedYet();
    }

    @Override
    public void remove(int indexes) {
        if (!isRegisterEventIsEnabled) {
            throw executionException(ERROR_HISTORY_DISABLED);
        }
        if (isHistoryConsistent) {
            throw executionException(ERROR_HISTORY_INCONSISTENT);
        }
        if (size() != indexes + 1) {
            throw notImplementedYet();
        }
        assignments.remove(rawLinesView().get(indexes));
    }

    @Override
    public int currentIndex() {
        if (!isRegisterEventIsEnabled) {
            throw executionException(ERROR_HISTORY_DISABLED);
        }
        if (isHistoryConsistent) {
            throw executionException(ERROR_HISTORY_INCONSISTENT);
        }
        return lastEventId;
    }

    @Override
    public Line assign(Line demand, Line supply) {
        if (!isRegisterEventIsEnabled) {
            throw executionException(ERROR_HISTORY_DISABLED);
        }
        if (isHistoryConsistent) {
            throw executionException(ERROR_HISTORY_INCONSISTENT);
        }
        throw notImplementedYet();
    }

    @Override
    public Line anyAssignmentOf(LinePointer demand, LinePointer supply) {
        if (!isRegisterEventIsEnabled) {
            throw executionException(ERROR_HISTORY_DISABLED);
        }
        if (isHistoryConsistent) {
            throw executionException(ERROR_HISTORY_INCONSISTENT);
        }
        return this.assignments.anyAssignmentOf(demand, supply);
    }

    @Override
    public Table supplies() {
        if (!isRegisterEventIsEnabled) {
            throw executionException(ERROR_HISTORY_DISABLED);
        }
        if (isHistoryConsistent) {
            throw executionException(ERROR_HISTORY_INCONSISTENT);
        }
        return assignments.supplies();
    }

    @Override
    public Table suppliesUsed() {
        if (!isRegisterEventIsEnabled) {
            throw executionException(ERROR_HISTORY_DISABLED);
        }
        if (isHistoryConsistent) {
            throw executionException(ERROR_HISTORY_INCONSISTENT);
        }
        return assignments.suppliesUsed();
    }

    @Override
    public Table suppliesFree() {
        if (!isRegisterEventIsEnabled) {
            throw executionException(ERROR_HISTORY_DISABLED);
        }
        if (isHistoryConsistent) {
            throw executionException(ERROR_HISTORY_INCONSISTENT);
        }
        return assignments.suppliesFree();
    }

    @Override
    public Table demands() {
        if (!isRegisterEventIsEnabled) {
            throw executionException(ERROR_HISTORY_DISABLED);
        }
        if (isHistoryConsistent) {
            throw executionException(ERROR_HISTORY_INCONSISTENT);
        }
        return assignments.demands();
    }

    @Override
    public Table demandsUsed() {
        if (!isRegisterEventIsEnabled) {
            throw executionException(ERROR_HISTORY_DISABLED);
        }
        if (isHistoryConsistent) {
            throw executionException(ERROR_HISTORY_INCONSISTENT);
        }
        return assignments.demandsUsed();
    }

    @Override
    public Table demandsFree() {
        if (!isRegisterEventIsEnabled) {
            throw executionException(ERROR_HISTORY_DISABLED);
        }
        if (isHistoryConsistent) {
            throw executionException(ERROR_HISTORY_INCONSISTENT);
        }
        return assignments.demandsFree();
    }

    @Override
    public Line demandOfAssignment(Line allocation) {
        if (!isRegisterEventIsEnabled) {
            throw executionException(ERROR_HISTORY_DISABLED);
        }
        if (isHistoryConsistent) {
            throw executionException(ERROR_HISTORY_INCONSISTENT);
        }
        return assignments.demandOfAssignment(allocation);
    }

    @Override
    public Line supplyOfAssignment(Line allocation) {
        if (!isRegisterEventIsEnabled) {
            throw executionException(ERROR_HISTORY_DISABLED);
        }
        if (isHistoryConsistent) {
            throw executionException(ERROR_HISTORY_INCONSISTENT);
        }
        return assignments.supplyOfAssignment(allocation);
    }

    @Override
    public Set<Line> assignmentsOfSupply(Line supply) {
        if (!isRegisterEventIsEnabled) {
            throw executionException(ERROR_HISTORY_DISABLED);
        }
        if (isHistoryConsistent) {
            throw executionException(ERROR_HISTORY_INCONSISTENT);
        }
        return assignments.assignmentsOfSupply(supply);
    }

    @Override
    public Set<Line> assignmentsOfDemand(Line demand) {
        if (!isRegisterEventIsEnabled) {
            throw executionException(ERROR_HISTORY_DISABLED);
        }
        if (isHistoryConsistent) {
            throw executionException(ERROR_HISTORY_INCONSISTENT);
        }
        return assignments.assignmentsOfDemand(demand);
    }

    @Override
    public String name() {
        return "history of " + solution.name();
    }

    @Override
    public List<Attribute<Object>> headerView() {
        if (!isRegisterEventIsEnabled) {
            throw executionException(ERROR_HISTORY_DISABLED);
        }
        if (isHistoryConsistent) {
            throw executionException(ERROR_HISTORY_INCONSISTENT);
        }
        return assignments.headerView();
    }

    @Override
    public List<Attribute<? extends Object>> headerView2() {
        if (!isRegisterEventIsEnabled) {
            throw executionException(ERROR_HISTORY_DISABLED);
        }
        if (isHistoryConsistent) {
            throw executionException(ERROR_HISTORY_INCONSISTENT);
        }
        return assignments.headerView2();
    }

    @Override
    public <T> ColumnView<T> columnView(Attribute<T> attribute) {
        if (!isRegisterEventIsEnabled) {
            throw executionException(ERROR_HISTORY_DISABLED);
        }
        if (isHistoryConsistent) {
            throw executionException(ERROR_HISTORY_INCONSISTENT);
        }
        return assignments.columnView(attribute);
    }

    @Override
    public ListView<ColumnView<Object>> columnsView() {
        if (!isRegisterEventIsEnabled) {
            throw executionException(ERROR_HISTORY_DISABLED);
        }
        if (isHistoryConsistent) {
            throw executionException(ERROR_HISTORY_INCONSISTENT);
        }
        return assignments.columnsView();
    }

    @Override
    public ListView<Line> rawLinesView() {
        if (!isRegisterEventIsEnabled) {
            throw executionException(ERROR_HISTORY_DISABLED);
        }
        if (isHistoryConsistent) {
            throw executionException(ERROR_HISTORY_INCONSISTENT);
        }
        return assignments.rawLinesView();
    }

    @Override
    public List<Line> orderedLines() {
        if (!isRegisterEventIsEnabled) {
            throw executionException(ERROR_HISTORY_DISABLED);
        }
        if (isHistoryConsistent) {
            throw executionException(ERROR_HISTORY_INCONSISTENT);
        }
        return assignments.orderedLines();
    }

    @Override
    public int size() {
        if (!isRegisterEventIsEnabled) {
            throw executionException(ERROR_HISTORY_DISABLED);
        }
        if (isHistoryConsistent) {
            throw executionException(ERROR_HISTORY_INCONSISTENT);
        }
        return assignments.size();
    }

    @Override
    public List<Line> rawLines() {
        if (!isRegisterEventIsEnabled) {
            throw executionException(ERROR_HISTORY_DISABLED);
        }
        if (isHistoryConsistent) {
            throw executionException(ERROR_HISTORY_INCONSISTENT);
        }
        return assignments.rawLines();
    }

    @Override
    public Line lookupEquals(Attribute<Line> attribute, Line other) {
        if (!isRegisterEventIsEnabled) {
            throw executionException(ERROR_HISTORY_DISABLED);
        }
        if (isHistoryConsistent) {
            throw executionException(ERROR_HISTORY_INCONSISTENT);
        }
        return assignments.lookupEquals(attribute, other);
    }

    @Override
    public DiscoverableRenderer discoverableRenderer() {
        return assignments.discoverableRenderer();
    }

    @Override
    public List<String> path() {
        return assignments.path();
    }

    public boolean isHistoryConsistent() {
        return isHistoryConsistent;
    }

    @Override
    public boolean isRegisterEventIsEnabled() {
        return isRegisterEventIsEnabled;
    }

    @Override
    public History withRegisterEventIsEnabled(boolean arg) {
        isRegisterEventIsEnabled = arg;
        return this;
    }

    @Override
    public Object identity() {
        return this;
    }

    @Override
    public boolean logNaturalArgumentation() {
        return logNaturalArgumentation;
    }

    @Override
    public History withLogNaturalArgumentation(boolean newValue) {
        logNaturalArgumentation = newValue;
        return this;
    }
}
