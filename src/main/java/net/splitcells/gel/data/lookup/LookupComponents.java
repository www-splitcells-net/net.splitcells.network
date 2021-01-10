package net.splitcells.gel.data.lookup;

import java.util.function.Predicate;

import net.splitcells.gel.data.table.Table;

public interface LookupComponents<T> {
	Table lookup(T vertība);

	Table lookup(Predicate<T> predikāts);
}
