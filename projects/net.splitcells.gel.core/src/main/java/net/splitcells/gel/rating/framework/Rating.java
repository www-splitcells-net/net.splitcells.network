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
package net.splitcells.gel.rating.framework;

import net.splitcells.dem.lang.annotations.ReturnsThis;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.data.order.PartiallyOrdered;
import net.splitcells.dem.object.Base;

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
}
