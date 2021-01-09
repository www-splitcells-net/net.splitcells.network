package net.splitcells.gel.problem;


import net.splitcells.dem.lang.annotations.Returns_this;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.constraint.Ierobežojums;

import static java.util.stream.IntStream.rangeClosed;
import static net.splitcells.dem.data.set.list.Lists.list;

public interface DefineSupply {

	@Returns_this
	default DefineSupply arTukšiemPiedāvājumiem(int numursNuTuķšīem) {
		final List<List<Object>> piedāvājumi = list();
		rangeClosed(1, numursNuTuķšīem).forEach(i -> piedāvājumi.add(list()));
		return arePiedāvājumiem(piedāvājumi);
	}

	DefineSupply arePiedāvājumiem(List<Object>... peidāvājumi);

	DefineSupply arePiedāvājumiem(List<List<Object>> peidāvājumi);

	ProblemGenerator arIerobežojumu(Ierobežojums ierobežojums);

}
