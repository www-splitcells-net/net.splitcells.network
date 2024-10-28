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

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.data.view.Line;
import net.splitcells.gel.data.view.Table;
import net.splitcells.gel.rating.rater.framework.Rater;
import net.splitcells.gel.rating.rater.framework.RatingEvent;

import java.util.function.Predicate;

import static net.splitcells.dem.data.set.Sets.toSetOfUniques;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.gel.constraint.Constraint.INCOMING_CONSTRAINT_GROUP;
import static net.splitcells.gel.constraint.Constraint.LINE;
import static net.splitcells.gel.rating.framework.LocalRatingI.localRating;
import static net.splitcells.gel.rating.rater.framework.RatingEventI.ratingEvent;
import static net.splitcells.gel.rating.type.Cost.noCost;

public class ForAllWithCondition<T> implements Rater {

    public static <T> ForAllWithCondition<T> forAllWithCondition(Predicate<Line> condition) {
        return new ForAllWithCondition<>(condition);
    }

    private final Predicate<Line> condition;
    private final List<Discoverable> contexts = list();

    private ForAllWithCondition(Predicate<Line> condition) {
        this.condition = condition;
    }

    @Override
    public RatingEvent ratingAfterAddition
            (Table lines, Line addition, List<Constraint> children, Table ratingsBeforeAddition) {
        final List<Constraint> targetChildren;
        if (condition.test(addition.value(LINE))) {
            targetChildren = children;
        } else {
            targetChildren = list();
        }
        final var ratingEvent = ratingEvent();
        ratingEvent.additions().put
                (addition
                        , localRating()
                                .withPropagationTo(targetChildren)
                                .withRating(noCost())
                                .withResultingGroupId(addition.value(INCOMING_CONSTRAINT_GROUP)));
        return ratingEvent;
    }

    @Override
    public RatingEvent rating_before_removal(Table lines, Line removal, List<Constraint> children, Table ratingsBeforeRemoval) {
        return ratingEvent();
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
    public List<Domable> arguments() {
        return list(tree(condition.toString()));
    }

    @Override
    public String toSimpleDescription(Line line, Table groupsLineProcessing, GroupId incomingGroup) {
        return "For all with condition " + condition;
    }
}
