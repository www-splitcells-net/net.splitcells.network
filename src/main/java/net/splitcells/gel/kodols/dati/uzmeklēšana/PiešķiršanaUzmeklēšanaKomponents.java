package net.splitcells.gel.kodols.dati.uzmeklēšana;

import java.util.function.Predicate;

import net.splitcells.gel.kodols.dati.piešķiršanas.Piešķiršanas;

public interface PiešķiršanaUzmeklēšanaKomponents<T> extends UzmeklēšanaKomponents<T> {
	Piešķiršanas uzmeklēšana(T vertība);

	Piešķiršanas uzmeklēšana(Predicate<T> predikāts);
}
