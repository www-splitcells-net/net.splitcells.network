package net.splitcells.gel.data.uzmeklēšana;

import java.util.function.Predicate;

import net.splitcells.gel.data.piešķiršanas.Piešķiršanas;

public interface PiešķiršanaUzmeklēšanaKomponents<T> extends UzmeklēšanaKomponents<T> {
	Piešķiršanas uzmeklēšana(T vertība);

	Piešķiršanas uzmeklēšana(Predicate<T> predikāts);
}
