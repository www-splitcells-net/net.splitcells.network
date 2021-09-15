package net.splitcells.gel.solution.optimization.primitive.repair;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.solution.optimization.Optimization;

import java.util.function.BiFunction;

@FunctionalInterface
public interface SupplySelector extends BiFunction<Map<GroupId, Set<Line>>, List<Line>, Optimization> {
    Optimization apply(Map<GroupId, Set<Line>> freeDemandGroups, List<Line> freeSupplies);
}
