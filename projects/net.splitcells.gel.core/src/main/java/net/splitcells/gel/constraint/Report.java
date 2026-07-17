/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.constraint;

import net.splitcells.gel.data.view.Line;
import net.splitcells.gel.rating.framework.Rating;

public final class Report {
	public static Report report(Line line, GroupId group, Rating rating) {
		return new Report(line, group, rating);
	}

	private Line line;
	private GroupId group;
	private Rating rating;

	private Report(Line line, GroupId group, Rating rating) {
		this.line = line;
		this.group = group;
		this.rating = rating;
	}

	public Line line() {
		return line;
	}

	public GroupId group() {
		return group;
	}

	public Rating rating() {
		return rating;
	}
}
