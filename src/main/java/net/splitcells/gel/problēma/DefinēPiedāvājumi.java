package net.splitcells.gel.problēma;


import net.splitcells.dem.lang.annotations.Returns_this;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.ierobežojums.Ierobežojums;

import static java.util.stream.IntStream.rangeClosed;
import static net.splitcells.dem.data.set.list.Lists.list;

public interface DefinēPiedāvājumi {

	@Returns_this
	default DefinēPiedāvājumi arTukšiemPiedāvājumiem(int numursNuTuķšīem) {
		final List<List<Object>> piedāvājumi = list();
		rangeClosed(1, numursNuTuķšīem).forEach(i -> piedāvājumi.add(list()));
		return arePiedāvājumiem(piedāvājumi);
	}

	DefinēPiedāvājumi arePiedāvājumiem(List<Object>... peidāvājumi);

	DefinēPiedāvājumi arePiedāvājumiem(List<List<Object>> peidāvājumi);

	ProblēmaĢenerators arIerobežojumu(Ierobežojums ierobežojums);

}
