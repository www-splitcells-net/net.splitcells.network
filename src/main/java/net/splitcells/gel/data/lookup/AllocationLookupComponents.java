package net.splitcells.gel.data.lookup;

import java.util.function.Predicate;

import net.splitcells.gel.data.allocation.Allocations;

public interface AllocationLookupComponents<T> extends LookupComponents<T> {
	Allocations uzmeklēšana(T vertība);

	Allocations uzmeklēšana(Predicate<T> predikāts);
}
