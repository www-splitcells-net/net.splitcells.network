package net.splitcells.gel.constraint.vidējs.dati;

import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.constraint.Constraint;

public class GrupuIzdalīšanaVirziens {
	public static GrupuIzdalīšanaVirziens routingResult(GroupId grupa, Constraint izplatītājs) {
		return new GrupuIzdalīšanaVirziens(grupa, izplatītājs);
	}

	private final GroupId grupa;
	private final Constraint izplatītājs;

	private GrupuIzdalīšanaVirziens(GroupId grupa, Constraint izplatītājs) {
		this.grupa = grupa;
		this.izplatītājs = izplatītājs;
	}

	public GroupId grupa() {
		return grupa;
	}

	public Constraint izplatītājs() {
		return izplatītājs;
	}
}
