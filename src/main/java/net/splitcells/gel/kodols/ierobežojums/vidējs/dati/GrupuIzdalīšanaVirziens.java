package net.splitcells.gel.kodols.ierobežojums.vidējs.dati;

import net.splitcells.gel.kodols.ierobežojums.Ierobežojums;
import net.splitcells.gel.kodols.ierobežojums.GrupaId;

public class GrupuIzdalīšanaVirziens {
	public static GrupuIzdalīšanaVirziens routingResult(GrupaId grupa, Ierobežojums izplatītājs) {
		return new GrupuIzdalīšanaVirziens(grupa, izplatītājs);
	}

	private final GrupaId grupa;
	private final Ierobežojums izplatītājs;

	private GrupuIzdalīšanaVirziens(GrupaId grupa, Ierobežojums izplatītājs) {
		this.grupa = grupa;
		this.izplatītājs = izplatītājs;
	}

	public GrupaId grupa() {
		return grupa;
	}

	public Ierobežojums izplatītājs() {
		return izplatītājs;
	}
}
