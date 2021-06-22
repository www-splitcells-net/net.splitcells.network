package net.splitcells.gel.rating.rater;

import net.splitcells.dem.data.set.map.Map;
import net.splitcells.gel.data.table.attribute.Attribute;

import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.gel.constraint.Constraint.LINE;
import static net.splitcells.gel.rating.rater.RaterBasedOnLineGroup.groupRater;
import static net.splitcells.gel.rating.type.Cost.cost;

public class AllSame {
    public static <T> Rater allSame(Attribute<T> attribute) {
        return groupRater((lines, addition, removal) -> {
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
        });
    }

    private AllSame() {
    }
}
