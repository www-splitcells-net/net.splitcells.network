package net.splitcells.gel.constraint;

import net.splitcells.gel.data.tabula.Rinda;
import net.splitcells.gel.rating.structure.Rating;

public final class Ziņojums {
	public static Ziņojums report(Rinda rinda, GrupaId grupa, Rating novērtējums) {
		return new Ziņojums(rinda, grupa, novērtējums);
	}

	private Rinda rinda;
	private GrupaId group;
	private Rating novērtējums;

	private Ziņojums(Rinda rinda, GrupaId grupa, Rating novērtējums) {
		this.rinda = rinda;
		this.group = grupa;
		this.novērtējums = novērtējums;
	}

	public Rinda rinda() {
		return rinda;
	}

	public GrupaId grupa() {
		return group;
	}

	public Rating novērtējums() {
		return novērtējums;
	}
}
