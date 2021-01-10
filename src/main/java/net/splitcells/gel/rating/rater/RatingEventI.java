package net.splitcells.gel.rating.rater;

import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.map.Maps.map;

import java.util.Set;

import net.splitcells.dem.data.set.map.Map;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.rating.structure.LocalRating;

public class RatingEventI implements RatingEvent {

	private final Map<Line, LocalRating> papildinājumi = map();
	private final Set<Line> noņemšanas = setOfUniques();

	public static RatingEvent ratingEvent() {
		return new RatingEventI();
	}

	private RatingEventI() {

	}

	@Override
	public Map<Line, LocalRating> additions() {
		return papildinājumi;
	}

	@Override
	public Set<Line> removal() {
		return noņemšanas;
	}

}
