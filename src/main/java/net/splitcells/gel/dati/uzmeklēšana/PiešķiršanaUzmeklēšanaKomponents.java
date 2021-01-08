package net.splitcells.gel.dati.uzmeklēšana;

import java.util.function.Predicate;

import net.splitcells.gel.dati.piešķiršanas.Piešķiršanas;

public interface PiešķiršanaUzmeklēšanaKomponents<T> extends UzmeklēšanaKomponents<T> {
	Piešķiršanas uzmeklēšana(T vertība);

	Piešķiršanas uzmeklēšana(Predicate<T> predikāts);
}
