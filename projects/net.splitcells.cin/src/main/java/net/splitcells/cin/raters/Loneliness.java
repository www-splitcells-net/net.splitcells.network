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

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.rating.framework.Rating;
import net.splitcells.gel.rating.rater.Rater;
import net.splitcells.gel.rating.rater.RatingEvent;

import static net.splitcells.cin.raters.PositionClusters.centerXPositionOf;
import static net.splitcells.cin.raters.PositionClusters.centerYPositionOf;
import static net.splitcells.dem.data.order.Comparator.ASCENDING_INTEGERS;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.gel.constraint.Constraint.INCOMING_CONSTRAINT_GROUP;
import static net.splitcells.gel.constraint.Constraint.LINE;
import static net.splitcells.gel.rating.framework.LocalRatingI.localRating;
import static net.splitcells.gel.rating.rater.RatingEventI.ratingEvent;
import static net.splitcells.gel.rating.type.Cost.cost;
import static net.splitcells.gel.rating.type.Cost.noCost;

/**
 * Determines if the player {@link #playerValue},
 * has at least 2 neighbouring positions with the same {@link #playerValue} given a {@link TimeSteps} during the start.
 * {@link Loneliness} can only be calculated, if the start and end time of the center position is present.
 */
public class Loneliness implements Rater {
    public static Rater loneliness(int playerValue
            , Attribute<Integer> playerAttribute
            , Attribute<Integer> timeAttribute
            , Attribute<Integer> xCoordinate
            , Attribute<Integer> yCoordinate) {
        return new Loneliness(playerValue, playerAttribute, timeAttribute, xCoordinate, yCoordinate);
    }

    private final int playerValue;
    private final Attribute<Integer> playerAttribute;
    private final Attribute<Integer> timeAttribute;
    private final Attribute<Integer> xCoordinate;
    private final Attribute<Integer> yCoordinate;

    private Loneliness(int playerValue
            , Attribute<Integer> playerAttribute
            , Attribute<Integer> timeAttribute
            , Attribute<Integer> xCoordinate
            , Attribute<Integer> yCoordinate) {
        this.playerValue = playerValue;
        this.playerAttribute = playerAttribute;
        this.timeAttribute = timeAttribute;
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
    }

    @Override
    public List<Domable> arguments() {
        throw notImplementedYet();
    }

    @Override
    public RatingEvent ratingAfterAddition(Table lines, Line addition, List<Constraint> children, Table lineProcessing) {
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
        final var centerStartPosition = lineValues
                .stream()
                .filter(l -> l.value(timeAttribute).equals(startTime))
                .filter(l -> l.value(xCoordinate).equals(centerXPosition))
                .filter(l -> l.value(yCoordinate).equals(centerYPosition))
                .findFirst();
        final var additionLine = addition.value(LINE);
        final boolean isAdditionCenterStart = startTime.equals(additionLine.value(timeAttribute))
                && centerXPosition.equals(additionLine.value(xCoordinate))
                && centerYPosition.equals(additionLine.value(yCoordinate));
        final boolean isAdditionCenterEnd = additionLine.value(timeAttribute).equals(startTime + 1)
                && centerXPosition.equals(additionLine.value(xCoordinate))
                && centerYPosition.equals(additionLine.value(yCoordinate));
        if (centerStartPosition.isEmpty()) {
            lines.linesStream()
                    .filter(line -> !line.equalsTo(addition))
                    .forEach(line ->
                            ratingEvent.additions().put(line
                                    , localRating()
                                            .withPropagationTo(list())
                                            .withRating(noCost())
                                            .withResultingGroupId(incomingConstraintGroup)));
            return ratingEvent;
        }
        final var centerEndPosition = lineValues
                .stream()
                .filter(l -> l.value(timeAttribute).equals(startTime))
                .filter(l -> l.value(xCoordinate).equals(centerXPosition))
                .filter(l -> l.value(yCoordinate).equals(centerYPosition))
                .findFirst();
        if (centerEndPosition.isEmpty()) {
            lines.linesStream()
                    .filter(line -> !line.equalsTo(addition))
                    .forEach(line -> ratingEvent.updateRating_withReplacement(line
                            , localRating()
                                    .withPropagationTo(list())
                                    .withRating(noCost())
                                    .withResultingGroupId(incomingConstraintGroup)));
            return ratingEvent;
        }
        final var startPlayer = centerStartPosition.get().value(playerAttribute);
        if (startPlayer != playerValue) {
            ratingEvent.additions().put(addition
                    , localRating()
                            .withPropagationTo(list())
                            .withRating(noCost())
                            .withResultingGroupId(incomingConstraintGroup));
            lines.linesStream()
                    .filter(line -> !line.equalsTo(addition))
                    .forEach(line -> ratingEvent.updateRating_withReplacement(line
                            , localRating()
                                    .withPropagationTo(list())
                                    .withRating(noCost())
                                    .withResultingGroupId(incomingConstraintGroup)));
            return ratingEvent;
        }
        final var playerCount = lineValues.stream()
                .map(l -> l.value(playerAttribute))
                .filter(player -> player.equals(startPlayer))
                .count();
        if (playerCount < 2) {
            ratingEvent.additions().put(addition
                    , localRating()
                            .withPropagationTo(children)
                            .withRating(noCost())
                            .withResultingGroupId(incomingConstraintGroup));
            lines.linesStream()
                    .filter(line -> !line.equalsTo(addition))
                    .forEach(line -> ratingEvent.updateRating_withReplacement(line
                            , localRating()
                                    .withPropagationTo(children)
                                    .withRating(noCost())
                                    .withResultingGroupId(incomingConstraintGroup)));
        } else {
            ratingEvent.additions().put(addition
                    , localRating()
                            .withPropagationTo(list())
                            .withRating(cost(1))
                            .withResultingGroupId(incomingConstraintGroup));
            lines.linesStream()
                    .filter(line -> !line.equalsTo(addition))
                    .forEach(line -> ratingEvent.updateRating_withReplacement(line
                            , localRating()
                                    .withPropagationTo(list())
                                    .withRating(cost(1))
                                    .withResultingGroupId(incomingConstraintGroup)));
        }
        return ratingEvent;
    }
}
