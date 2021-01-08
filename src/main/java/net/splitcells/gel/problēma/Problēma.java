package net.splitcells.gel.problēma;

import net.splitcells.gel.dati.piešķiršanas.Piešķiršanas;
import net.splitcells.gel.atrisinājums.Atrisinājums;

public interface Problēma extends Piešķiršanas, ProblēmasSkats {
	Atrisinājums uzAtrisinājumu();

	Atrisinājums kāAtrisinājums();
}
