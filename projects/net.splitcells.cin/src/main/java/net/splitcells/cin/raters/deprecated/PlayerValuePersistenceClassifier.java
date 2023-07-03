/*
 * Copyright (c) 2021 Contributors To The `net.splitcells.*` Projects
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License v2.0 or later
 * which is available at https://www.gnu.org/licenses/old-licenses/gpl-2.0-standalone.html
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.cin.raters.deprecated;

import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.rating.rater.framework.Rater;

import java.util.function.BiPredicate;
import java.util.stream.Stream;

import static net.splitcells.dem.data.order.Comparators.ASCENDING_INTEGERS;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;
import static net.splitcells.gel.constraint.Constraint.INCOMING_CONSTRAINT_GROUP;
import static net.splitcells.gel.constraint.Constraint.LINE;
import static net.splitcells.gel.rating.framework.LocalRatingI.localRating;
import static net.splitcells.gel.rating.rater.lib.RaterBasedOnLineGroup.groupRouter;
import static net.splitcells.gel.rating.rater.framework.RatingEventI.ratingEvent;
import static net.splitcells.gel.rating.type.Cost.cost;
import static net.splitcells.gel.rating.type.Cost.noCost;

public class PlayerValuePersistenceClassifier {

    /**
     * Checks if a time step of a position adheres to a certain rules.
     *
     * @param playerValue              Currently, this is not used.
     * @param playerAttribute          Currently, this is not used.
     * @param timeAttribute            This {@link Attribute} contains the time of an allocation.
     * @param xCoordinate              This {@link Attribute} contains the x coordinate of an allocation.
     * @param yCoordinate              This {@link Attribute} contains the y coordinate of an allocation.
     * @param centerPositionClassifier Checks whether the center positions of the start and end {@link Line}s
     *                                 adhere to a rule.
     * @param name                     This is a descriptive name for the {@link Rater}.
     * @return
     */
    public static Rater playerValuePersistenceClassifier(int playerValue
            , Attribute<Integer> playerAttribute
            , Attribute<Integer> timeAttribute
            , Attribute<Integer> xCoordinate
            , Attribute<Integer> yCoordinate
            , BiPredicate<Stream<Line>, Stream<Line>> centerPositionClassifier
            , String name) {
        return groupRouter((lines, children) -> {
            final var ratingEvent = ratingEvent();
            final var startTime = lines.columnView(LINE)
                    .stream()
                    .map(l -> l.value(timeAttribute))
                    .distinct()
                    .sorted(ASCENDING_INTEGERS)
                    .findFirst().orElseThrow();
            final var incomingConstraintGroup = lines.unorderedLinesStream()
                    .findFirst()
                    .orElseThrow()
                    .value(INCOMING_CONSTRAINT_GROUP);
            final var centerXPosition = incomingConstraintGroup.metaData().value(PositionClustersCenterX.class);
            final var centerYPosition = incomingConstraintGroup.metaData().value(PositionClustersCenterY.class);
            final var centerStartPositions = lines.columnView(LINE)
                    .stream()
                    .filter(l -> l.value(timeAttribute).equals(startTime))
                    .filter(l -> l.value(xCoordinate).equals(centerXPosition))
                    .filter(l -> l.value(yCoordinate).equals(centerYPosition));
            final var centerEndPositions = lines.columnView(LINE)
                    .stream()
                    .filter(l -> l.value(timeAttribute).equals(startTime + 1))
                    .filter(l -> l.value(xCoordinate).equals(centerXPosition))
                    .filter(l -> l.value(yCoordinate).equals(centerYPosition));
            if (centerPositionClassifier.test(centerStartPositions, centerEndPositions)) {
                lines.unorderedLinesStream()
                        .forEach(line -> ratingEvent.updateRating_withReplacement(line
                                , localRating()
                                        .withPropagationTo(children)
                                        .withRating(noCost())
                                        .withResultingGroupId(incomingConstraintGroup)));
            } else {
                lines.unorderedLinesStream()
                        .filter(l -> !l.value(LINE).value(xCoordinate).equals(centerXPosition)
                                || !l.value(LINE).value(yCoordinate).equals(centerYPosition))
                        .forEach(line -> ratingEvent.updateRating_withReplacement(line
                                , localRating()
                                        .withPropagationTo(list())
                                        .withRating(noCost())
                                        .withResultingGroupId(incomingConstraintGroup)));
                lines.unorderedLinesStream()
                        .filter(l -> l.value(LINE).value(xCoordinate).equals(centerXPosition))
                        .filter(l -> l.value(LINE).value(yCoordinate).equals(centerYPosition))
                        .forEach(line -> ratingEvent.updateRating_withReplacement(line
                                , localRating()
                                        .withPropagationTo(list())
                                        .withRating(cost(1))
                                        .withResultingGroupId(incomingConstraintGroup)));
            }
            return ratingEvent;
        }, name);
    }

    private PlayerValuePersistenceClassifier() {
        throw constructorIllegal();
    }
}
