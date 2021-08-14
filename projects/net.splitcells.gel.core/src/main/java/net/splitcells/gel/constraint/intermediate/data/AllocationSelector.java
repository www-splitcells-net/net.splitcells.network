/*
 * Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License,
 * which is available at https://spdx.org/licenses/MIT.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
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
