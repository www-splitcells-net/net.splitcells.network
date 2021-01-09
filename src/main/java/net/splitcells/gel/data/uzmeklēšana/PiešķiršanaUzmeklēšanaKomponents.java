package net.splitcells.gel.data.uzmeklēšana;

import java.util.function.Predicate;

import net.splitcells.gel.data.allocation.Allocations;

public interface PiešķiršanaUzmeklēšanaKomponents<T> extends UzmeklēšanaKomponents<T> {
	Allocations uzmeklēšana(T vertība);

	Allocations uzmeklēšana(Predicate<T> predikāts);
}
