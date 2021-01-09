package net.splitcells.gel.constraint;

import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.rating.structure.Rating;

public final class Ziņojums {
	public static Ziņojums report(Line rinda, GrupaId grupa, Rating novērtējums) {
		return new Ziņojums(rinda, grupa, novērtējums);
	}

	private Line rinda;
	private GrupaId group;
	private Rating novērtējums;

	private Ziņojums(Line rinda, GrupaId grupa, Rating novērtējums) {
		this.rinda = rinda;
		this.group = grupa;
		this.novērtējums = novērtējums;
	}

	public Line rinda() {
		return rinda;
	}

	public GrupaId grupa() {
		return group;
	}

	public Rating novērtējums() {
		return novērtējums;
	}
}
