package net.splitcells.gel.data.allocation;

import static java.util.Objects.requireNonNull;
import static net.splitcells.dem.lang.Xml.elementWithChildren;
import static net.splitcells.dem.lang.Xml.textNode;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.list.Lists.concat;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.data.set.map.Maps.map;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.util.Map;
import java.util.Set;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.ListView;
import net.splitcells.dem.lang.Xml;
import net.splitcells.gel.common.Language;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.data.table.column.Column;
import net.splitcells.gel.data.table.column.ColumnView;
import org.w3c.dom.Element;
import net.splitcells.gel.data.database.AfterAdditionSubscriber;
import net.splitcells.gel.data.database.Database;
import net.splitcells.gel.data.database.DatabaseI;
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
    protected final Map<Integer, Set<Integer>> usedSupplyIndex_to_usedDeamndsIndex = map();

    @Deprecated
    protected AllocationsI(String name, Database demand, Database supply) {
        this.names = name;
        allocations = new DatabaseI(Language.ALLOCATIONS.value(), this, concat(demand.headerView(), supply.headerView()));
        // TODO Remove code and comment duplications.
        {
            this.demands = demand;
            demands_free = new DatabaseI("demands-free", this, demand.headerView());
            demands_used = new DatabaseI("demands-used", this, demand.headerView());
            demand.rawLinesView().forEach(demands_free::add);
            demand.subscribeToAfterAdditions(demands_free::add);
            demand.subscriberToBeforeRemoval(removalOf -> {
                if (usedDemandIndexes_to_allocationIndexes.containsKey(removalOf.index())) {
                    listWithValuesOf(
                            usedDemandIndexes_to_allocationIndexes.get(removalOf.index()))
                            .forEach(allocation_of_demand -> remove(allocations.rawLinesView().get(allocation_of_demand)));
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
            supplies_free = new DatabaseI("supply-free", this, supply.headerView());
            supplies_used = new DatabaseI("supply-used", this, supply.headerView());
            supply.rawLinesView().forEach(supplies_free::add);
            supply.subscribeToAfterAdditions(i -> {
                supplies_free.add(i);
            });
            supply.subscriberToBeforeRemoval(noņemšanaNo -> {
                if (usedSupplyIndexes_to_allocationIndexes.containsKey(noņemšanaNo.index())) {
                    listWithValuesOf
                            (usedSupplyIndexes_to_allocationIndexes.get(noņemšanaNo.index()))
                            .forEach(piešķiršanas_no_piedāvāijumu
                                    -> remove(allocations.rawLinesView().get(piešķiršanas_no_piedāvāijumu)));
                }
                if (supplies_free.contains(noņemšanaNo)) {
                    supplies_free.remove(noņemšanaNo);
                }
                // TODO FIX Does something needs to be done if the condition is false.
                if (supplies_used.contains(noņemšanaNo)) {
                    supplies_used.remove(noņemšanaNo);
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
    public Database demandsUnused() {
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
                if (!usedSupplyIndex_to_usedDeamndsIndex.containsKey(supply.index())) {
                    usedSupplyIndex_to_usedDeamndsIndex.put(supply.index(), setOfUniques());
                }
                usedSupplyIndex_to_usedDeamndsIndex.get(supply.index()).add(demand.index());
            }
        }
        additionSubscriptions.forEach(listener -> listener.register_addition(allocation));
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
        beforeRemovalSubscriptions.forEach(subscriber -> subscriber.register_before_removal(allocation));
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
                usedSupplyIndex_to_usedDeamndsIndex.get(supply.index()).remove(demand.index());
                if (usedSupplyIndex_to_usedDeamndsIndex.get(supply.index()).isEmpty()) {
                    usedSupplyIndex_to_usedDeamndsIndex.remove(supply.index());
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
        if (!usedSupplyIndex_to_usedDeamndsIndex.containsKey(supply.index())) {
            supplies_used.remove(supply);
            supplies_free.add(supply);
        }
        afterRemovalSubscriptions.forEach(listener -> listener.register_before_removal(allocation));
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
    public <T> ColumnView<T> columnView(Attribute<T> attributes) {
        return allocations.columnView(attributes);
    }

    @Override
    public ListView<Line> rawLinesView() {
        return allocations.rawLinesView();
    }

    @Override
    public void subscriberToBeforeRemoval(BeforeRemovalSubscriber subscriber) {
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
    public void subscriberToAfterRemoval(BeforeRemovalSubscriber subscriber) {
        afterRemovalSubscriptions.add(subscriber);
    }

    @Override
    public Set<Line> allocationsOfSupply(Line supply) {
        final Set<Line> piešķiršanas_no_piedāvājuma = setOfUniques();
        try {
            usedSupplyIndexes_to_allocationIndexes
                    .get(supply.index())
                    .forEach(piešķiršanasIndekss ->
                            piešķiršanas_no_piedāvājuma.add(allocations.rawLinesView().get(piešķiršanasIndekss)));
        } catch (RuntimeException e) {
            throw e;
        }
        return piešķiršanas_no_piedāvājuma;
    }

    @Override
    public Set<Line> allocationsOfDemand(Line demand) {
        final Set<Line> allocations_of_demand = setOfUniques();
        usedDemandIndexes_to_allocationIndexes
                .get(demand.index())
                .forEach(piešķiršanasIndekss ->
                    allocations_of_demand.add(allocations.rawLinesView().get(piešķiršanasIndekss)));
        return allocations_of_demand;
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
        final net.splitcells.dem.data.set.list.List<String> path = list();
        path.addAll(demands.path());
        path.add(names);
        return path;
    }

    @Override
    public Element toDom() {
        final var dom = Xml.elementWithChildren(Allocations.class.getSimpleName());
        dom.appendChild(textNode(path().toString()));
        rawLinesView().stream()
                .filter(rinda -> rinda != null)
                .forEach(rinda -> dom.appendChild(rinda.toDom()));
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
}
