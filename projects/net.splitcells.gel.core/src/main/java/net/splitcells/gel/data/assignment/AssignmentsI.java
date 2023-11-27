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
import static net.splitcells.dem.lang.Xml.event;
import static net.splitcells.dem.lang.Xml.textNode;
import static net.splitcells.dem.lang.perspective.PerspectiveI.perspective;
import static net.splitcells.dem.resource.communication.log.Logs.logs;
import static net.splitcells.dem.resource.communication.log.LogLevel.DEBUG;
import static net.splitcells.dem.testing.Assertions.requireEquals;
import static net.splitcells.dem.testing.Assertions.requireNotNull;
import static net.splitcells.dem.utils.ExecutionException.executionException;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.list.Lists.concat;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.gel.common.Language.ALLOCATE;
import static net.splitcells.gel.common.Language.ALLOCATION;
import static net.splitcells.gel.common.Language.DEMAND;
import static net.splitcells.gel.common.Language.PATH_ACCESS_SYMBOL;
import static net.splitcells.gel.common.Language.REMOVE;
import static net.splitcells.gel.common.Language.SUPPLY;
import static net.splitcells.gel.data.database.Databases.database2;

import java.util.stream.Stream;

import net.splitcells.dem.data.atom.Integers;
import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.ListView;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.lang.perspective.Perspective;
import net.splitcells.gel.common.Language;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.LinePointer;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.data.table.column.ColumnView;
import org.w3c.dom.Element;
import net.splitcells.gel.data.database.AfterAdditionSubscriber;
import net.splitcells.gel.data.database.Database;
import net.splitcells.gel.data.database.BeforeRemovalSubscriber;

/**
 * <p>{@link #demandsUsed()} ()} and {@link #demandsFree()} contain all {@link Line} of {@link #demands()}.</p>
 * <p>Line removal from {@link #demands_free} and {@link #supplies_free} has no subscriptions,
 * because {@link Database} lines can be remove from the {@link Assignments} completely
 * or they can be moved to the respectively used tables.</p>
 * <p>TODO Fix {@link #demandOfAssignment(Line)} by using {@link #demands_used}.</p>
 * <p>TODO Fix {@link #supplyOfAssignment} by using {@link #supplies_used}.<p/>
 * <p>TODO IDEA Support for multiple {@link #path}. In this case paths with demand and supplies as base.<p/>
 * <p>TODO Define {@link #path} as an convention regarding the meaning of demands and supplies.</p>
 */
public class AssignmentsI implements Assignments {

    public static Assignments assignments(String name, Database demands, Database supplies) {
        return new AssignmentsI(name, demands, supplies);
    }

    private final String names;
    private final Database assignments;

    private final List<AfterAdditionSubscriber> additionSubscriptions = list();
    private final List<BeforeRemovalSubscriber> beforeRemovalSubscriptions = list();
    private final List<BeforeRemovalSubscriber> afterRemovalSubscriptions = list();

    private final Database supplies;
    private final Database supplies_used;
    private final Database supplies_free;

    private final Database demands;
    private final Database demands_used;
    private final Database demands_free;

    private final Map<Integer, Integer> allocationsIndex_to_usedDemandIndex = map();
    private final Map<Integer, Integer> allocationsIndex_to_usedSupplyIndex = map();

    private final Map<Integer, Set<Integer>> usedDemandIndexes_to_allocationIndexes = map();
    private final Map<Integer, Set<Integer>> usedSupplyIndexes_to_allocationIndexes = map();

    private final Map<Integer, Set<Integer>> usedDemandsIndex_to_usedSuppliesIndex = map();
    private final Map<Integer, Set<Integer>> usedSupplyIndex_to_usedDemandsIndex = map();

