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
package net.splitcells.gel.data.assignment;

import static net.splitcells.dem.data.atom.Bools.require;
import static net.splitcells.dem.environment.config.StaticFlags.ENFORCING_UNIT_CONSISTENCY;
import static net.splitcells.dem.environment.config.StaticFlags.TRACING;
import static net.splitcells.dem.lang.Xml.elementWithChildren;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.resource.communication.log.Logs.logs;
import static net.splitcells.dem.resource.communication.log.LogLevel.DEBUG;
import static net.splitcells.dem.testing.Assertions.requireEquals;
import static net.splitcells.dem.testing.Assertions.requireNotNull;
import static net.splitcells.dem.utils.ExecutionException.execException;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.list.Lists.concat;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.gel.common.Language.*;
import static net.splitcells.gel.data.table.Tables.table2;

import java.util.stream.Stream;

import net.splitcells.dem.data.atom.Integers;
import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.ListView;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.dem.utils.ExecutionException;
import net.splitcells.gel.data.table.Tables;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.data.view.Line;
import net.splitcells.gel.data.view.LinePointer;
import net.splitcells.gel.data.view.attribute.Attribute;
import net.splitcells.gel.data.view.column.ColumnView;
import net.splitcells.gel.data.table.AfterAdditionSubscriber;
import net.splitcells.gel.data.table.BeforeRemovalSubscriber;
import net.splitcells.gel.proposal.Proposal;
import net.splitcells.website.server.project.renderer.DiscoverableRenderer;

/**
 * <p>{@link #demandsUsed()} ()} and {@link #demandsFree()} contain all {@link Line} of {@link #demands()}.</p>
 * <p>Line removal from {@link #demandsFree} and {@link #suppliesFree} has no subscriptions,
 * because {@link Table} lines can be remove from the {@link Assignments} completely
 * or they can be moved to the respectively used tables.</p>
 * <p>TODO Fix {@link #demandOfAssignment(Line)} by using {@link #demandsUsed()}.</p>
 * <p>TODO Fix {@link #supplyOfAssignment} by using {@link #suppliesUsed()}.</p>
 * <p>TODO IDEA Support for multiple {@link #path}. In this case paths with demand and supplies as base.</p>
 * <p>TODO Define {@link #path()} as an convention regarding the meaning of demands and supplies.</p>
 */
public class AssignmentsI implements Assignments {

    public static Assignments assignments(String name, Table demands, Table supplies) {
        return new AssignmentsI(name, demands, supplies);
    }

    /**
     * TODO This is deprecated, because {@link #assignments} already has the fitting name.
     */
    @Deprecated
    private final String names;
    private final Table assignments;

    private final List<AfterAdditionSubscriber> additionSubscriptions = list();
    private final List<BeforeRemovalSubscriber> beforeRemovalSubscriptions = list();
    private final List<BeforeRemovalSubscriber> afterRemovalSubscriptions = list();

    private final Table supplies;
    private final Table suppliesUsed;
    private final Table suppliesFree;
    /**
     * TODO Make this configurable. This is needed for {@link Proposal}.
     */
    private final boolean allowsSuppliesOnDemand = true;

    private final Table demands;
    private final Table demandsUsed;
    private final Table demandsFree;

    private final Map<Integer, Integer> allocationsIndexToUsedDemandIndex = map();
    private final Map<Integer, Integer> allocationsIndexToUsedSupplyIndex = map();

    private final Map<Integer, Set<Integer>> usedDemandIndexesToAllocationIndexes = map();
    private final Map<Integer, Set<Integer>> usedSupplyIndexesToAllocationIndexes = map();

    private final Map<Integer, Set<Integer>> usedDemandsIndexToUsedSuppliesIndex = map();
    private final Map<Integer, Set<Integer>> usedSupplyIndexToUsedDemandsIndex = map();

