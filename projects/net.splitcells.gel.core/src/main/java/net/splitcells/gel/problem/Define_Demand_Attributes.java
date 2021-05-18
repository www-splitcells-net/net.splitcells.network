package net.splitcells.gel.problem;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.data.database.Database;
import net.splitcells.gel.data.table.attribute.Attribute;

public interface Define_Demand_Attributes {
    DefineDemands withDemandAttributes(Attribute<?>... demandAttributes);

    DefineDemands withDemandAttributes(List<Attribute<?>> demandAttributes);

    DefineSupply withDemands(Database demands);
}
