package net.splitcells.gel.solution.optimization.primitive.repair;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.solution.optimization.OfflineOptimization;

import java.util.Optional;
import java.util.function.BiFunction;

import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.utils.random.RandomnessSource.randomness;
import static net.splitcells.gel.solution.optimization.primitive.SupplySelection.supplySelection;

@FunctionalInterface
public interface SupplySelector extends BiFunction<Map<GroupId, Set<Line>>, List<Line>, OfflineOptimization> {

    OfflineOptimization apply(Map<GroupId, Set<Line>> freeDemandGroups, List<Line> freeSupplies);
}
