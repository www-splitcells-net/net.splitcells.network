/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
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
