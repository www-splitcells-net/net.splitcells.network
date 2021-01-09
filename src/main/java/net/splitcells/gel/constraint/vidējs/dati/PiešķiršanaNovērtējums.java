package net.splitcells.gel.constraint.vidējs.dati;

import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.rating.structure.MetaRating;

public class PiešķiršanaNovērtējums {
	public static PiešķiršanaNovērtējums rindasNovērtējums(Line rinda, MetaRating novērtējums) {
		return new PiešķiršanaNovērtējums(rinda, novērtējums);
	}

	private final Line rinda;
	private final MetaRating novērtējums;

	private PiešķiršanaNovērtējums(Line rinda, MetaRating novērtējums) {
		this.rinda = rinda;
		this.novērtējums = novērtējums;
	}

	public Line rinda() {
		return rinda;
	}

	public MetaRating novērtējums() {
		return novērtējums;
	}
}
