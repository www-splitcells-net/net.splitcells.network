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
package net.splitcells.cin.deprecated.raters;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.rating.rater.framework.Rater;
import net.splitcells.gel.rating.rater.framework.RatingEvent;

import static net.splitcells.dem.resource.communication.log.Logs.logs;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.gel.constraint.Constraint.INCOMING_CONSTRAINT_GROUP;
import static net.splitcells.gel.rating.framework.LocalRatingI.localRating;
import static net.splitcells.gel.rating.rater.framework.RatingEventI.ratingEvent;
import static net.splitcells.gel.rating.type.Cost.noCost;

public class FundamentalWorldRules implements Rater {
    public static Rater fundamentalWorldRules(Attribute<Integer> worldTime
            , Attribute<Integer> positionX
            , Attribute<Integer> positionY
            , Attribute<Integer> value) {
        return new FundamentalWorldRules(worldTime, positionX, positionY, value);
    }

    private final Attribute<Integer> worldTime;
    private final Attribute<Integer> positionX;
    private final Attribute<Integer> positionY;
    private final Attribute<Integer> value;

    private FundamentalWorldRules(Attribute<Integer> worldTime
            , Attribute<Integer> positionX
            , Attribute<Integer> positionY
            , Attribute<Integer> value) {
        this.worldTime = worldTime;
        this.positionX = positionX;
        this.positionY = positionY;
        this.value = value;
    }

    @Override
    public List<Domable> arguments() {
        throw notImplementedYet();
    }

    @Override
    public RatingEvent ratingAfterAddition(Table lines, Line addition, List<Constraint> children, Table lineProcessing) {
        logs().appendUnimplementedWarning(getClass());
        final var ratingEvent = ratingEvent();
        final var localRating = localRating().withRating(noCost())
                .withPropagationTo(children)
                .withResultingGroupId(addition.value(INCOMING_CONSTRAINT_GROUP));
        ratingEvent.addRating_viaAddition(addition, localRating);
        return ratingEvent;
    }

    @Override
    public String toSimpleDescription(Line line, Table groupsLineProcessing, GroupId incomingGroup) {
        throw notImplementedYet();
    }
}
