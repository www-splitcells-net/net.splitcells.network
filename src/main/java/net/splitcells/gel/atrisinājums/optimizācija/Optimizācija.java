package net.splitcells.gel.atrisinājums.optimizācija;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.atrisinājums.AtrisinājumaSkats;

@FunctionalInterface
public interface Optimizācija {

	List<OptimizācijasNotikums> optimizē(AtrisinājumaSkats atrisinājums);

}
