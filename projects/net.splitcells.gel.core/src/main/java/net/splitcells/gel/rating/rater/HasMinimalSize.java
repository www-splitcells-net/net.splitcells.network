/*
 * Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License,
 * which is available at https://spdx.org/licenses/MIT.html.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License, version 2
 * or any later versions with the GNU Classpath Exception which is
 * available at https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT OR GPL-2.0-or-later WITH Classpath-exception-2.0
 */
package net.splitcells.gel.rating.rater;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.dem.utils.CommonFunctions;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.rating.framework.Rating;
import org.w3c.dom.Node;

import java.util.Collection;

import static java.lang.Math.abs;
import static java.util.stream.Collectors.toList;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.rating.rater.RatingEventI.ratingEvent;
import static net.splitcells.gel.rating.framework.LocalRatingI.localRating;
import static net.splitcells.gel.rating.type.Cost.cost;
import static net.splitcells.gel.rating.type.Cost.noCost;


public class HasMinimalSize implements Rater {
    public static HasMinimalSize hasMinimalSize(int minimalSize) {
        return new HasMinimalSize(minimalSize);
    }

    private final int minimalSize;
    private final List<Discoverable> contexts = list();

    protected HasMinimalSize(int minimalSize) {
        this.minimalSize = minimalSize;
    }

    @Override
    public RatingEvent ratingAfterAddition(Table lines, Line addition, List<Constraint> children
            , Table ratingsBeforeAddition) {
        final var individualRating = rating(lines, false);
        final var additionalRatings
                = rateLines(lines, addition, children, individualRating);
        additionalRatings.additions().put(addition
                , localRating()
                        .withPropagationTo(children)
                        .withRating(individualRating)
                        .withResultingGroupId(addition.value(Constraint.INCOMING_CONSTRAINT_GROUP))
        );
        return additionalRatings;
    }

    private RatingEvent rateLines(Table lines, Line changed, List<Constraint> children, Rating cost) {
        final RatingEvent linesRating = ratingEvent();
        lines.rawLinesView().stream()
                .filter(e -> e != null)
                .filter(e -> e.index() != changed.index())
                .forEach(e -> {
                    linesRating.updateRating_withReplacement(e,
                            localRating().
                                    withPropagationTo(children).
                                    withRating(cost).
                                    withResultingGroupId(changed.value(Constraint.INCOMING_CONSTRAINT_GROUP))
                    );
                });
        return linesRating;
    }

    @Override
    public Node argumentation(GroupId group, Table allocations) {
        final var argumentation = Xml.elementWithChildren(HasMinimalSize.class.getSimpleName());
        argumentation.appendChild(
                Xml.elementWithChildren("minimal-size"
                        , Xml.textNode(minimalSize + "")));
        argumentation.appendChild(
                Xml.elementWithChildren("actual-size"
                        , Xml.textNode(allocations.size() + "")));
        return argumentation;
    }

    @Override
    public String toSimpleDescription(Line line, Table groupsLineProcessing, GroupId incomingGroup) {
        return "size should be at least" + minimalSize + ", but is " + groupsLineProcessing.size();
    }

    @Override
    public RatingEvent rating_before_removal
            (Table lines
                    , Line removal
                    , List<Constraint> children
                    , Table ratingsBeforeRemoval) {
        return rateLines(lines, removal, children, rating(lines, true));
    }

    private Rating rating(Table lines, boolean beforeRemoval) {
        final Rating rating;
        final int actualSize;
        if (beforeRemoval) {
            actualSize = lines.size() - 1;
        } else {
            actualSize = lines.size();
        }
        if (actualSize == 0) {
            rating = noCost();
        } else if (actualSize < minimalSize) {
            final int difference = minimalSize - actualSize;
            rating = cost(difference / ((double) actualSize));
        } else {
            rating = noCost();
        }
        return rating;
    }

    @Override
    public Class<? extends Rater> type() {
        return HasMinimalSize.class;
    }

    @Override
    public List<Domable> arguments() {
        return list(() -> Xml.elementWithChildren(HasMinimalSize.class.getSimpleName(), Xml.textNode("" + minimalSize)));
    }

    @Override
    public boolean equals(Object arg) {
        if (arg != null && arg instanceof HasMinimalSize) {
            return this.minimalSize == ((HasMinimalSize) arg).minimalSize;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return CommonFunctions.hashCode(minimalSize);
    }

    @Override
    public void addContext(Discoverable context) {
        contexts.add(context);
    }

    @Override
    public Collection<List<String>> paths() {
        return contexts.stream().map(Discoverable::path).collect(toList());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ", " + minimalSize;
    }
}