    private AssignmentsI(String name, Database demand, Database supply) {
        this.names = name;
        assignments = database2(Language.ALLOCATIONS.value() + "/" + name, demand, concat(demand.headerView(), supply.headerView()));
        // TODO Remove code and comment duplications.
        {
            this.demands = demand;
            demands_free = database2("demands-free", this, demand.headerView());
            demands_used = database2("demands-used", this, demand.headerView());
            demand.rawLinesView().forEach(demands_free::add);
            demand.subscribeToAfterAdditions(demands_free::add);
            demand.subscribeToBeforeRemoval(removalOf -> {
                if (usedDemandIndexes_to_allocationIndexes.containsKey(removalOf.index())) {
                    listWithValuesOf(
                            usedDemandIndexes_to_allocationIndexes.get(removalOf.index()))
                            .forEach(allocationOfDemand -> remove(assignments.rawLinesView().get(allocationOfDemand)));
                }
                if (demands_free.contains(removalOf)) {
                    demands_free.remove(removalOf);
                }
                // TODO FIX Does something needs to be done if the condition is false.
                if (demands_used.contains(removalOf)) {
                    demands_used.remove(removalOf);
                }
            });
        }
        {
            this.supplies = requireNotNull(supply);
            supplies_free = database2("supply-free", this, supply.headerView());
            supplies_used = database2("supply-used", this, supply.headerView());
            supply.rawLinesView().forEach(supplies_free::add);
            supply.subscribeToAfterAdditions(i -> {
                supplies_free.add(i);
            });
            supply.subscribeToBeforeRemoval(removalOf -> {
                if (usedSupplyIndexes_to_allocationIndexes.containsKey(removalOf.index())) {
                    listWithValuesOf
                            (usedSupplyIndexes_to_allocationIndexes.get(removalOf.index()))
                            .forEach(allocationsOfSupply
                                    -> remove(assignments.rawLinesView().get(allocationsOfSupply)));
                }
                if (supplies_free.contains(removalOf)) {
                    supplies_free.remove(removalOf);
                }
                // TODO FIX Does something needs to be done if the condition is false.
                if (supplies_used.contains(removalOf)) {
                    supplies_used.remove(removalOf);
                }
            });
        }
    }

    @Override
    public Database supplies() {
        return supplies;
    }

    @Override
    public Database suppliesUsed() {
        return supplies_used;
    }

    @Override
    public Database suppliesFree() {
        return supplies_free;
    }

    @Override
    public Database demands() {
        return demands;
    }

    @Override
    public Database demandsUsed() {
        return demands_used;
    }

    @Override
    public Database demandsFree() {
        return demands_free;
    }

