package net.splitcells.gel.data.allocations;

import net.splitcells.dem.environment.config.StaticFlags;
import net.splitcells.dem.lang.Xml;
import net.splitcells.gel.data.allocation.Allocations;
import net.splitcells.gel.data.allocation.AllocationsI;
import net.splitcells.gel.data.database.Database;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.data.table.column.ColumnView;

import java.util.Set;

import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.environment.config.StaticFlags.ENFORCING_UNIT_CONSISTENCY;
import static net.splitcells.dem.environment.config.StaticFlags.TRACING;
import static net.splitcells.dem.lang.Xml.elementWithChildren;
import static net.splitcells.dem.lang.Xml.event;
import static net.splitcells.dem.resource.host.interaction.Domsole.domsole;
import static net.splitcells.dem.resource.host.interaction.LogLevel.DEBUG;
import static net.splitcells.gel.common.Language.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

/**
 * {@link #demandsUsed()} ()} and {@link #demandsUnused()} contain all {@link Line} of {@link #demands()}.
 * {@link Line} with the same conceptional identity und different {@link Database} contexts have the same {@link Line#index()}.
 * The same applies to {@link #supplies()}.
 * <p/>
 * Line removal from {@link #demands_free} and {@link #supplies_free} has no subscriptions,
 * because {@link Database} lines can be remove from the {@link Allocations} completely
 * or they can be moved to the respectively used tables.
 * <p/>
 * TODO Fix {@link #demandOfAllocation(Line)} by using {@link #demands_used}.
 * <p/>
 * TODO Fix {@link #supplyOfAllocation} by using {@link #supplies_used}.
 * <p/>
 * TODO {@link #add(Line)}: The input argument has to be split into a supply and a demand part and a fitting supply and
 * demand pair already would be searched and used on {@code allocate(supply, demand)}.
 * If a fitting supply and demand pair does not exit the input is invalid.
 * <p/>
 * TODO {@link #path}: Support for multiple path. In this case paths with demand and supplies as base.
 * <p/>
 * TODO {@link #path}: Define this as an convention regarding the meaning of demands and supplies.
 */
public class AllocationsIRef extends AllocationsI {
    /**
     * TODO FIX Make constructor private.
     *
     * @param name
     * @param demand
     * @param supplies
     */
    public AllocationsIRef(String name, Database demand, Database supplies) {
        super(name, demand, supplies);
    }

    @Override
    public Line allocate(Line demand, Line supply) {
        if (TRACING) {
            assertThat(demand).withFailMessage("Cannot allocate without demand.").isNotNull();
            assertThat(supply).withFailMessage("Cannot allocate without supply.").isNotNull();
            domsole().append
                    (event(ALLOCATE.value() + PATH_ACCESS_SYMBOL.value() + Allocations.class.getSimpleName()
                            , path().toString()
                            , Xml.elementWithChildren(DEMAND.value(), demand.toDom())
                            , Xml.elementWithChildren(SUPPLY.value(), supply.toDom()))
                            , this
                            , DEBUG
                    );
        }
        if (ENFORCING_UNIT_CONSISTENCY) {
            assertThat(list(demand.context())).containsAnyOf(demands_free, demands);
            assertThat(list(supply.context())).containsAnyOf(supplies, supplies_free);
            assertThat(demands.rawLinesView().get(demand.index())).isNotNull();
            assertThat(supplies.rawLinesView().get(supply.index())).isNotNull();
            assertThat(supply.context()).isIn(setOfUniques(supplies, supplies_free, supplies_used));
            assertThat(demand.context()).isIn(setOfUniques(demands, demands_free, demands_used));
            if (usedDemandIndexes_to_allocationIndexes.containsKey(demand.index())
                    && usedSupplyIndexes_to_allocationIndexes.containsKey(supply.index())) {
                final var allocationIndexes_of_demand
                        = usedDemandIndexes_to_allocationIndexes.get(demand.index());
                final var allocationIndexes_of_supply
                        = usedSupplyIndexes_to_allocationIndexes.get(supply.index());
                // Checks if there is already an allocation.
                for (final var allocationIndex_of_demand : allocationIndexes_of_demand) {
                    if (allocationIndexes_of_supply.contains(allocationIndex_of_demand)) {
                        /** TODO IDEA Support multiple and partial allocations between {@link Demand} and {@link Supply}.
                         */
                        fail("The demand " + demand.index() + "and the supply" + supply.index() + "are already allocated to each other. Multiple assignments to same variables are currently not supported.");
                    }
                }
            }
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
        return super.allocate(demand, supply);
    }

    @Override
    public void remove(Line allocation) {
        final var demand = demandOfAllocation(allocation);
        final var supply = supplyOfAllocation(allocation);
        if (TRACING) {
            domsole().append
                    (Xml.event(REMOVE.value()
                                    + PATH_ACCESS_SYMBOL.value()
                                    + Allocations.class.getSimpleName()
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
            assertThat(list(demand.context())).containsAnyOf(demands, demands_used);
            assertThat(list(supply.context())).containsAnyOf(supplies, supplies_used);
            assertThat(allocation.context()).isEqualTo(allocations);
            assertThat(usedDemandIndexes_to_allocationIndexes.get(demand.index())).contains(allocation.index());
            assertThat(usedSupplyIndexes_to_allocationIndexes.get(supply.index())).contains(allocation.index());
            assertThat(allocationsIndex_to_usedDemandIndex.get(allocation.index())).isEqualTo(demand.index());
            assertThat(allocationsIndex_to_usedSupplyIndex.get(allocation.index())).isEqualTo(supply.index());
        }
        super.remove(allocation);
    }

    @Override
    public <T> ColumnView<T> columnView(Attribute<T> attribute) {
        if (ENFORCING_UNIT_CONSISTENCY) {
            assert demands.headerView().contains(attribute) || supplies.headerView().contains(attribute);
        }
        return super.columnView(attribute);
    }

    @Override
    public Set<Line> allocationsOfSupply(Line supply) {
        if (StaticFlags.ENFORCING_UNIT_CONSISTENCY) {
            assertThat(usedSupplyIndexes_to_allocationIndexes)
                    .describedAs("No allocations for the given supply are present.")
                    .containsKey(supply.index());
        }
        return super.allocationsOfSupply(supply);
    }

    @Override
    public Set<Line> allocationsOfDemand(Line demand) {
        if (StaticFlags.ENFORCING_UNIT_CONSISTENCY) {
            try {
                assertThat(usedDemandIndexes_to_allocationIndexes).containsKey(demand.index());
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
        return super.allocationsOfDemand(demand);
    }
}
