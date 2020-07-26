package net.splitcells.gel.kodols.atrisinājums.optimizācija;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.kodols.atrisinājums.AtrisinājumaSkats;

@FunctionalInterface
public interface Optimizācija {

	List<OptimizācijasNotikums> optimizē(AtrisinājumaSkats atrisinājums);

}
