package net.splitcells.gel.data.uzmeklēšana;

import java.util.function.Predicate;

import net.splitcells.gel.data.table.Tabula;

public interface UzmeklēšanaKomponents<T> {
	Tabula uzmeklēšana(T vertība);

	Tabula uzmeklēšana(Predicate<T> predikāts);
}
