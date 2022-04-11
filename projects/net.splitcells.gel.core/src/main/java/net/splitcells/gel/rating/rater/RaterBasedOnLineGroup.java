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
package net.splitcells.gel.rating.rater;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.Table;
import org.w3c.dom.Node;

import java.util.Collection;
import java.util.Optional;
import net.splitcells.gel.rating.framework.Rating;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.gel.rating.rater.RatingEventI.ratingEvent;
import static net.splitcells.gel.rating.framework.LocalRatingI.localRating;

/**
 * This {@link Rater} makes it easy to rate groups with interdependent {@link Line}s.
 * Every {@link Line} has the same {@link Rating} in the group.
 * Keep in mind, that during a change in the group, every {@link Line}s' {@link Rating} in the group is updated.
 */
public class RaterBasedOnLineGroup implements Rater {

    public static RaterBasedOnLineGroup groupRater(GroupRater rater) {
        return raterBasedOnLineGroup(new RaterBasedOnLineGroupLambda() {

            @Override
            public RatingEvent rating(Table lines, Optional<Line> addition, Optional<Line> removal, java.util.List<Constraint> children) {
                final var lineRating = rater.lineRating(lines, addition, removal);
                final var ratingEvent = ratingEvent();
                lines.getLines().stream()
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
            public String toString() {
                return rater.toString();
            }
        });
    }

    public static RaterBasedOnLineGroup raterBasedOnLineGroup(RaterBasedOnLineGroupLambda rater) {
        return new RaterBasedOnLineGroup(rater);
    }

    private final RaterBasedOnLineGroupLambda rater;

    private RaterBasedOnLineGroup(RaterBasedOnLineGroupLambda rater) {
        this.rater = rater;
    }

    @Override
    public Collection<List<String>> paths() {
        throw notImplementedYet();
    }

    @Override
    public void addContext(Discoverable context) {
        throw notImplementedYet();
    }

    @Override
    public List<Domable> arguments() {
        // TODO
        return list(new Domable() {
            @Override
            public Node toDom() {
                return Xml.textNode("Arguments not implemented.");
            }
        });
    }

    @Override
    public RatingEvent ratingAfterAddition(Table lines, Line addition, List<Constraint> children, Table ratingsBeforeAddition) {
        return rater.rating(lines, Optional.of(addition), Optional.empty(), children);
    }

    @Override
    public RatingEvent rating_before_removal(Table lines, Line removal, List<Constraint> children, Table ratingsBeforeRemoval) {
        return rater.rating(lines, Optional.empty(), Optional.of(removal), children);
    }

    @Override
    public String toSimpleDescription(Line line, Table groupsLineProcessing, GroupId incomingGroup) {
        return rater.toString();
    }
}
