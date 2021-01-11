package net.splitcells.gel.constraint;

import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.rating.structure.Rating;

public final class Report {
	public static Report report(Line rinda, GroupId grupa, Rating novērtējums) {
		return new Report(rinda, grupa, novērtējums);
	}

	private Line rinda;
	private GroupId group;
	private Rating novērtējums;

	private Report(Line rinda, GroupId grupa, Rating novērtējums) {
		this.rinda = rinda;
		this.group = grupa;
		this.novērtējums = novērtējums;
	}

	public Line line() {
		return rinda;
	}

	public GroupId group() {
		return group;
	}

	public Rating novērtējums() {
		return novērtējums;
	}
}