    private AssignmentsI(String name, Table demand, Table supply) {
        this.names = name;
        assignments = Tables.table2(name, demand, concat(demand.headerView(), supply.headerView()));
        // TODO Remove code and comment duplications.
        {
            this.demands = demand;
            demandsFree = Tables.table2("demands-free", this, demand.headerView());
            demandsUsed = Tables.table2("demands-used", this, demand.headerView());
            demand.rawLinesView().forEach(demandsFree::add);
            demand.subscribeToAfterAdditions(demandsFree::add);
            demand.subscribeToBeforeRemoval(removalOf -> {
                if (usedDemandIndexesToAllocationIndexes.containsKey(removalOf.index())) {
                    listWithValuesOf(
                            usedDemandIndexesToAllocationIndexes.get(removalOf.index()))
                            .forEach(allocationOfDemand -> remove(assignments.rawLinesView().get(allocationOfDemand)));
                }
                if (demandsFree.contains(removalOf)) {
                    demandsFree.remove(removalOf);
                }
                // TODO FIX Does something needs to be done if the condition is false.
                if (demandsUsed.contains(removalOf)) {
                    demandsUsed.remove(removalOf);
                }
            });
        }
        {
            this.supplies = requireNotNull(supply);
            suppliesFree = Tables.table2("supply-free", this, supply.headerView());
            suppliesUsed = Tables.table2("supply-used", this, supply.headerView());
            supply.rawLinesView().forEach(suppliesFree::add);
            supply.subscribeToAfterAdditions(i -> {
                suppliesFree.add(i);
            });
            supply.subscribeToBeforeRemoval(removalOf -> {
                if (usedSupplyIndexesToAllocationIndexes.containsKey(removalOf.index())) {
                    listWithValuesOf
                            (usedSupplyIndexesToAllocationIndexes.get(removalOf.index()))
                            .forEach(allocationsOfSupply
                                    -> remove(assignments.rawLinesView().get(allocationsOfSupply)));
                }
                if (suppliesFree.contains(removalOf)) {
                    suppliesFree.remove(removalOf);
                }
                // TODO FIX Does something needs to be done if the condition is false.
                if (suppliesUsed.contains(removalOf)) {
                    suppliesUsed.remove(removalOf);
                }
            });
        }
    }

    @Override
    public Table supplies() {
        return supplies;
    }

    @Override
    public Table suppliesUsed() {
        return suppliesUsed;
    }

    @Override
    public Table suppliesFree() {
        return suppliesFree;
    }

    @Override
    public Table demands() {
        return demands;
    }

    @Override
    public Table demandsUsed() {
        return demandsUsed;
    }

    @Override
    public Table demandsFree() {
        return demandsFree;
    }

