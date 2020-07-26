package net.splitcells.gel.kodols.ierobežojums.vidējs.dati;

import net.splitcells.gel.kodols.dati.tabula.Rinda;
import net.splitcells.gel.kodols.novērtējums.struktūra.RefleksijaNovērtējums;

public class PiešķiršanaNovērtējums {
	public static PiešķiršanaNovērtējums rindasNovērtējums(Rinda rinda, RefleksijaNovērtējums novērtējums) {
		return new PiešķiršanaNovērtējums(rinda, novērtējums);
	}

	private final Rinda rinda;
	private final RefleksijaNovērtējums novērtējums;

	private PiešķiršanaNovērtējums(Rinda rinda, RefleksijaNovērtējums novērtējums) {
		this.rinda = rinda;
		this.novērtējums = novērtējums;
	}

	public Rinda rinda() {
		return rinda;
	}

	public RefleksijaNovērtējums novērtējums() {
		return novērtējums;
	}
}
