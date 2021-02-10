package net.splitcells.gel.problem;


import net.splitcells.dem.lang.annotations.Returns_this;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.constraint.Query;
import net.splitcells.gel.constraint.QueryI;

import java.util.function.Function;

import static java.util.stream.IntStream.rangeClosed;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.constraint.type.ForAlls.for_all;

public interface DefineSupply {

    @Returns_this
    default DefineSupply withEmptySupplies(int supplyCount) {
        final List<List<Object>> supplies = list();
        rangeClosed(1, supplyCount).forEach(i -> supplies.add(list()));
        return withSupplies(supplies);
    }

    DefineSupply withSupplies(List<Object>... supplies);

    DefineSupply withSupplies(List<List<Object>> supplies);

    ProblemGenerator withConstraint(Constraint constraint);

    default ProblemGenerator withConstraints(List<Function<Query, Query>> builders) {
        final var root = for_all();
        builders.forEach(b -> b.apply(QueryI.query(root)));
        return withConstraint(root);
    }

    default ProblemGenerator withConstraint(Function<Query, Query> builder) {
        return withConstraint(builder.apply(QueryI.query(for_all())).constraint());
    }

}
