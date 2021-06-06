package net.splitcells.gel.solution.optimization;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.solution.SolutionView;

/**
 * Perfect is the enemy of good -Voltaire
 */
@FunctionalInterface
public interface Optimization {

	List<OptimizationEvent> optimize(SolutionView solution);

}
