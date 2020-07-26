package net.splitcells.gel.kodols.novērtējums.vērtētājs;

import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.map.Maps.map;

import java.util.Set;

import net.splitcells.dem.data.set.map.Map;
import net.splitcells.gel.kodols.dati.tabula.Rinda;
import net.splitcells.gel.kodols.novērtējums.struktūra.VietējieNovērtējums;

public class NovērtējumsNotikumsI implements NovērtējumsNotikums {

	private final Map<Rinda, VietējieNovērtējums> papildinājumi = map();
	private final Set<Rinda> noņemšanas = setOfUniques();

	public static NovērtējumsNotikums novērtejumuNotikums() {
		return new NovērtējumsNotikumsI();
	}

	private NovērtējumsNotikumsI() {

	}

	@Override
	public Map<Rinda, VietējieNovērtējums> papildinājumi() {
		return papildinājumi;
	}

	@Override
	public Set<Rinda> noņemšana() {
		return noņemšanas;
	}

}
