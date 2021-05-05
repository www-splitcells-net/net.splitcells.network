package net.splitcells.gel.rating.rater;

import net.splitcells.dem.data.set.Sets;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.rating.structure.Rating;

import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.data.set.map.Pair.pair;
import static net.splitcells.dem.utils.MathUtils.distance;
import static net.splitcells.gel.rating.rater.RaterBasedOnLineGroup.raterBasedOnLineGroup;
import static net.splitcells.gel.rating.rater.RatingEventI.ratingEvent;
import static net.splitcells.gel.rating.structure.LocalRatingI.localRating;
import static net.splitcells.gel.rating.type.Cost.cost;

public class AllSame {
    public static <T> Rater allSame(Attribute<T> attribute) {
        return raterBasedOnLineGroup((lines, addition, removal, children) -> {
            final var ratingEvent = ratingEvent();
            final T requiredHours = addition
                    .map(e -> e.value(attribute))
                    .orElseGet(() -> removal.get().value(attribute));
            final Map<T, Integer> valueCounter = map();
            lines.getLines()
                    .stream()
                    .filter(e -> removal.map(line -> e.index() != line.index()).orElse(true))
                    .map(line -> line.value(attribute))
                    .forEach(value -> {
                        valueCounter.computeIfPresent(value, (k, v) -> valueCounter.put(k, v + 1));
                        valueCounter.computeIfAbsent(value, v -> valueCounter.put(v, 1));
                    });
            final Rating lineCost;
            if (addition.isPresent()) {
                lineCost = cost((valueCounter.size() - 1) / (lines.size() + 1));
            } else {
                lineCost = cost((valueCounter.size() - 1) / (lines.size() - 1));
            }
            lines.getLines().stream()
                    .filter(e -> removal.map(line -> e.index() != line.index()).orElse(true))
                    .forEach(e -> ratingEvent.updateRating_withReplacement(e
                            , localRating()
                                    .withPropagationTo(children)
                                    .withRating(lineCost)
                                    .withResultingGroupId
                                            (e.value(Constraint.INCOMING_CONSTRAINT_GROUP))));
            addition.ifPresent(line -> ratingEvent.additions()
                    .put(line
                            , localRating()
                                    .withPropagationTo(children)
                                    .withRating(lineCost)
                                    .withResultingGroupId
                                            (line.value(Constraint.INCOMING_CONSTRAINT_GROUP))));
            return ratingEvent;
        });
    }

    private AllSame() {
    }
}
