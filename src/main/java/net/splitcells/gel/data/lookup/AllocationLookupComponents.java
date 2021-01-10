package net.splitcells.gel.data.lookup;

import java.util.function.Predicate;

import net.splitcells.gel.data.allocation.Allocations;

public interface AllocationLookupComponents<T> extends LookupComponents<T> {
	Allocations lookup(T vertība);

	Allocations lookup(Predicate<T> predikāts);
}
