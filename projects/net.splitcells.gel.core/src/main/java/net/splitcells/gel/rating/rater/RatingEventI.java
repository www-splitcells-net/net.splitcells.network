package net.splitcells.gel.rating.rater;

import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.map.Maps.map;

import java.util.Set;

import net.splitcells.dem.data.set.map.Map;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.rating.framework.LocalRating;

public class RatingEventI implements RatingEvent {

	private final Map<Line, LocalRating> additions = map();
	private final Set<Line> removal = setOfUniques();

	public static RatingEvent ratingEvent() {
		return new RatingEventI();
	}

	private RatingEventI() {

	}

	@Override
	public Map<Line, LocalRating> additions() {
		return additions;
	}

	@Override
	public Set<Line> removal() {
		return removal;
	}

}
