package net.splitcells.gel.constraint.vidējs.dati;

import net.splitcells.gel.data.tabula.Rinda;
import net.splitcells.gel.rating.structure.MetaRating;

public class PiešķiršanaNovērtējums {
	public static PiešķiršanaNovērtējums rindasNovērtējums(Rinda rinda, MetaRating novērtējums) {
		return new PiešķiršanaNovērtējums(rinda, novērtējums);
	}

	private final Rinda rinda;
	private final MetaRating novērtējums;

	private PiešķiršanaNovērtējums(Rinda rinda, MetaRating novērtējums) {
		this.rinda = rinda;
		this.novērtējums = novērtējums;
	}

	public Rinda rinda() {
		return rinda;
	}

	public MetaRating novērtējums() {
		return novērtējums;
	}
}