    @Override
    public Line assign(Line demand, Line supply) {
        if (TRACING) {
            requireNotNull(demand, "Cannot allocate without demand.");
            requireNotNull(supply, "Cannot allocate without supply.");
            logs().append(tree(ALLOCATE.value() + PATH_ACCESS_SYMBOL.value() + Assignments.class.getSimpleName())
                            .withProperty("path", path().toString())
                            .withProperty("demand", demand.toTree())
                            .withProperty("supply", supply.toTree())
                    , this
                    , DEBUG);
        }
        if (ENFORCING_UNIT_CONSISTENCY) {
            list(demand.context()).requireContainsOneOf(demandsFree, demands);
            list(demand.context()).requireContainsOneOf(demandsFree, demands);
            list(supply.context()).requireContainsOneOf(supplies, suppliesFree);
            if (demand.index() < demands.rawLinesView().size()) {
                requireNotNull(demands.rawLinesView().get(demand.index()));
            } else if (demand.index() < demandsFree.rawLinesView().size()) {
                requireNotNull(demandsFree.rawLinesView().get(demand.index()));
            } else if (demand.index() < demandsUsed.rawLinesView().size()) {
                requireNotNull(demandsUsed.rawLinesView().get(demand.index()));
            } else {
                throw ExecutionException.execException("A demand with such an index is not known");
            }
            if (supply.index() < supplies.rawLinesView().size()) {
                requireNotNull(supplies.rawLinesView().get(supply.index()));
            } else if (supply.index() < suppliesFree.rawLinesView().size()) {
                requireNotNull(suppliesFree.rawLinesView().get(supply.index()));
            } else if (supply.index() < suppliesUsed.rawLinesView().size()) {
                requireNotNull(suppliesUsed.rawLinesView().get(supply.index()));
            } else {
                throw ExecutionException.execException("A supply with such an index is not known");
            }
            list(supply.context()).requireContainsOneOf(supplies, suppliesFree, suppliesUsed);
            list(demand.context()).requireContainsOneOf(demands, demandsFree, demandsUsed);
            {
                // Multiple allocations per supply or demand are allowed.
                boolean valid = false;
                if (demand.index() < demandsUsed.rawLinesView().size()) {
                    valid |= demandsUsed.rawLinesView().get(demand.index()) != null;
                    if (demand.index() < demandsFree.rawLinesView().size()) {
                        valid |= demandsFree.rawLinesView().get(demand.index()) != null;
                    }
                } else if (demand.index() < demandsFree.rawLinesView().size()) {
                    valid |= demandsFree.rawLinesView().get(demand.index()) != null;
                    if (demand.index() < demandsUsed.rawLinesView().size()) {
                        valid |= demandsUsed.rawLinesView().get(demand.index()) != null;
                    }
                } else {
                    throw new IllegalArgumentException();
                }
                assert valid;
                /**
                 * TODO The same for supplies;
                 * <p/>
                 * TODO Test if the right tables contain the suppy and if other tables do not
                 * contain these {@link Table}
                 */
            }
        }
        final var allocation = assignments.addTranslated(Line.concat(demand, supply));
        if (!usedSupplyIndexesToAllocationIndexes.containsKey(supply.index())) {
            suppliesUsed.addWithSameHeaderPrefix(supply);
            suppliesFree.remove(supply);
        }
        if (!usedDemandIndexesToAllocationIndexes.containsKey(demand.index())) {
            demandsUsed.addWithSameHeaderPrefix(demand);
            demandsFree.remove(demand);
        }
        {
            allocationsIndexToUsedDemandIndex.put(allocation.index(), demand.index());
            allocationsIndexToUsedSupplyIndex.put(allocation.index(), supply.index());
        }
        {
            {
                if (!usedDemandIndexesToAllocationIndexes.containsKey(demand.index())) {
                    usedDemandIndexesToAllocationIndexes.put(demand.index(), setOfUniques());
                }
                usedDemandIndexesToAllocationIndexes.get(demand.index()).add(allocation.index());
                if (!usedSupplyIndexesToAllocationIndexes.containsKey(supply.index())) {
                    usedSupplyIndexesToAllocationIndexes.put(supply.index(), setOfUniques());
                }
                usedSupplyIndexesToAllocationIndexes.get(supply.index()).add(allocation.index());
            }
        }
        {
            {
                if (!usedDemandsIndexToUsedSuppliesIndex.containsKey(demand.index())) {
                    usedDemandsIndexToUsedSuppliesIndex.put(demand.index(), setOfUniques());
                }
                usedDemandsIndexToUsedSuppliesIndex.get(demand.index()).add(supply.index());
            }
            {
                if (!usedSupplyIndexToUsedDemandsIndex.containsKey(supply.index())) {
                    usedSupplyIndexToUsedDemandsIndex.put(supply.index(), setOfUniques());
                }
                usedSupplyIndexToUsedDemandsIndex.get(supply.index()).add(demand.index());
            }
        }
        additionSubscriptions.forEach(listener -> listener.registerAddition(allocation));
        return allocation;
    }

    @Override
    public boolean allowsSuppliesOnDemand() {
        return allowsSuppliesOnDemand;
    }

    @Override
    public Line demandOfAssignment(Line allocation) {
        return demands.rawLinesView()
                .get(allocationsIndexToUsedDemandIndex.get(allocation.index()));
    }

    @Override
    public Line supplyOfAssignment(Line allocation) {
        return supplies.rawLinesView()
                .get(allocationsIndexToUsedSupplyIndex.get(allocation.index()));
    }

    @Override
    public Line addTranslated(ListView<Object> lineValues, int index) {
        throw notImplementedYet();
    }

    @Override
    public Line addTranslated(ListView<?> values) {
        throw notImplementedYet();
    }

    @Override
    public Line add(Line line) {
        throw notImplementedYet();
    }

    @Override
    public Line addWithSameHeaderPrefix(Line line) {
        throw notImplementedYet();
    }

