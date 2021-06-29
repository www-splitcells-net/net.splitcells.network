package net.splitcells.gel.rating.rater;

import net.splitcells.dem.data.set.map.Map;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.rating.framework.Rating;

import java.util.Optional;

import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.gel.constraint.Constraint.LINE;
import static net.splitcells.gel.rating.rater.RaterBasedOnLineGroup.groupRater;
import static net.splitcells.gel.rating.type.Cost.cost;

public class AllSame {
    public static <T> Rater allSame(Attribute<T> attribute) {
        return allSame(attribute, "values of " + attribute.name() + " should have the same value");
    }

    private static <T> Rater allSame(Attribute<T> attribute, String description) {
        return groupRater(new GroupRater() {

            @Override
            public Rating lineRating(Table lines, Optional<Line> addition, Optional<Line> removal) {
                final Map<T, Integer> valueCounter = map();
                lines.getLines()
                        .stream()
                        .filter(e -> removal.map(line -> e.index() != line.index()).orElse(true))
                        .map(line -> line.value(LINE).value(attribute))
                        .forEach(value -> {
                            valueCounter.computeIfPresent(value, (k, v) -> valueCounter.put(k, v + 1));
                            valueCounter.computeIfAbsent(value, v -> valueCounter.put(v, 1));
                        });
                return addition.map(a -> cost((valueCounter.size() - 1) / (lines.size())))
                        .orElseGet(() -> cost((valueCounter.size() - 1) / (lines.size() - 1)));
            }

            @Override
            public String toString() {
                return description;
            }
        });
    }

    private AllSame() {
    }
}
