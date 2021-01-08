package net.splitcells.gel.problem;

import net.splitcells.gel.data.piešķiršanas.Piešķiršanas;
import net.splitcells.gel.solution.Solution;

public interface Problēma extends Piešķiršanas, ProblēmasSkats {
	Solution uzAtrisinājumu();

	Solution kāAtrisinājums();
}
