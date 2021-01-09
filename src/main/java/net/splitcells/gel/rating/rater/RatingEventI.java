package net.splitcells.gel.rating.rater;

import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.map.Maps.map;

import java.util.Set;

import net.splitcells.dem.data.set.map.Map;
import net.splitcells.gel.data.tabula.Rinda;
import net.splitcells.gel.rating.structure.LocalRating;

public class RatingEventI implements RatingEvent {

	private final Map<Rinda, LocalRating> papildinājumi = map();
	private final Set<Rinda> noņemšanas = setOfUniques();

	public static RatingEvent novērtejumuNotikums() {
		return new RatingEventI();
	}

	private RatingEventI() {

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
