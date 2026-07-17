/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.rating.framework;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.constraint.Constraint;

public class LocalRatingI implements LocalRating {

    private GroupId resultingConstraintGroupId;
    private Rating rating;
    private List<Constraint> propagationTo;

    public static LocalRating localRating() {
        return new LocalRatingI();
    }

    private LocalRatingI() {

    }

    @Override
    public GroupId resultingConstraintGroupId() {
        return resultingConstraintGroupId;
    }

    @Override
    public Rating rating() {
        return rating;
    }

    @Override
    public List<Constraint> propagateTo() {
        return propagationTo;
    }

    @Override
    public LocalRatingI withPropagationTo(List<Constraint> propagationTo) {
        this.propagationTo = propagationTo;
        return this;
    }

    @Override
    public LocalRatingI withRating(Rating rating) {
        this.rating = rating;
        return this;
    }

    @Override
    public LocalRatingI withResultingGroupId(GroupId resultingConstraintGroupId) {
        this.resultingConstraintGroupId = resultingConstraintGroupId;
        return this;
    }

}
