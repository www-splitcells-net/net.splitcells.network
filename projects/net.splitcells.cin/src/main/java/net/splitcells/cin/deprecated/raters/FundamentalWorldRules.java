/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.cin.deprecated.raters;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.data.view.Line;
import net.splitcells.gel.data.view.View;
import net.splitcells.gel.data.view.attribute.Attribute;
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
    public RatingEvent ratingAfterAddition(View lines, Line addition, List<Constraint> children, View lineProcessing) {
        logs().warnUnimplementedPart(getClass());
        final var ratingEvent = ratingEvent();
        final var localRating = localRating().withRating(noCost())
                .withPropagationTo(children)
                .withResultingGroupId(addition.value(INCOMING_CONSTRAINT_GROUP));
        ratingEvent.addRating_viaAddition(addition, localRating);
        return ratingEvent;
    }

    @Override public RatingEvent rating_before_removal(View lines, Line removal, List<Constraint> children, View lineProcessingBeforeRemoval) {
        return ratingEvent();
    }

    @Override
    public String toSimpleDescription(Line line, View groupsLineProcessing, GroupId incomingGroup) {
        throw notImplementedYet();
    }

    @Override public String descriptivePathName() {
        return "fundamental-world-rules";
    }
}
