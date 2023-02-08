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
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.rating.rater.Rater;
import net.splitcells.gel.rating.rater.RatingEvent;

import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;

/**
 * Determines if the player {@link #playerValue},
 * has enough neighbouring positions with the same {@link #playerValue} given a {@link TimeSteps}.
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
        return null;
    }
}