    @Override
    public void remove(Line allocation) {
        final var demand = demandOfAssignment(allocation);
        final var supply = supplyOfAssignment(allocation);
        if (TRACING) {
            logs().append(tree(REMOVE.value()
                            + PATH_ACCESS_SYMBOL.value()
                            + Assignments.class.getSimpleName())
                            .withProperty("path", path().toString())
                            .withProperty("demand", demand.toTree())
                            .withProperty("supply", supply.toTree())
                    , this
                    , DEBUG);
        }
        if (ENFORCING_UNIT_CONSISTENCY) {
            list(demand.context()).requireContainsOneOf(demands, demandsUsed);
            list(supply.context()).requireContainsOneOf(supplies, suppliesUsed);
            requireEquals(allocation.context(), assignments);
            usedDemandIndexesToAllocationIndexes.get(demand.index()).requirePresenceOf(allocation.index());
            usedSupplyIndexesToAllocationIndexes.get(supply.index()).requirePresenceOf(allocation.index());
            requireEquals(allocationsIndexToUsedDemandIndex.get(allocation.index()), demand.index());
            requireEquals(allocationsIndexToUsedSupplyIndex.get(allocation.index()), supply.index());
        }
        beforeRemovalSubscriptions.forEach(subscriber -> subscriber.registerBeforeRemoval(allocation));
        assignments.remove(allocation);
        // TODO Make following code a remove subscription to allocations.
        {
            allocationsIndexToUsedDemandIndex.remove(allocation.index());
            allocationsIndexToUsedSupplyIndex.remove(allocation.index());
        }
        {
            {
                usedDemandsIndexToUsedSuppliesIndex.get(demand.index()).remove(supply.index());
                if (usedDemandsIndexToUsedSuppliesIndex.get(demand.index()).isEmpty()) {
                    usedDemandsIndexToUsedSuppliesIndex.remove(demand.index());
                }
                usedSupplyIndexToUsedDemandsIndex.get(supply.index()).remove(demand.index());
                if (usedSupplyIndexToUsedDemandsIndex.get(supply.index()).isEmpty()) {
                    usedSupplyIndexToUsedDemandsIndex.remove(supply.index());
                }
            }
            {
                usedSupplyIndexesToAllocationIndexes.get(supply.index()).remove(allocation.index());
                if (usedSupplyIndexesToAllocationIndexes.get(supply.index()).isEmpty()) {
                    usedSupplyIndexesToAllocationIndexes.remove(supply.index());
                }
                usedDemandIndexesToAllocationIndexes.get(demand.index()).remove(allocation.index());
                if (usedDemandIndexesToAllocationIndexes.get(demand.index()).isEmpty()) {
                    usedDemandIndexesToAllocationIndexes.remove(demand.index());
                }
            }
        }
        allocationsIndexToUsedDemandIndex.remove(allocation.index());
        allocationsIndexToUsedSupplyIndex.remove(allocation.index());
        if (!usedDemandsIndexToUsedSuppliesIndex.containsKey(demand.index())) {
            demandsUsed.remove(demand);
            demandsFree.addWithSameHeaderPrefix(demand);
        }
        if (!usedSupplyIndexToUsedDemandsIndex.containsKey(supply.index())) {
            suppliesUsed.remove(supply);
            suppliesFree.addWithSameHeaderPrefix(supply);
        }
        afterRemovalSubscriptions.forEach(listener -> listener.registerBeforeRemoval(allocation));
    }

    @Override
    public void subscribeToAfterAdditions(AfterAdditionSubscriber subscriber) {
        additionSubscriptions.add(subscriber);
    }

    @Override
    public String name() {
        return names;
    }

    @Override
    public List<Attribute<Object>> headerView() {
        return assignments.headerView();
    }

    @Override
    public List<Attribute<? extends Object>> headerView2() {
        return assignments.headerView2();
    }

    @Override
    public <T> ColumnView<T> columnView(Attribute<T> attribute) {
        if (ENFORCING_UNIT_CONSISTENCY) {
            require(demands.headerView().contains(attribute) || supplies.headerView().contains(attribute));
        }
        return assignments.columnView(attribute);
    }

    @Override
    public ListView<Line> rawLinesView() {
        return assignments.rawLinesView();
    }

