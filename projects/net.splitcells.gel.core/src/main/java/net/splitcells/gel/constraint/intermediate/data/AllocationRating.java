/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.constraint.intermediate.data;

import net.splitcells.gel.data.view.Line;
import net.splitcells.gel.rating.framework.Rating;

public class AllocationRating {
	public static AllocationRating lineRating(Line line, Rating rating) {
		return new AllocationRating(line, rating);
	}

	private final Line line;
	private final Rating rating;

	private AllocationRating(Line line, Rating rating) {
		this.line = line;
		this.rating = rating;
	}

	public Line line() {
		return line;
	}

	public Rating rating() {
		return rating;
	}
}
