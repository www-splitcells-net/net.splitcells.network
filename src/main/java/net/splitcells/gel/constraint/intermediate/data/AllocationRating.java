package net.splitcells.gel.constraint.intermediate.data;

import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.rating.structure.MetaRating;

public class AllocationRating {
	public static AllocationRating rindasNovērtējums(Line rinda, MetaRating novērtējums) {
		return new AllocationRating(rinda, novērtējums);
	}

	private final Line rinda;
	private final MetaRating novērtējums;

	private AllocationRating(Line rinda, MetaRating novērtējums) {
		this.rinda = rinda;
		this.novērtējums = novērtējums;
	}

	public Line rinda() {
		return rinda;
	}

	public MetaRating novērtējums() {
		return novērtējums;
	}
}
