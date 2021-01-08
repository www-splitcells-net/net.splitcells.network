package net.splitcells.gel.constraint.vidējs.dati;

import net.splitcells.gel.rating.tips.Cena;

import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;

public class PiešķiršanaFiltrs {
	private PiešķiršanaFiltrs() {
		throw constructorIllegal();
	}

	public static boolean atlasītArCenu(PiešķiršanaNovērtējums piešķiršanaNovērtējums) {
		return !piešķiršanaNovērtējums.novērtējums().equalz(Cena.bezMaksas());
	}
}
