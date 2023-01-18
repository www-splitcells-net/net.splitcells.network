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

import static net.splitcells.dem.data.set.list.Lists.list;

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
    public Rater alive(Attribute<Integer> playerAttribute, int playerValue) {
        return new Alive(playerAttribute, playerValue);
    }

    private final Attribute<Integer> playerAttribute;
    private int playerValue;

    private Alive(Attribute<Integer> playerAttribute, int playerValue) {
        this.playerAttribute = playerAttribute;
        this.playerValue = playerValue;
    }

    @Override
    public List<Domable> arguments() {
        return list((Domable) playerAttribute);
    }

    @Override
    public RatingEvent ratingAfterAddition(Table lines, Line addition, List<Constraint> children, Table lineProcessing) {
        return null;
    }
}
