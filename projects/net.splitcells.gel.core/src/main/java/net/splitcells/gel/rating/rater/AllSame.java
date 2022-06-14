/*
 * Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License,
 * which is available at https://spdx.org/licenses/MIT.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 */
package net.splitcells.gel.rating.rater;

import net.splitcells.dem.data.order.Comparator;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.utils.MathUtils;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.rating.framework.Rating;
import net.splitcells.gel.rating.type.Cost;

import java.util.Optional;
import java.util.stream.Collectors;

import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.gel.constraint.Constraint.LINE;
import static net.splitcells.gel.rating.rater.RaterBasedOnLineGroup.groupRater;
import static net.splitcells.gel.rating.type.Cost.cost;
import static net.splitcells.gel.rating.type.Cost.noCost;

/**
 * Checks whether the {@linke Line}s of a group have the same value for a given {@link Attribute}.
 */
public class AllSame {

    public static <T> Rater allSame(Attribute<T> attribute) {
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
                if (1 == valueCounter.size()) {
                    return noCost();
                }
                final int futureLineSize;
                if (removal.isPresent()) {
                    futureLineSize = lines.size() - 1;
                } else {
                    futureLineSize = lines.size();
                }
                if (0 == futureLineSize) {
                    return noCost();
                }
                final var valueCounts = valueCounter.values()
                        .stream()
                        .sorted(Comparator.ASCENDING_INTEGERS)
                        .collect(toList());
                valueCounts.remove(valueCounts.size() - 1);
                return cost((double) valueCounts.stream()
                        .reduce(Integer::sum)
                        .get()
                        / (double) futureLineSize);
            }

            @Override
            public String toString() {
                return "values of " + attribute.name() + " should have the same value";
            }
        }, (line, groupLineProcessing, incomingGroup) -> {
            final var distinctValues = groupLineProcessing.getLines().stream()
                    .map(l -> l.value(LINE).value(attribute).toString())
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("undefined");
            return "values of " + attribute.name() + " should have the same value, but has the values '" + distinctValues + "'.";
        });
    }

    private AllSame() {
    }
}
