package net.splitcells.gel.problem;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.data.table.attribute.Attribute;

public interface Define_Demand_Attributes {
	DefineDemands withDemandAttributes(Attribute<?>... parsībasNosaukumi);

	DefineDemands withDemandAttributes(List<Attribute<?>> parsībasNosaukumi);

}
