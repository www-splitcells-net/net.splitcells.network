package net.splitcells.gel.kodols.problēma;

import net.splitcells.gel.kodols.dati.piešķiršanas.Piešķiršanas;
import net.splitcells.gel.kodols.atrisinājums.Atrisinājums;

public interface Problēma extends Piešķiršanas, ProblēmasSkats {
	Atrisinājums uzProblēma();

	Atrisinājums kāProblēma();
}
