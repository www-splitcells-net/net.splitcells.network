/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.rating.rater.lib;

import net.splitcells.gel.data.view.Line;
import net.splitcells.gel.data.view.View;
import net.splitcells.gel.data.view.attribute.Attribute;
import net.splitcells.gel.rating.framework.Rating;
import net.splitcells.gel.rating.rater.framework.GroupRater;
import net.splitcells.gel.rating.rater.framework.Rater;

import java.util.Optional;

import static net.splitcells.dem.utils.MathUtils.distance;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.gel.constraint.Constraint.LINE;
import static net.splitcells.gel.rating.rater.lib.RaterBasedOnLineGroup.groupRater;
import static net.splitcells.gel.rating.type.Cost.cost;

public class RegulatedLength {
    private RegulatedLength() {
        throw notImplementedYet();
    }

    public static Rater regulatedLength(Attribute<Integer> targetLength, Attribute<Integer> lengthElement) {
        return regulatedLength(targetLength, lengthElement, "sum of " + lengthElement.name()
                + " values should be equal to the value of " + targetLength.name());
    }

    private static Rater regulatedLength(Attribute<Integer> targetLength, Attribute<Integer> lengthElement
            , String description) {
        return groupRater(new GroupRater() {

            @Override
            public Rating lineRating(View lines, Optional<Line> addition, Optional<Line> removal) {
                final int requiredLength = addition
                        .map(e -> e.value(LINE).value(targetLength))
                        .orElseGet(() -> removal.get().value(LINE).value(targetLength));
                final var currentLength = lines.unorderedLines()
                        .stream()
                        .filter(e -> removal.map(line -> e.index() != line.index()).orElse(true))
                        .map(line -> line.value(LINE).value(lengthElement))
                        .reduce(Integer::sum)
                        .orElse(0);
                final var totalCost = distance(requiredLength, currentLength);
                return addition.map(a -> cost(totalCost / (lines.unorderedLines().size())))
                        .orElseGet(() -> cost(totalCost / (lines.unorderedLines().size() - 1)));
            }

            @Override public String descriptivePathName() {
                return "sum-of-" + lengthElement.descriptivePathName() + "-equal-to-" + targetLength.descriptivePathName();
            }

            @Override
            public String toString() {
                return description;
            }
        });
    }
}
