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
package net.splitcells.cin.raters;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.rating.framework.Rating;
import net.splitcells.gel.rating.rater.Rater;
import net.splitcells.gel.rating.rater.RatingEvent;

import java.util.Optional;

import static net.splitcells.cin.raters.PositionClusters.centerXPositionOf;
import static net.splitcells.cin.raters.PositionClusters.centerYPositionOf;
import static net.splitcells.dem.data.order.Comparator.ASCENDING_INTEGERS;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.gel.constraint.Constraint.INCOMING_CONSTRAINT_GROUP;
import static net.splitcells.gel.constraint.Constraint.LINE;
import static net.splitcells.gel.rating.framework.LocalRatingI.localRating;
import static net.splitcells.gel.rating.rater.RatingEventI.ratingEvent;
import static net.splitcells.gel.rating.type.Cost.cost;
import static net.splitcells.gel.rating.type.Cost.noCost;

/**
 * <p>Given a group based on {@link TimeSteps} and {@link PositionClusters},
 * filters {@link Line} where the player {@link #playerValue} is alive at the starting time.
 * Other players are ignored by this {@link Rater} and are not propagated.</p>
 * <p>The player of a group is identified by the {@link #playerAttribute} at the center position at the starting time.
 * If {@link #playerAttribute} is equal to {@link #playerValue},
 * than this {@link Rater} applies to the group.
 * This way it is possible, to create different rules to different players.</p>
 */
public class Alive implements Rater {
    public static Rater alive(int playerValue
            , Attribute<Integer> playerAttribute
            , Attribute<Integer> timeAttribute
            , Attribute<Integer> xCoordinate
            , Attribute<Integer> yCoordinate) {
        return new Alive(playerValue, playerAttribute, timeAttribute, xCoordinate, yCoordinate);
    }

    private final Attribute<Integer> playerAttribute;
    private final Attribute<Integer> timeAttribute;
    private final Attribute<Integer> xCoordinate;
    private final Attribute<Integer> yCoordinate;
    private int playerValue;

    private Alive(int playerValue
            , Attribute<Integer> playerAttribute
            , Attribute<Integer> timeAttribute
            , Attribute<Integer> xCoordinate
            , Attribute<Integer> yCoordinate) {
        this.playerAttribute = playerAttribute;
        this.playerValue = playerValue;
        this.timeAttribute = timeAttribute;
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
    }

    @Override
    public List<Domable> arguments() {
        return list((Domable) playerAttribute);
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
        final var centerXPosition = centerXPositionOf(incomingConstraintGroup);
        final var centerYPosition = centerYPositionOf(incomingConstraintGroup);
        final var centerStartPosition = lineValues
                .stream()
                .filter(l -> l.value(timeAttribute) == startTime)
                .filter(l -> l.value(xCoordinate) == centerXPosition)
                .filter(l -> l.value(yCoordinate) == centerYPosition)
                .findFirst();
        final var additionLine = addition.value(LINE);
        final boolean isAdditionCenterStart = startTime.equals(additionLine.value(timeAttribute))
                && centerXPosition == additionLine.value(xCoordinate)
                && centerYPosition == additionLine.value(yCoordinate);
        if (centerStartPosition.isEmpty()) {
            ratingEvent.additions().put(addition
                    , localRating()
                            .withPropagationTo(list())
                            .withRating(cost(0))
                            .withResultingGroupId(incomingConstraintGroup));
            return ratingEvent;
        }
        final var startPlayer = centerStartPosition.get().value(playerAttribute);
        if (startPlayer == playerValue) {
            ratingEvent.additions().put(addition
                    , localRating()
                            .withPropagationTo(children)
                            .withRating(noCost())
                            .withResultingGroupId(incomingConstraintGroup));
        } else {
            final Rating defianceCost;
            if (isAdditionCenterStart) {
                defianceCost = cost(1);
            } else {
                defianceCost = noCost();
            }
            ratingEvent.additions().put(addition
                    , localRating()
                            .withPropagationTo(list())
                            .withRating(defianceCost)
                            .withResultingGroupId(incomingConstraintGroup));
        }
        return ratingEvent;
    }
}
