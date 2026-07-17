/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.rating.framework;

import net.splitcells.dem.lang.annotations.ReturnsThis;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.data.order.PartiallyOrdered;
import net.splitcells.dem.object.Base;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.rating.rater.framework.Rater;

import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;

/**
 * TODO Make Rating immutable.
 */
public interface Rating extends PartiallyOrdered<Rating>, Domable, Base {

    @ReturnsThis
    <R extends Rating> R combine(Rating... additionalRatings);

    default MetaRating asMetaRating() {
        return MetaRatingI.metaRating().combine(this);
    }

    <R extends Rating> R _clone();

    boolean betterThan(Rating rating);

    default boolean betterThanOrEquals(Rating rating) {
        return this.betterThan(rating) || this.equalz(rating);
    }

    default String descriptionForUser() {
        throw notImplementedYet();
    }

    default void requireVerySimilar(Rating arg) {
        requireEqualsTo(arg);
    }

    /**
     *
     * @return Returns a description on the {@link Rating}
     * that is used for {@link Rater#descriptivePathName()} in {@link Constraint#path()}.
     * Only use upper and lower case characters, numbers and hyphens.
     * Avoid whitespaces.
     */
    default String descriptivePathName() {
        return toString();
    }
}
