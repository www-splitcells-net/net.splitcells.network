package net.splitcells.gel.problem;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.data.table.attribute.Attribute;

public interface Define_Demand_Attributes {
	DefineDemands arPrasībasNosaukumiem(Attribute<?>... parsībasNosaukumi);

	DefineDemands arPrasībasNosaukumiem(List<Attribute<?>> parsībasNosaukumi);

}
