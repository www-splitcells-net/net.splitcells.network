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
package net.splitcells.gel.rating.framework;

import net.splitcells.dem.lang.annotations.ReturnsThis;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.data.order.PartiallyOrdered;

/**
 * TODO Make Rating immutable.
 */
public interface Rating extends PartiallyOrdered<Rating>, Domable {

    @ReturnsThis
    <R extends Rating> R combine(Rating... additionalNovērtējums);

    default MetaRating asMetaRating() {
        return MetaRatingI.metaRating().combine(this);
    }

    <R extends Rating> R _clone();

    boolean betterThan(Rating rating);
}
