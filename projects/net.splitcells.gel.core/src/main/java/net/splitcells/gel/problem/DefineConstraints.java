package net.splitcells.gel.problem;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.constraint.Query;
import net.splitcells.gel.constraint.QueryI;

import java.util.function.Function;

import static net.splitcells.gel.constraint.type.ForAlls.forAll;

public interface DefineConstraints {
    ProblemGenerator withConstraint(Constraint constraint);

    default ProblemGenerator withConstraints(List<Function<Query, Query>> builders) {
        final var root = forAll();
        builders.forEach(b -> b.apply(QueryI.query(root)));
        return withConstraint(root);
    }

    default ProblemGenerator withConstraint(Function<Query, Query> builder) {
        return withConstraint(builder.apply(QueryI.query(forAll())).currentConstraint());
    }
}
