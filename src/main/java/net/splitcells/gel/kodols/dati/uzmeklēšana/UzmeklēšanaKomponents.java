package net.splitcells.gel.kodols.dati.uzmeklēšana;

import java.util.function.Predicate;

import net.splitcells.gel.kodols.dati.tabula.Tabula;

public interface UzmeklēšanaKomponents<T> {
	Tabula uzmeklēšana(T vertība);

	Tabula uzmeklēšana(Predicate<T> predikāts);
}
