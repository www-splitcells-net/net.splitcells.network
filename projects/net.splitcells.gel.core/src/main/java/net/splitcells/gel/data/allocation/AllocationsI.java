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
package net.splitcells.gel.data.allocation;

import static java.util.Objects.requireNonNull;
import static net.splitcells.dem.lang.Xml.elementWithChildren;
import static net.splitcells.dem.lang.Xml.textNode;
import static net.splitcells.dem.utils.ExecutionException.executionException;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.list.Lists.concat;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.gel.data.database.Databases.database2;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.util.Map;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.ListView;
import net.splitcells.dem.environment.config.StaticFlags;
import net.splitcells.dem.lang.Xml;
import net.splitcells.gel.common.Language;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.LinePointer;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.data.table.column.Column;
import net.splitcells.gel.data.table.column.ColumnView;
import org.w3c.dom.Element;
import net.splitcells.gel.data.database.AfterAdditionSubscriber;
import net.splitcells.gel.data.database.Database;
import net.splitcells.gel.data.database.BeforeRemovalSubscriber;

public class AllocationsI implements Allocations {
    protected final String names;
    protected final Database allocations;

    protected final List<AfterAdditionSubscriber> additionSubscriptions = list();
    protected final List<BeforeRemovalSubscriber> beforeRemovalSubscriptions = list();
    protected final List<BeforeRemovalSubscriber> afterRemovalSubscriptions = list();

    protected final Database supplies;
    protected final Database supplies_used;
    protected final Database supplies_free;

    protected final Database demands;
    protected final Database demands_used;
    protected final Database demands_free;

    protected final Map<Integer, Integer> allocationsIndex_to_usedDemandIndex = map();
    protected final Map<Integer, Integer> allocationsIndex_to_usedSupplyIndex = map();

    protected final Map<Integer, Set<Integer>> usedDemandIndexes_to_allocationIndexes = map();
    protected final Map<Integer, Set<Integer>> usedSupplyIndexes_to_allocationIndexes = map();

    protected final Map<Integer, Set<Integer>> usedDemandsIndex_to_usedSuppliesIndex = map();
    protected final Map<Integer, Set<Integer>> usedSupplyIndex_to_usedDemandsIndex = map();

