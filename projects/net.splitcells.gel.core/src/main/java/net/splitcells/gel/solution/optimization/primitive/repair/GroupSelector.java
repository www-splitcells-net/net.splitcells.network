package net.splitcells.gel.solution.optimization.primitive.repair;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.constraint.Constraint;

import java.util.function.Function;

@FunctionalInterface
public interface GroupSelector extends Function<List<List<Constraint>>, List<List<Constraint>>> {
    List<List<Constraint>> apply(List<List<Constraint>> constraintPaths);
}
