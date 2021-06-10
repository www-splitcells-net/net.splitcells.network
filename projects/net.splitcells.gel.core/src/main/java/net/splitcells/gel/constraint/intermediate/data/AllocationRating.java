package net.splitcells.gel.constraint.intermediate.data;

import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.rating.framework.MetaRating;

public class AllocationRating {
	public static AllocationRating lineRating(Line line, MetaRating rating) {
		return new AllocationRating(line, rating);
	}

	private final Line line;
	private final MetaRating rating;

	private AllocationRating(Line line, MetaRating rating) {
		this.line = line;
		this.rating = rating;
	}

	public Line line() {
		return line;
	}

	public MetaRating rating() {
		return rating;
	}
}