    @Override
    public Line assign(Line demand, Line supply) {
        if (TRACING) {
            requireNotNull(demand, "Cannot allocate without demand.");
            requireNotNull(supply, "Cannot allocate without supply.");
            logs().append
                    (event(ALLOCATE.value() + PATH_ACCESS_SYMBOL.value() + Assignments.class.getSimpleName()
                                    , path().toString()
                                    , Xml.elementWithChildren(DEMAND.value(), demand.toDom())
                                    , Xml.elementWithChildren(SUPPLY.value(), supply.toDom()))
                            , this
                            , DEBUG
                    );
        }
        if (ENFORCING_UNIT_CONSISTENCY) {
            list(demand.context()).requireContainsOneOf(demands_free, demands);
            list(demand.context()).requireContainsOneOf(demands_free, demands);
            list(supply.context()).requireContainsOneOf(supplies, supplies_free);
            if (demand.index() < demands.rawLinesView().size()) {
                requireNotNull(demands.rawLinesView().get(demand.index()));
            } else if (demand.index() < demands_free.rawLinesView().size()) {
                requireNotNull(demands_free.rawLinesView().get(demand.index()));
            } else if (demand.index() < demands_used.rawLinesView().size()) {
                requireNotNull(demands_used.rawLinesView().get(demand.index()));
            } else {
                throw executionException("A demand with such an index is not known");
            }
            if (supply.index() < supplies.rawLinesView().size()) {
                requireNotNull(supplies.rawLinesView().get(supply.index()));
            } else if (supply.index() < supplies_free.rawLinesView().size()) {
                requireNotNull(supplies_free.rawLinesView().get(supply.index()));
            } else if (supply.index() < supplies_used.rawLinesView().size()) {
                requireNotNull(supplies_used.rawLinesView().get(supply.index()));
            } else {
                throw executionException("A supply with such an index is not known");
            }
            list(supply.context()).requireContainsOneOf(supplies, supplies_free, supplies_used);
            list(demand.context()).requireContainsOneOf(demands, demands_free, demands_used);
            {
                // Multiple allocations per supply or demand are allowed.
                boolean valid = false;
                if (demand.index() < demands_used.rawLinesView().size()) {
                    valid |= demands_used.rawLinesView().get(demand.index()) != null;
                    if (demand.index() < demands_free.rawLinesView().size()) {
                        valid |= demands_free.rawLinesView().get(demand.index()) != null;
                    }
                } else if (demand.index() < demands_free.rawLinesView().size()) {
                    valid |= demands_free.rawLinesView().get(demand.index()) != null;
                    if (demand.index() < demands_used.rawLinesView().size()) {
                        valid |= demands_used.rawLinesView().get(demand.index()) != null;
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
        if (!usedSupplyIndexes_to_allocationIndexes.containsKey(supply.index())) {
            supplies_used.addWithSameHeaderPrefix(supply);
            supplies_free.remove(supply);
        }
        if (!usedDemandIndexes_to_allocationIndexes.containsKey(demand.index())) {
            demands_used.addWithSameHeaderPrefix(demand);
            demands_free.remove(demand);
        }
        {
            allocationsIndex_to_usedDemandIndex.put(allocation.index(), demand.index());
            allocationsIndex_to_usedSupplyIndex.put(allocation.index(), supply.index());
        }
        {
            {
                if (!usedDemandIndexes_to_allocationIndexes.containsKey(demand.index())) {
                    usedDemandIndexes_to_allocationIndexes.put(demand.index(), setOfUniques());
                }
                usedDemandIndexes_to_allocationIndexes.get(demand.index()).add(allocation.index());
                if (!usedSupplyIndexes_to_allocationIndexes.containsKey(supply.index())) {
                    usedSupplyIndexes_to_allocationIndexes.put(supply.index(), setOfUniques());
                }
                usedSupplyIndexes_to_allocationIndexes.get(supply.index()).add(allocation.index());
            }
        }
        {
            {
                if (!usedDemandsIndex_to_usedSuppliesIndex.containsKey(demand.index())) {
                    usedDemandsIndex_to_usedSuppliesIndex.put(demand.index(), setOfUniques());
                }
                usedDemandsIndex_to_usedSuppliesIndex.get(demand.index()).add(supply.index());
            }
            {
                if (!usedSupplyIndex_to_usedDemandsIndex.containsKey(supply.index())) {
                    usedSupplyIndex_to_usedDemandsIndex.put(supply.index(), setOfUniques());
                }
                usedSupplyIndex_to_usedDemandsIndex.get(supply.index()).add(demand.index());
            }
        }
        additionSubscriptions.forEach(listener -> listener.registerAddition(allocation));
        return allocation;
    }

    @Override
    public Line demandOfAssignment(Line allocation) {
        return demands.rawLinesView()
                .get(allocationsIndex_to_usedDemandIndex.get(allocation.index()));
    }

    @Override
    public Line supplyOfAssignment(Line allocation) {
        return supplies.rawLinesView()
                .get(allocationsIndex_to_usedSupplyIndex.get(allocation.index()));
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
            logs().append
                    (Xml.event(REMOVE.value()
                                            + PATH_ACCESS_SYMBOL.value()
                                            + Assignments.class.getSimpleName()
                                    , path().toString()
                                    , Xml.elementWithChildren(ALLOCATION.value()
                                            , allocation.toDom())
                                    , Xml.elementWithChildren(DEMAND.value()
                                            , demand.toDom())
                                    , Xml.elementWithChildren(SUPPLY.value()
                                            , supply.toDom()))
                            , this
                            , DEBUG
                    );
        }
        if (ENFORCING_UNIT_CONSISTENCY) {
            list(demand.context()).requireContainsOneOf(demands, demands_used);
            list(supply.context()).requireContainsOneOf(supplies, supplies_used);
            requireEquals(allocation.context(), assignments);
            usedDemandIndexes_to_allocationIndexes.get(demand.index()).requirePresenceOf(allocation.index());
            usedSupplyIndexes_to_allocationIndexes.get(supply.index()).requirePresenceOf(allocation.index());
            requireEquals(allocationsIndex_to_usedDemandIndex.get(allocation.index()), demand.index());
            requireEquals(allocationsIndex_to_usedSupplyIndex.get(allocation.index()), supply.index());
        }
        beforeRemovalSubscriptions.forEach(subscriber -> subscriber.registerBeforeRemoval(allocation));
        assignments.remove(allocation);
        // TODO Make following code a remove subscription to allocations.
        {
            allocationsIndex_to_usedDemandIndex.remove(allocation.index());
            allocationsIndex_to_usedSupplyIndex.remove(allocation.index());
        }
        {
            {
                usedDemandsIndex_to_usedSuppliesIndex.get(demand.index()).remove(supply.index());
                if (usedDemandsIndex_to_usedSuppliesIndex.get(demand.index()).isEmpty()) {
                    usedDemandsIndex_to_usedSuppliesIndex.remove(demand.index());
                }
                usedSupplyIndex_to_usedDemandsIndex.get(supply.index()).remove(demand.index());
                if (usedSupplyIndex_to_usedDemandsIndex.get(supply.index()).isEmpty()) {
                    usedSupplyIndex_to_usedDemandsIndex.remove(supply.index());
                }
            }
            {
                usedSupplyIndexes_to_allocationIndexes.get(supply.index()).remove(allocation.index());
                if (usedSupplyIndexes_to_allocationIndexes.get(supply.index()).isEmpty()) {
                    usedSupplyIndexes_to_allocationIndexes.remove(supply.index());
                }
                usedDemandIndexes_to_allocationIndexes.get(demand.index()).remove(allocation.index());
                if (usedDemandIndexes_to_allocationIndexes.get(demand.index()).isEmpty()) {
                    usedDemandIndexes_to_allocationIndexes.remove(demand.index());
                }
            }
        }
        allocationsIndex_to_usedDemandIndex.remove(allocation.index());
        allocationsIndex_to_usedSupplyIndex.remove(allocation.index());
        if (!usedDemandsIndex_to_usedSuppliesIndex.containsKey(demand.index())) {
            demands_used.remove(demand);
            demands_free.addWithSameHeaderPrefix(demand);
        }
        if (!usedSupplyIndex_to_usedDemandsIndex.containsKey(supply.index())) {
            supplies_used.remove(supply);
            supplies_free.addWithSameHeaderPrefix(supply);
        }
        afterRemovalSubscriptions.forEach(listener -> listener.registerBeforeRemoval(allocation));
    }

    @Override
    public void subscribeToAfterAdditions(AfterAdditionSubscriber subscriber) {
        additionSubscriptions.add(subscriber);
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
        try {
            remove(assignments.rawLinesView().get(lineIndex));
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public void subscribeToAfterRemoval(BeforeRemovalSubscriber subscriber) {
        afterRemovalSubscriptions.add(subscriber);
        throw notImplementedYet("This method is not correct: the argument type is completely of.");
    }

    @Override
    public Set<Line> assignmentsOfSupply(Line supply) {
        if (ENFORCING_UNIT_CONSISTENCY) {
            if (!usedSupplyIndexes_to_allocationIndexes.containsKey(supply.index())) {
                throw executionException(perspective("No allocations for the given supply are present.")
                        .withProperty("supply index", "" + supply.index())
                        .withProperty("context path", "" + supply.context().path())
                );
            }
        }
        final Set<Line> allocationsOfSupply = setOfUniques();
        usedSupplyIndexes_to_allocationIndexes
                .get(supply.index())
                .forEach(allocationIndex ->
                        allocationsOfSupply.add(assignments.rawLinesView().get(allocationIndex)));
        return allocationsOfSupply;
    }

    @Override
    public Set<Line> assignmentsOfDemand(Line demand) {
        if (ENFORCING_UNIT_CONSISTENCY) {
            try {
                setOfUniques(usedDemandIndexes_to_allocationIndexes.keySet()).requirePresenceOf(demand.index());
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
        final Set<Line> allocationsOfDemand = setOfUniques();
        usedDemandIndexes_to_allocationIndexes
                .get(demand.index())
                .forEach(allocationIndex ->
                        allocationsOfDemand.add(assignments.rawLinesView().get(allocationIndex)));
        return allocationsOfDemand;
    }

    @Override
    public Line anyAssignmentOf(LinePointer demand, LinePointer supply) {
        if (ENFORCING_UNIT_CONSISTENCY) {
            usedDemandIndexes_to_allocationIndexes
                    .get(demand.index())
                    .assertSizeIs(1);
            usedSupplyIndexes_to_allocationIndexes
                    .get(supply.index())
                    .assertSizeIs(1);
            final var demandLine = usedDemandIndexes_to_allocationIndexes
                    .get(demand.index())
                    .iterator()
                    .next();
            final var supplyLine = usedSupplyIndexes_to_allocationIndexes
                    .get(supply.index())
                    .iterator()
                    .next();
            Integers.requireEqualInts(demandLine, supplyLine);
        }
        return assignments.rawLine(
                usedDemandIndexes_to_allocationIndexes
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
        return assignments.path().withAppended(names);
    }

    @Override
    public Element toDom() {
        final var dom = Xml.elementWithChildren(Assignments.class.getSimpleName());
        dom.appendChild(textNode(path().toString()));
        rawLinesView().stream()
                .filter(line -> line != null)
                .forEach(line -> dom.appendChild(line.toDom()));
        return dom;
    }

    @Override
    public Perspective toPerspective() {
        final var dom = perspective(Assignments.class.getSimpleName());
        dom.withChild(perspective(path().toString()));
        rawLinesView().stream()
                .filter(line -> line != null)
                .forEach(line -> dom.withChild(line.toPerspective()));
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
    public Object identity() {
        return this;
    }

    @Override
    public boolean equals(Object arg) {
        if (arg instanceof Assignments) {
            final var castedArg = (Assignments) arg;
            return identity().equals(castedArg.identity());
        }
        throw executionException("Invalid argument type: " + arg);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public Stream<Line> orderedLinesStream() {
        return assignments.orderedLinesStream();
    }
}
