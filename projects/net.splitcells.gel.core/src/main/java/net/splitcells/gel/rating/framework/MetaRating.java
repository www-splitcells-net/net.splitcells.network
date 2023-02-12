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

import static net.splitcells.gel.rating.framework.MetaRatingI.metaRating;
import static net.splitcells.gel.rating.type.Cost.noCost;

import net.splitcells.dem.data.set.map.Map;

/**
 * <p>Use {@link MetaRating} as sparingly as possible,
 * because it adds significant overhead.
 * Prefer using simple {@link Rating}s such as {@link net.splitcells.gel.rating.type.Cost},
 * if there is no concrete need for {@link MetaRating}s.</p>
 * <p>This {@link Rating} instance consists of multiple rating instances.
 * The complete rating is represented by the combination of all
 * {@link #content()} {@link Rating}s.
 * Every element of {@link #content()} can represent the rating
 * of the subject,
 * but each element has its own informational content.
 * </p>
 * <p>No element of {@link #content()} is allowed to conflict with an other
 * element.
 * For instance, a {@link MetaRating} is not allowed to contain the sub rating
 * {@link net.splitcells.gel.rating.type.Compliance} of true
 * and {@link net.splitcells.gel.rating.type.Optimality} of 0,
 * because an {@link net.splitcells.gel.rating.type.Compliance} of true
 * implies an {@link net.splitcells.gel.rating.type.Optimality} of 1.
 * </p>
 */
public interface MetaRating extends Rating, RatingTranslator, MetaRatingMerger {
    static MetaRating neutral() {
        final MetaRating neutral = metaRating();
        neutral.combine(noCost());
        return neutral;
    }

    Map<Class<? extends Rating>, Rating> content();

    @SuppressWarnings("unchecked")
    default <T> T getContentValue(Class<? extends T> tips) {
        return (T) content().get(tips);
    }

    @Override
    default MetaRating asMetaRating() {
        return this;
    }

    RatingTranslator translator();

    MetaRatingMerger merger();
}
