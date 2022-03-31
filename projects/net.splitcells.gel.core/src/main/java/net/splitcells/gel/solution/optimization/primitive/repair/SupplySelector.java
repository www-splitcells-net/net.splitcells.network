package net.splitcells.gel.solution.optimization.primitive.repair;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.solution.optimization.OnlineOptimization;

import java.util.function.Function;

/**
 * Assigns supplies to given free demands,
 * which are grouped by {@link GroupId}.
 */
@FunctionalInterface
public interface SupplySelector extends Function<Map<GroupId, Set<Line>>, OnlineOptimization> {
    OnlineOptimization apply(Map<GroupId, Set<Line>> freeDemandGroups);
}
