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

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.data.view.Line;
import net.splitcells.gel.data.view.View;

import java.util.Optional;

import net.splitcells.gel.proposal.Proposal;
import net.splitcells.gel.rating.framework.Rating;
import net.splitcells.gel.rating.rater.framework.*;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.gel.rating.rater.framework.RatingEventI.ratingEvent;
import static net.splitcells.gel.rating.framework.LocalRatingI.localRating;

/**
 * This {@link Rater} makes it easy to rate groups with interdependent {@link Line}s.
 * Every {@link Line} has the same {@link Rating} in the group.
 * Keep in mind, that during a change in the group, every {@link Line}s' {@link Rating} in the group is updated.
 *
 * @deprecated This is too complicated. {@link LineGroupRater} replaces this.
 */
@Deprecated
public class RaterBasedOnLineGroup implements Rater {

    @Deprecated
    public static RaterBasedOnLineGroup groupRater(GroupRater rater) {
        return groupRater(rater, (a, b, c) -> rater.toString());
    }

    @Deprecated
    public static RaterBasedOnLineGroup groupRater(GroupRater rater, SimpleDescriptor simpleDescriptor) {
        return raterBasedOnLineGroup(new RaterBasedOnLineGroupLambda() {

            @Override
            public RatingEvent rating(View lines, Optional<Line> addition, Optional<Line> removal, List<Constraint> children) {
                final var lineRating = rater.lineRating(lines, addition, removal);
                final var ratingEvent = ratingEvent();
                lines.unorderedLines().stream()
                        .filter(e -> addition.map(line -> e.index() != line.index()).orElse(true))
                        .filter(e -> removal.map(line -> e.index() != line.index()).orElse(true))
                        .forEach(e -> ratingEvent.updateRating_withReplacement(e
                                , localRating()
                                        .withPropagationTo(children)
                                        .withRating(lineRating)
                                        .withResultingGroupId
                                                (e.value(Constraint.INCOMING_CONSTRAINT_GROUP))));
                addition.ifPresent(line -> ratingEvent.additions()
                        .put(line
                                , localRating()
                                        .withPropagationTo(children)
                                        .withRating(lineRating)
                                        .withResultingGroupId
                                                (line.value(Constraint.INCOMING_CONSTRAINT_GROUP))));
                return ratingEvent;
            }

            @Override
            public Proposal propose(Proposal proposal) {
                return rater.propose(proposal);
            }

            @Override
            public String toString() {
                return rater.toString();
            }
        }, simpleDescriptor);
    }

    @Deprecated
    public static RaterBasedOnLineGroup groupRouter(GroupRouter rater) {
        return groupRouter(rater, (a, b, c) -> rater.toString(), RaterBasedOnLineGroup.class.getSimpleName());
    }

    @Deprecated
    public static RaterBasedOnLineGroup groupRouter(GroupRouter rater, String name) {
        return groupRouter(rater, (a, b, c) -> name, name);
    }

    @Deprecated
    public static RaterBasedOnLineGroup groupRouter(GroupRouter rater, SimpleDescriptor simpleDescriptor, String name) {
        return new RaterBasedOnLineGroup(new RaterBasedOnLineGroupLambda() {

            @Override
            public RatingEvent rating(View lines, Optional<Line> addition, Optional<Line> removal, List<Constraint> children) {
                final var rating = rater.routing(lines, children);
                if (addition.isPresent() && rating.removal().contains(addition.get())) {
                    rating.removal().remove(addition.get());
                }
                if (removal.isPresent()) {
                    if (rating.additions().keySet().contains(removal.get())) {
                        rating.additions().remove(removal.get());
                    }
                    if (rating.removal().contains(removal.get())) {
                        rating.removal().remove(removal.get());
                    }
                }
                return rating;
            }

            @Override
            public Proposal propose(Proposal proposal) {
                return rater.propose(proposal);
            }

            @Override
            public String toString() {
                return rater.toString();
            }
        }
                , simpleDescriptor
                , name);
    }

    @Deprecated
    public static RaterBasedOnLineGroup raterBasedOnLineGroup(RaterBasedOnLineGroupLambda rater, SimpleDescriptor simpleDescriptor) {
        return new RaterBasedOnLineGroup(rater, simpleDescriptor, rater.getClass().getSimpleName());
    }

    @Deprecated
    public static RaterBasedOnLineGroup raterBasedOnLineGroup(RaterBasedOnLineGroupLambda rater) {
        return new RaterBasedOnLineGroup(rater, (a, b, c) -> rater.toString(), rater.getClass().getSimpleName());
    }

    private final RaterBasedOnLineGroupLambda rater;
    private final SimpleDescriptor simpleDescriptor;

    private final String name;

    private RaterBasedOnLineGroup(RaterBasedOnLineGroupLambda rater, SimpleDescriptor simpleDescriptor, String name) {
        this.rater = rater;
        this.simpleDescriptor = simpleDescriptor;
        this.name = name;
    }

    @Override
    public Set<List<String>> paths() {
        throw notImplementedYet();
    }

    @Override
    public void addContext(Discoverable context) {
        throw notImplementedYet();
    }

    @Override
    public List<Domable> arguments() {
        // TODO
        return list(tree("Arguments not implemented."));
    }

    @Override
    public RatingEvent ratingAfterAddition(View lines, Line addition, List<Constraint> children, View ratingsBeforeAddition) {
        return rater.rating(lines, Optional.of(addition), Optional.empty(), children);
    }

    @Override
    public RatingEvent rating_before_removal(View lines, Line removal, List<Constraint> children, View ratingsBeforeRemoval) {
        return rater.rating(lines, Optional.empty(), Optional.of(removal), children);
    }

    @Override
    public String toSimpleDescription(Line line, View groupsLineProcessing, GroupId incomingGroup) {
        return simpleDescriptor.toSimpleDescription(line, groupsLineProcessing, incomingGroup);
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Proposal propose(Proposal proposal) {
        return rater.propose(proposal);
    }

    @Override
    public Tree toTree() {
        return simpleDescriptor.toPerspective();
    }
}
