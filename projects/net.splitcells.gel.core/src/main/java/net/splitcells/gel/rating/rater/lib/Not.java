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
package net.splitcells.gel.rating.rater.lib;

import lombok.val;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.data.view.Line;
import net.splitcells.gel.data.view.View;
import net.splitcells.gel.rating.framework.LocalRating;
import net.splitcells.gel.rating.rater.framework.Rater;
import net.splitcells.gel.rating.rater.framework.RatingEvent;
import net.splitcells.gel.rating.type.Cost;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.rating.framework.LocalRatingI.localRating;
import static net.splitcells.gel.rating.rater.framework.RatingEventI.ratingEvent;
import static net.splitcells.gel.rating.type.Cost.cost;
import static net.splitcells.gel.rating.type.Cost.noCost;

/**
 * Inverses a given {@link Rater}, so that only {@link Line}s with {@link Cost}s are propagated and
 * do not cause any {@link Cost}.
 * {@link Line}s with {@link Cost#noCost()} are not propagated, but cause constant {@link Cost}.
 */
public class Not implements Rater {
    public static Rater not(Rater argRater) {
        return new Not(argRater);
    }

    private final Rater base;

    private Not(Rater argRater) {
        base = argRater;
    }

    @Override public RatingEvent ratingAfterAddition(View linesOfGroup
            , Line addition
            , List<Constraint> children
            , View lineProcessingBeforeAddition) {
        return invertRating(base.ratingAfterAddition(linesOfGroup, addition, children, lineProcessingBeforeAddition));
    }

    @Override public RatingEvent rating_before_removal(View lines, Line removal, List<Constraint> children, View lineProcessingBeforeRemoval) {
        return invertRating(base.rating_before_removal(lines, removal, children, lineProcessingBeforeRemoval));
    }

    private static RatingEvent invertRating(RatingEvent arg) {
        val inversion = ratingEvent();
        inversion.removal().addAll(arg.removal());
        arg.additions()
                .forEach((line, localRating) -> inversion.additions().put(line, invert(localRating)));
        arg.complexAdditions().forEach((line, ratings) ->
                ratings.forEach(rating -> inversion.extendComplexRating(line, invert(rating))));
        return inversion;
    }

    private static LocalRating invert(LocalRating localRating) {
        if (localRating.rating().equalz(noCost())) {
            return localRating()
                    .withRating(cost(1))
                    .withPropagationTo(list())
                    .withResultingGroupId(localRating.resultingConstraintGroupId());
        }
        return localRating()
                .withRating(noCost())
                .withPropagationTo(localRating.propagateTo())
                .withResultingGroupId(localRating.resultingConstraintGroupId());
    }

    @Override public String toSimpleDescription(Line line, View groupsLineProcessing, GroupId incomingGroup) {
        return "not " + base.toSimpleDescription(line, groupsLineProcessing, incomingGroup);
    }

    @Override public List<Domable> arguments() {
        return list(base);
    }
}
