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
package net.splitcells.cin.raters;

import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.rating.rater.Rater;

import java.util.function.Predicate;

import static net.splitcells.dem.data.order.Comparator.ASCENDING_INTEGERS;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;
import static net.splitcells.gel.constraint.Constraint.INCOMING_CONSTRAINT_GROUP;
import static net.splitcells.gel.constraint.Constraint.LINE;
import static net.splitcells.gel.rating.framework.LocalRatingI.localRating;
import static net.splitcells.gel.rating.rater.RaterBasedOnLineGroup.groupRouter;
import static net.splitcells.gel.rating.rater.RatingEventI.ratingEvent;
import static net.splitcells.gel.rating.type.Cost.cost;
import static net.splitcells.gel.rating.type.Cost.noCost;

public class CrowdDetector {

    public static Rater crowdDetector(int playerValue
            , Attribute<Integer> playerAttribute
            , Attribute<Integer> timeAttribute
            , Attribute<Integer> xCoordinate
            , Attribute<Integer> yCoordinate
            , Predicate<Long> crowdClassifier
            , String name) {
        return groupRouter((lines, children) -> {
            final var ratingEvent = ratingEvent();
            final var lineValues = lines.columnView(LINE).values();
            final var timeValues = lineValues
                    .stream()
                    .map(l -> l.value(timeAttribute))
                    .distinct()
                    .sorted(ASCENDING_INTEGERS)
                    .collect(toList());
            final var startTime = timeValues.get(0);
            final var incomingConstraintGroup = lines.lines().get(0).value(INCOMING_CONSTRAINT_GROUP);
            final var centerXPosition = incomingConstraintGroup.metaData().value(PositionClustersCenterX.class);
            final var centerYPosition = incomingConstraintGroup.metaData().value(PositionClustersCenterY.class);
            final var centerStartPosition = lineValues.stream()
                    .filter(l -> l.value(timeAttribute).equals(startTime))
                    .filter(l -> l.value(xCoordinate).equals(centerXPosition))
                    .filter(l -> l.value(yCoordinate).equals(centerYPosition))
                    .findFirst();
            if (centerStartPosition.isEmpty()) {
                lines.linesStream().forEach(line -> ratingEvent.updateRating_withReplacement(line
                        , localRating()
                                .withPropagationTo(list())
                                .withRating(noCost())
                                .withResultingGroupId(incomingConstraintGroup)));
                return ratingEvent;
            }
            final var centerEndPosition = lineValues
                    .stream()
                    .filter(l -> l.value(timeAttribute).equals(startTime + 1))
                    .filter(l -> l.value(xCoordinate).equals(centerXPosition))
                    .filter(l -> l.value(yCoordinate).equals(centerYPosition))
                    .findFirst();
            if (centerEndPosition.isEmpty()) {
                lines.linesStream().forEach(line -> ratingEvent.updateRating_withReplacement(line
                        , localRating()
                                .withPropagationTo(list())
                                .withRating(noCost())
                                .withResultingGroupId(incomingConstraintGroup)));
                return ratingEvent;
            }
            final var startPlayer = centerStartPosition.get().value(playerAttribute);
            if (startPlayer != playerValue) {
                lines.linesStream()
                        .forEach(line -> ratingEvent.updateRating_withReplacement(line
                                , localRating()
                                        .withPropagationTo(list())
                                        .withRating(noCost())
                                        .withResultingGroupId(incomingConstraintGroup)));
                return ratingEvent;
            }
            final var playerCount = lineValues.stream()
                    .filter(l -> startTime.equals(l.value(timeAttribute)))
                    .map(l -> l.value(playerAttribute))
                    .filter(line -> line.equals(startPlayer))
                    .count();
            if (crowdClassifier.test(playerCount)) {
                lines.linesStream()
                        .forEach(line -> ratingEvent.updateRating_withReplacement(line
                                , localRating()
                                        .withPropagationTo(children)
                                        .withRating(noCost())
                                        .withResultingGroupId(incomingConstraintGroup)));
            } else {
                lines.linesStream()
                        .forEach(line -> ratingEvent.updateRating_withReplacement(line
                                , localRating()
                                        .withPropagationTo(list())
                                        .withRating(cost(1))
                                        .withResultingGroupId(incomingConstraintGroup)));
            }
            return ratingEvent;
        }, name);
    }

    private CrowdDetector() {
        throw constructorIllegal();
    }
}
