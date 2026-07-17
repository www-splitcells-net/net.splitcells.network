/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.rating.rater.framework;

import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.map.Maps.map;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.data.set.Set;
import net.splitcells.gel.data.view.Line;
import net.splitcells.gel.rating.framework.LocalRating;

public class RatingEventI implements RatingEvent {

    private final Map<Line, LocalRating> additions = map();

    private final Map<Line, List<LocalRating>> complexAdditions = map();
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
    public Map<Line, List<LocalRating>> complexAdditions() {
        return complexAdditions;
    }

    @Override
    public Set<Line> removal() {
        return removal;
    }

}
