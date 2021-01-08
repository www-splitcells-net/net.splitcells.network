package net.splitcells.gel.problem;

import net.splitcells.gel.data.piešķiršanas.Piešķiršanas;
import net.splitcells.gel.solution.Optimization;

public interface Problēma extends Piešķiršanas, ProblēmasSkats {
	Optimization uzAtrisinājumu();

	Optimization kāAtrisinājums();
}
