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
package net.splitcells.gel.rating.rater.lib.classification;

import static net.splitcells.dem.data.set.Sets.toSetOfUniques;
import static net.splitcells.dem.lang.Xml.elementWithChildren;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.rating.rater.framework.RatingEventI.ratingEvent;
import static net.splitcells.gel.rating.type.Cost.noCost;
import static net.splitcells.gel.rating.framework.LocalRatingI.localRating;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.data.view.Line;
import net.splitcells.gel.data.view.View;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.gel.rating.rater.framework.Rater;
import net.splitcells.gel.rating.rater.framework.RatingEvent;

public class Propagation implements Rater {
    public static Propagation propagation() {
        return new Propagation();
    }

    private Propagation() {
    }

    private final List<Discoverable> contexts = list();

    @Override
    public RatingEvent ratingAfterAddition
            (View lines, Line addition, List<Constraint> children, View ratingsBeforeAddition) {
        final RatingEvent ratingEvent = ratingEvent();
        ratingEvent.additions().put
                (addition
                        , localRating()
                                .withPropagationTo(children)
                                .withRating(noCost())
                                .withResultingGroupId(addition.value(Constraint.INCOMING_CONSTRAINT_GROUP)));
        return ratingEvent;
    }

    @Override
    public RatingEvent rating_before_removal
            (View lines, Line removal, List<Constraint> children, View ratingsBeforeRemoval) {
        return ratingEvent();
    }

    @Override
    public String toSimpleDescription(Line line, View groupsLineProcessing, GroupId incomingGroup) {
        return "";
    }

    @Override
    public List<Domable> arguments() {
        return list();
    }

    @Override
    public void addContext(Discoverable contexts) {
        this.contexts.add(contexts);
    }

    @Override
    public Set<List<String>> paths() {
        return contexts.stream().map(Discoverable::path).collect(toSetOfUniques());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    @Override
    public Class<? extends Rater> type() {
        return Propagation.class;
    }
}
