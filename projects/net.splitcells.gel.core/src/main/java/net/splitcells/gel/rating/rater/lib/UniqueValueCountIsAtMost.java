/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.rating.rater.lib;

import lombok.val;
import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.Sets;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.data.view.Line;
import net.splitcells.gel.data.view.View;
import net.splitcells.gel.data.view.attribute.Attribute;
import net.splitcells.gel.rating.framework.Rating;
import net.splitcells.gel.rating.rater.framework.Rater;
import net.splitcells.gel.rating.rater.framework.RatingEvent;

import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.constraint.Constraint.*;
import static net.splitcells.gel.rating.framework.LocalRatingI.localRating;
import static net.splitcells.gel.rating.rater.framework.RatingEventI.ratingEvent;
import static net.splitcells.gel.rating.rater.lib.LineGroupRater.lineGroupRater;
import static net.splitcells.gel.rating.type.Cost.cost;
import static net.splitcells.gel.rating.type.Cost.noCost;

public class UniqueValueCountIsAtMost implements GroupingRater {
    public static Rater uniqueValueCountIsAtMost(int argMaxCount
            , Attribute<? extends Object> argAttribute) {
        return lineGroupRater(new UniqueValueCountIsAtMost(argMaxCount, argAttribute));
    }

    private final int maxCount;
    private final Attribute<? extends Object> attribute;

    private UniqueValueCountIsAtMost(int argMaxCount, Attribute<? extends Object> argAttribute) {
        maxCount = argMaxCount;
        attribute = argAttribute;
    }

    @Override public RatingEvent rating(View lines, List<Constraint> children) {
        final Set<Object> distinctValues = setOfUniques();
        distinctValues.addAll(lines.columnView(attribute).values());
        val ratingEvent = ratingEvent();
        val incomingConstraintGroup = lines.unorderedLinesStream2().findFirst().orElseThrow()
                .value(INCOMING_CONSTRAINT_GROUP);
        Rating rating;
        if (distinctValues.size() > maxCount) {
            rating = cost((double) (distinctValues.size() - maxCount) / distinctValues.size());
        } else {
            rating = noCost();
        }
        lines.unorderedLinesStream2().forEach(line -> ratingEvent.additions().put(line, localRating()
                .withPropagationTo(children)
                .withResultingGroupId(incomingConstraintGroup)
                .withRating(rating)));
        return ratingEvent;
    }

    /**
     *
     * @param groupsLineProcessing Corresponds to {@link Constraint#lineProcessing()} for one group.
     * @return
     */
    private int numberOfUniqueValues(View groupsLineProcessing) {
        final Set<Object> distinctValues = setOfUniques();
        groupsLineProcessing.columnView(LINE).values().stream()
                .map(lp -> lp.value(attribute))
                .forEach(distinctValues::add);
        return distinctValues.size();
    }

    @Override public String toSimpleDescription(Line line, View groupsLineProcessing, GroupId incomingGroup) {
        if (groupsLineProcessing.persistedLookup(LINE, line).unorderedLine(0).value(RATING).equalz(noCost())) {
            return "has at most " + maxCount + " of unique values of " + attribute.name();
        } else {

            return "should have at most "
                    + maxCount
                    + " of unique values of "
                    + attribute.name()
                    + " but has "
                    + numberOfUniqueValues(groupsLineProcessing) + " unique value";
        }
    }

    @Override public List<Domable> arguments() {
        return list();
    }
}
