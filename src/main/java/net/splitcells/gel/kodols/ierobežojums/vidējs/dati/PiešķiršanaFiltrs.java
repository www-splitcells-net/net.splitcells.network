package net.splitcells.gel.kodols.ierobežojums.vidējs.dati;

import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;
import static net.splitcells.gel.kodols.novērtējums.tips.Cena.bezMaksas;

public class PiešķiršanaFiltrs {
	private PiešķiršanaFiltrs() {
		throw constructorIllegal();
	}

	public static boolean atlasītArCenu(PiešķiršanaNovērtējums piešķiršanaNovērtējums) {
		return !piešķiršanaNovērtējums.novērtējums().equalz(bezMaksas());
	}
}
