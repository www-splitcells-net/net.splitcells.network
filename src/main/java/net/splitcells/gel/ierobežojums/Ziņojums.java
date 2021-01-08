package net.splitcells.gel.ierobežojums;

import net.splitcells.gel.dati.tabula.Rinda;
import net.splitcells.gel.novērtējums.struktūra.Novērtējums;

public final class Ziņojums {
	public static Ziņojums report(Rinda rinda, GrupaId grupa, Novērtējums novērtējums) {
		return new Ziņojums(rinda, grupa, novērtējums);
	}

	private Rinda rinda;
	private GrupaId group;
	private Novērtējums novērtējums;

	private Ziņojums(Rinda rinda, GrupaId grupa, Novērtējums novērtējums) {
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

	public Novērtējums novērtējums() {
		return novērtējums;
	}
}
