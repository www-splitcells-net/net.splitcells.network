package net.splitcells.gel.rating.rater;

import net.splitcells.gel.data.table.attribute.Attribute;

import static net.splitcells.dem.utils.MathUtils.distance;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.gel.rating.rater.RaterBasedOnLineGroup.groupRater;
import static net.splitcells.gel.rating.type.Cost.cost;

public class RegulatedLength {
    private RegulatedLength() {
        throw notImplementedYet();
    }

    public static Rater regulatedLength(Attribute<Integer> targetLength, Attribute<Integer> lengthElement) {
        return groupRater((lines, addition, removal) -> {
            final int requiredLength = addition
                    .map(e -> e.value(targetLength))
                    .orElseGet(() -> removal.get().value(targetLength));
            final var currentLength = lines.getLines()
                    .stream()
                    .filter(e -> removal.map(line -> e.index() != line.index()).orElse(true))
                    .map(line -> line.value(lengthElement))
                    .reduce(Integer::sum)
                    .map(sum -> sum + addition.map(a -> a.value(lengthElement)).orElse(0))
                    .orElse(0);
            final var totalCost = distance(requiredLength, currentLength);
            return addition.map(a -> cost(totalCost / (lines.getLines().size())))
                    .orElseGet(() -> cost(totalCost / (lines.getLines().size() - 1)));
        });
    }
}
