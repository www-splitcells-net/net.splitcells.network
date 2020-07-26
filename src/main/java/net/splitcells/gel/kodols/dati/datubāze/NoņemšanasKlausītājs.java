package net.splitcells.gel.kodols.dati.datubāze;

import java.util.Collection;

import net.splitcells.gel.kodols.dati.tabula.Rinda;

@FunctionalInterface
public interface NoņemšanasKlausītājs {
	void rēgistrē_noņemšanas(Rinda rinda);

	default void rēgistrē_noņemšanas(Collection<Rinda> rindas) {
		rindas.forEach(line -> rēgistrē_noņemšanas(line));
	}

}
