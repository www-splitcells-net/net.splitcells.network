package net.splitcells.gel.constraint;

import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.rating.structure.Rating;

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
