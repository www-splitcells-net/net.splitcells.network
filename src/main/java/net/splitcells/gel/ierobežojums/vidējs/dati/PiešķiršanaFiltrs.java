package net.splitcells.gel.ierobežojums.vidējs.dati;

import net.splitcells.gel.novērtējums.tips.Cena;

import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;

public class PiešķiršanaFiltrs {
	private PiešķiršanaFiltrs() {
		throw constructorIllegal();
	}

	public static boolean atlasītArCenu(PiešķiršanaNovērtējums piešķiršanaNovērtējums) {
		return !piešķiršanaNovērtējums.novērtējums().equalz(Cena.bezMaksas());
	}
}
