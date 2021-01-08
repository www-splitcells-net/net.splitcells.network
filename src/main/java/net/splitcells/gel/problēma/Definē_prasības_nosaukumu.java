package net.splitcells.gel.problēma;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.dati.tabula.atribūts.Atribūts;

public interface Definē_prasības_nosaukumu {
	DefinēPrasība arPrasībasNosaukumiem(Atribūts<?>... parsībasNosaukumi);

	DefinēPrasība arPrasībasNosaukumiem(List<Atribūts<?>> parsībasNosaukumi);

}
