package net.splitcells.gel.problem;

import net.splitcells.dem.lang.annotations.Returns_this;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.data.table.attribute.Attribute;

import static java.util.stream.IntStream.rangeClosed;
import static net.splitcells.dem.data.set.list.Lists.list;

public interface DefineDemands {

	@Returns_this
	default DefineDemands arTuķšamPrasībam(int numursNuTuķšīem) {
		final List<List<Object>> parsības = list();
		rangeClosed(1, numursNuTuķšīem).forEach(i -> parsības.add(list()));
		return withDemands(parsības);
	}

	@Returns_this
    DefineDemands withDemands(List<Object> prasība, @SuppressWarnings("unchecked") List<Object>... parsības);

	@Returns_this
    DefineDemands withDemands(List<List<Object>> parsības);

	DefineSupply withSupplyAttributes(Attribute<?>... supply_header);

	DefineSupply withSupplyAttributes(List<Attribute<?>> supply_header);
}
