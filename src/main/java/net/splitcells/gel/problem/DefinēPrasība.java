package net.splitcells.gel.problem;

import net.splitcells.dem.lang.annotations.Returns_this;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.data.tabula.atribūts.Atribūts;

import static java.util.stream.IntStream.rangeClosed;
import static net.splitcells.dem.data.set.list.Lists.list;

public interface DefinēPrasība {

	@Returns_this
	default DefinēPrasība arTuķšamPrasībam(int numursNuTuķšīem) {
		final List<List<Object>> parsības = list();
		rangeClosed(1, numursNuTuķšīem).forEach(i -> parsības.add(list()));
		return arPrasībam(parsības);
	}

	@Returns_this
	DefinēPrasība arPrasībam(List<Object> prasība, @SuppressWarnings("unchecked") List<Object>... parsības);

	@Returns_this
	DefinēPrasība arPrasībam(List<List<Object>> parsības);

	DefinēPiedāvājumi arPiedāvumuNosaukumiem(Atribūts<?>... supply_header);

	DefinēPiedāvājumi arPiedāvumuNosaukumiem(List<Atribūts<?>> supply_header);
}