    @Deprecated
    protected AllocationsI(String name, Database demand, Database supply) {
        this.names = name;
        allocations = database2(Language.ALLOCATIONS.value(), demand, concat(demand.headerView(), supply.headerView()));
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
                            .forEach(allocationOfDemand -> remove(allocations.rawLinesView().get(allocationOfDemand)));
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
            this.supplies = requireNonNull(supply);
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
                                    -> remove(allocations.rawLinesView().get(allocationsOfSupply)));
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
    public Line allocate(Line demand, Line supply) {
        final var allocation = allocations.addTranslated(Line.concat(demand, supply));
        if (!usedSupplyIndexes_to_allocationIndexes.containsKey(supply.index())) {
            supplies_used.add(supply);
            supplies_free.remove(supply);
        }
        if (!usedDemandIndexes_to_allocationIndexes.containsKey(demand.index())) {
            demands_used.add(demand);
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
    public Line demandOfAllocation(Line allocation) {
        return demands.rawLinesView()
                .get(allocationsIndex_to_usedDemandIndex.get(allocation.index()));
    }

    @Override
    public Line supplyOfAllocation(Line allocation) {
        return supplies.rawLinesView()
                .get(allocationsIndex_to_usedSupplyIndex.get(allocation.index()));
    }

    @Override
    public Line addTranslated(List<?> values) {
        throw notImplementedYet();
    }

    @Override
    public Line add(Line line) {
        throw notImplementedYet();
    }

    @Override
    public void remove(Line allocation) {
        final var demand = demandOfAllocation(allocation);
        final var supply = supplyOfAllocation(allocation);
        beforeRemovalSubscriptions.forEach(subscriber -> subscriber.registerBeforeRemoval(allocation));
        allocations.remove(allocation);
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
            demands_free.add(demand);
        }
        if (!usedSupplyIndex_to_usedDemandsIndex.containsKey(supply.index())) {
            supplies_used.remove(supply);
            supplies_free.add(supply);
        }
        afterRemovalSubscriptions.forEach(listener -> listener.registerBeforeRemoval(allocation));
    }

    @Override
    public void subscribeToAfterAdditions(AfterAdditionSubscriber subscriber) {
        additionSubscriptions.add(subscriber);
    }

    @Override
    public List<Attribute<Object>> headerView() {
        return allocations.headerView();
    }

    @Override
    public List<Attribute<? extends Object>> headerView2() {
        return allocations.headerView2();
    }

    @Override
    public <T> ColumnView<T> columnView(Attribute<T> attributes) {
        return allocations.columnView(attributes);
    }

    @Override
    public ListView<Line> rawLinesView() {
        return allocations.rawLinesView();
    }

    @Override
    public void subscribeToBeforeRemoval(BeforeRemovalSubscriber subscriber) {
        beforeRemovalSubscriptions.add(subscriber);
    }

    @Override
    public int size() {
        return allocations.size();
    }

    @Override
    public void remove(int lineIndex) {
        try {
            remove(allocations.rawLinesView().get(lineIndex));
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public void subscribeToAfterRemoval(BeforeRemovalSubscriber subscriber) {
        afterRemovalSubscriptions.add(subscriber);
    }

    @Override
    public Set<Line> allocationsOfSupply(Line supply) {
        final Set<Line> allocationsOfSupply = setOfUniques();
        try {
            usedSupplyIndexes_to_allocationIndexes
                    .get(supply.index())
                    .forEach(allocationIndex ->
                            allocationsOfSupply.add(allocations.rawLinesView().get(allocationIndex)));
        } catch (RuntimeException e) {
            throw e;
        }
        return allocationsOfSupply;
    }

    @Override
    public Set<Line> allocationsOfDemand(Line demand) {
        final Set<Line> allocationsOfDemand = setOfUniques();
        usedDemandIndexes_to_allocationIndexes
                .get(demand.index())
                .forEach(allocationIndex ->
                        allocationsOfDemand.add(allocations.rawLinesView().get(allocationIndex)));
        return allocationsOfDemand;
    }

    @Override
    public Line allocationOf(LinePointer demand, LinePointer supply) {
        if (StaticFlags.ENFORCING_UNIT_CONSISTENCY) {
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
            assertThat(demandLine).isEqualTo(supplyLine);
        }
        return allocations.rawLine(
                usedDemandIndexes_to_allocationIndexes
                        .get(demand.index())
                        .iterator()
                        .next());
    }

    @Override
    public List<Column<Object>> columnsView() {
        return allocations.columnsView();
    }

    @Override
    public String toString() {
        return Allocations.class.getSimpleName() + path().toString();
    }

    @Override
    public net.splitcells.dem.data.set.list.List<String> path() {
        //return allocations.path();
        return net.splitcells.dem.data.set.list.Lists.list(names);
    }

    @Override
    public Element toDom() {
        final var dom = Xml.elementWithChildren(Allocations.class.getSimpleName());
        dom.appendChild(textNode(path().toString()));
        rawLinesView().stream()
                .filter(line -> line != null)
                .forEach(line -> dom.appendChild(line.toDom()));
        return dom;
    }

    @Override
    public List<Line> rawLines() {
        throw notImplementedYet();
    }

    @Override
    public Line lookupEquals(Attribute<Line> atribūts, Line cits) {
        return allocations.lookupEquals(atribūts, cits);
    }

    @Override
    public Object identity() {
        return this;
    }

    @Override
    public boolean equals(Object arg) {
        if (arg instanceof Allocations) {
            final var castedArg = (Allocations) arg;
            return identity().equals(castedArg.identity());
        }
        throw executionException("Invalid argument type: " + arg);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
