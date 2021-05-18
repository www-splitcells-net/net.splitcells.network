package net.splitcells.gel.problem;

import net.splitcells.dem.lang.annotations.Returns_this;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.data.table.attribute.Attribute;

import static java.util.stream.IntStream.rangeClosed;
import static net.splitcells.dem.data.set.list.Lists.list;

public interface DefineDemands extends DefineSupplyAttributes {

    @Returns_this
    default DefineDemands withEmptyDemands(int demandCount) {
        final List<List<Object>> demands = list();
        rangeClosed(1, demandCount).forEach(i -> demands.add(list()));
        return withDemands(demands);
    }

    @Returns_this
    DefineDemands withDemands(List<Object> demand, @SuppressWarnings("unchecked") List<Object>... demands);

    @Returns_this
    DefineDemands withDemands(List<List<Object>> demands);
}
