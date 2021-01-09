package net.splitcells.gel.problem;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.data.table.atribūts.Atribūts;

public interface Define_Demand_Attributes {
	DefineDemands arPrasībasNosaukumiem(Atribūts<?>... parsībasNosaukumi);

	DefineDemands arPrasībasNosaukumiem(List<Atribūts<?>> parsībasNosaukumi);

}
