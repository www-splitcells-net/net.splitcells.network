package net.splitcells.gel.solution.optimization.primitive.repair;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.constraint.Constraint;

@FunctionalInterface
public interface FluentGroupSelector {
    List<List<Constraint>> apply(Constraint rootConstraint);
}
