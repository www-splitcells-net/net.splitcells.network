package net.splitcells.gel.solution.optimization;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.solution.AtrisinājumaSkats;

@FunctionalInterface
public interface Optimizācija {

	List<OptimizācijasNotikums> optimizē(AtrisinājumaSkats atrisinājums);

}
