package net.splitcells.gel.constraint.intermediate.data;

import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.constraint.Constraint;

public class RoutingResult {
	public static RoutingResult routingResult(GroupId grupa, Constraint izplatītājs) {
		return new RoutingResult(grupa, izplatītājs);
	}

	private final GroupId grupa;
	private final Constraint izplatītājs;

	private RoutingResult(GroupId grupa, Constraint izplatītājs) {
		this.grupa = grupa;
		this.izplatītājs = izplatītājs;
	}

	public GroupId grupa() {
		return grupa;
	}

	public Constraint propagation() {
		return izplatītājs;
	}
}
