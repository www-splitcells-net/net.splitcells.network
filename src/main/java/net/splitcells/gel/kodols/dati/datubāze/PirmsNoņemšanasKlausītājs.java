package net.splitcells.gel.kodols.dati.datubāze;

import java.util.Collection;

import net.splitcells.gel.kodols.dati.tabula.Rinda;

@FunctionalInterface
public interface PirmsNoņemšanasKlausītājs {
	void rēgistrē_pirms_noņemšanas(Rinda rinda);

	default void rēgistrē_pirms_noņemšanas(Collection<Rinda> rindas) {
		rindas.forEach(line -> rēgistrē_pirms_noņemšanas(line));
	}

}
