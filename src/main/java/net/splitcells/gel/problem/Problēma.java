package net.splitcells.gel.problem;

import net.splitcells.gel.data.piešķiršanas.Piešķiršanas;
import net.splitcells.gel.solution.Atrisinājums;

public interface Problēma extends Piešķiršanas, ProblēmasSkats {
	Atrisinājums uzAtrisinājumu();

	Atrisinājums kāAtrisinājums();
}
