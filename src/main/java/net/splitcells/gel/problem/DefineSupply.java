package net.splitcells.gel.problem;


import net.splitcells.dem.lang.annotations.Returns_this;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.constraint.Constraint;

import static java.util.stream.IntStream.rangeClosed;
import static net.splitcells.dem.data.set.list.Lists.list;

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

}
