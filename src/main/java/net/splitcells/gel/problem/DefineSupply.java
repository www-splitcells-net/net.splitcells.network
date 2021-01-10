package net.splitcells.gel.problem;


import net.splitcells.dem.lang.annotations.Returns_this;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.constraint.Constraint;

import static java.util.stream.IntStream.rangeClosed;
import static net.splitcells.dem.data.set.list.Lists.list;

public interface DefineSupply {

	@Returns_this
	default DefineSupply arTukšiemPiedāvājumiem(int numursNuTuķšīem) {
		final List<List<Object>> piedāvājumi = list();
		rangeClosed(1, numursNuTuķšīem).forEach(i -> piedāvājumi.add(list()));
		return withSupplies(piedāvājumi);
	}

	DefineSupply withSupplies(List<Object>... peidāvājumi);

	DefineSupply withSupplies(List<List<Object>> peidāvājumi);

	ProblemGenerator withConstraint(Constraint ierobežojums);

}
