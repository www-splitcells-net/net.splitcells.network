package net.splitcells.gel.constraint.intermediate.data;

import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.constraint.Constraint;

public class RoutingResult {
	public static RoutingResult routingResult(GroupId group, Constraint propagation) {
		return new RoutingResult(group, propagation);
	}

	private final GroupId group;
	private final Constraint propagation;

	private RoutingResult(GroupId group, Constraint propagation) {
		this.group = group;
		this.propagation = propagation;
	}

	public GroupId group() {
		return group;
	}

	public Constraint propagation() {
		return propagation;
	}
}