    @Override
    public void subscribeToBeforeRemoval(BeforeRemovalSubscriber subscriber) {
        beforeRemovalSubscriptions.add(subscriber);
    }

    @Override
    public int size() {
        return assignments.size();
    }

    @Override
    public void remove(int lineIndex) {
        remove(assignments.rawLinesView().get(lineIndex));
    }

    @Override
    public void subscribeToAfterRemoval(BeforeRemovalSubscriber subscriber) {
        afterRemovalSubscriptions.add(subscriber);
        throw notImplementedYet("This method is not correct: the argument type is completely of.");
    }

    @Override
    public Set<Line> assignmentsOfSupply(Line supply) {
        if (ENFORCING_UNIT_CONSISTENCY && !usedSupplyIndexesToAllocationIndexes.containsKey(supply.index())) {
            throw ExecutionException.execException(tree("No allocations for the given supply are present.")
                    .withProperty("supply index", "" + supply.index())
                    .withProperty("context path", "" + supply.context().path()));
        }
        final Set<Line> allocationsOfSupply = setOfUniques();
        usedSupplyIndexesToAllocationIndexes
                .get(supply.index())
                .forEach(allocationIndex ->
                        allocationsOfSupply.add(assignments.rawLinesView().get(allocationIndex)));
        return allocationsOfSupply;
    }

    @Override
    public Set<Line> assignmentsOfDemand(Line demand) {
        if (ENFORCING_UNIT_CONSISTENCY) {
            setOfUniques(usedDemandIndexesToAllocationIndexes.keySet()).requirePresenceOf(demand.index());
        }
        final Set<Line> allocationsOfDemand = setOfUniques();
        usedDemandIndexesToAllocationIndexes
                .get(demand.index())
                .forEach(allocationIndex ->
                        allocationsOfDemand.add(assignments.rawLinesView().get(allocationIndex)));
        return allocationsOfDemand;
    }

    @Override
    public Line anyAssignmentOf(LinePointer demand, LinePointer supply) {
        if (ENFORCING_UNIT_CONSISTENCY) {
            usedDemandIndexesToAllocationIndexes
                    .get(demand.index())
                    .assertSizeIs(1);
            usedSupplyIndexesToAllocationIndexes
                    .get(supply.index())
                    .assertSizeIs(1);
            final var demandLine = usedDemandIndexesToAllocationIndexes
                    .get(demand.index())
                    .iterator()
                    .next();
            final var supplyLine = usedSupplyIndexesToAllocationIndexes
                    .get(supply.index())
                    .iterator()
                    .next();
            Integers.requireEqualInts(demandLine, supplyLine);
        }
        return assignments.rawLine(
                usedDemandIndexesToAllocationIndexes
                        .get(demand.index())
                        .iterator()
                        .next());
    }

    @Override
    public ListView<ColumnView<Object>> columnsView() {
        return assignments.columnsView();
    }

    @Override
    public String toString() {
        return Assignments.class.getSimpleName() + path().toString();
    }

    @Override
    public net.splitcells.dem.data.set.list.List<String> path() {
        return assignments.path();
    }

    @Override
    public Tree toTree() {
        final var dom = tree(Assignments.class.getSimpleName());
        dom.withChild(tree(path().toString()));
        rawLinesView().stream()
                .filter(line -> line != null)
                .forEach(line -> dom.withChild(line.toTree()));
        return dom;
    }

    @Override
    public List<Line> rawLines() {
        throw notImplementedYet();
    }

    @Override
    public Line lookupEquals(Attribute<Line> attribute, Line other) {
        return assignments.lookupEquals(attribute, other);
    }

    @Override
    public DiscoverableRenderer discoverableRenderer() {
        return assignments.discoverableRenderer();
    }

    @Override
    public Object identity() {
        return this;
    }

    @Override
    public boolean equals(Object arg) {
        if (arg instanceof Assignments) {
            final var castedArg = (Assignments) arg;
            return identity().equals(castedArg.identity());
        }
        throw ExecutionException.execException("Invalid argument type: " + arg);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public Stream<Line> orderedLinesStream() {
        return assignments.orderedLinesStream();
    }

    @Override
    public List<Line> orderedLines() {
        return assignments.orderedLines();
    }
}
