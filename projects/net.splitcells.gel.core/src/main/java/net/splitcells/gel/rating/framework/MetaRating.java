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

import static net.splitcells.gel.rating.framework.MetaRatingI.metaRating;
import static net.splitcells.gel.rating.type.Cost.noCost;

import net.splitcells.dem.data.set.map.Map;

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
