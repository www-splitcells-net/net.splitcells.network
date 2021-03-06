package net.splitcells.gel.constraint.intermediate.data;

import net.splitcells.gel.rating.type.Cost;

import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;

public class AllocationSelector {
	private AllocationSelector() {
		throw constructorIllegal();
	}

	public static boolean selectLinesWithCost(AllocationRating allocationRating) {
		return !allocationRating.rating().equalz(Cost.noCost());
	}
}
