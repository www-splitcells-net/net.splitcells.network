/*
 * Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License,
 * which is available at https://spdx.org/licenses/MIT.html.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License, version 2
 * or any later versions with the GNU Classpath Exception which is
 * available at https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT OR GPL-2.0-or-later WITH Classpath-exception-2.0
 */
package net.splitcells.gel.rating.rater;

import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.rating.framework.Rating;

import java.util.Optional;

import static net.splitcells.dem.utils.MathUtils.distance;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.gel.constraint.Constraint.LINE;
import static net.splitcells.gel.rating.rater.RaterBasedOnLineGroup.groupRater;
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
            public Rating lineRating(Table lines, Optional<Line> addition, Optional<Line> removal) {
                final int requiredLength = addition
                        .map(e -> e.value(LINE).value(targetLength))
                        .orElseGet(() -> removal.get().value(LINE).value(targetLength));
                final var currentLength = lines.getLines()
                        .stream()
                        .filter(e -> removal.map(line -> e.index() != line.index()).orElse(true))
                        .map(line -> line.value(LINE).value(lengthElement))
                        .reduce(Integer::sum)
                        .orElse(0);
                final var totalCost = distance(requiredLength, currentLength);
                return addition.map(a -> cost(totalCost / (lines.getLines().size())))
                        .orElseGet(() -> cost(totalCost / (lines.getLines().size() - 1)));
            }

            @Override
            public String toString() {
                return description;
            }
        });
    }
}
