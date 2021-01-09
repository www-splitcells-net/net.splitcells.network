package net.splitcells.gel.rating.vērtētājs;

import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.map.Maps.map;

import java.util.Set;

import net.splitcells.dem.data.set.map.Map;
import net.splitcells.gel.data.tabula.Rinda;
import net.splitcells.gel.rating.structure.LocalRating;

public class NovērtējumsNotikumsI implements NovērtējumsNotikums {

	private final Map<Rinda, LocalRating> papildinājumi = map();
	private final Set<Rinda> noņemšanas = setOfUniques();

	public static NovērtējumsNotikums novērtejumuNotikums() {
		return new NovērtējumsNotikumsI();
	}

	private NovērtējumsNotikumsI() {

	}

	@Override
	public Map<Rinda, LocalRating> papildinājumi() {
		return papildinājumi;
	}

	@Override
	public Set<Rinda> noņemšana() {
		return noņemšanas;
	}

}
