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

import java.util.function.BiFunction;
import java.util.function.BiPredicate;

import net.splitcells.dem.data.set.map.Map;

/**
 * TODO This interface was created in order to split up the implementation of
 * {@link MetaRating}.
 * It was done in a bad way, because the split between {@link MetaRating} and
 * {@link MetaRatingMerger} is based on side effects.
 * This whole interface needs to be overdone.
 * Especially the fact, that this interface extends {@link Rating} seems to be
 * a bad idea.
 * Instead this interface probably should be a conversion function API.
 * Also keep in mind, that this interface should not support any mutability,
 * because the meaning of such a mutability seems to be hard to understand.
 *
 */
@Deprecated
public interface MetaRatingMerger extends Rating {
    @Deprecated
    <T extends Rating> void registerMerger(
            BiPredicate
                    <Map<Class<? extends Rating>, Rating>
                            , Map<Class<? extends Rating>, Rating>> condition,
            BiFunction
                    <Map<Class<? extends Rating>, Rating>
                            , Map<Class<? extends Rating>, Rating>
                            , Map<Class<? extends Rating>, Rating>> combiner);
}
