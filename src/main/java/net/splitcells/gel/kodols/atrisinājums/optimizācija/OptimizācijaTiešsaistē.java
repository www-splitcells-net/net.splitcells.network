package net.splitcells.gel.kodols.atrisinājums.optimizācija;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.kodols.atrisinājums.Atrisinājums;

@Deprecated
@FunctionalInterface
public interface OptimizācijaTiešsaistē {

	List<OptimizācijasNotikums> optimizē(Atrisinājums atrisinājums);

}
