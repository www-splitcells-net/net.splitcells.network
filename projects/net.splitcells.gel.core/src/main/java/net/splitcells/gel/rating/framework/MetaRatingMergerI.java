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

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.data.set.map.Maps.typeMapping;
import static net.splitcells.dem.lang.perspective.TreeI.perspective;
import static net.splitcells.dem.testing.Assertions.requireNotNull;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.perspective.Tree;
import net.splitcells.dem.utils.StreamUtils;
import net.splitcells.dem.data.order.Ordering;
import net.splitcells.dem.data.set.map.Map;

public class MetaRatingMergerI implements MetaRatingMerger {
    private final Map<Class<? extends Rating>, Rating> ratings;
    private final Map<BiPredicate
            <Map
                    <Class<? extends Rating>, Rating>
                    , Map<Class<? extends Rating>, Rating>>
            , BiFunction
            <Map<Class<? extends Rating>, Rating>
                    , Map<Class<? extends Rating>, Rating>
                    , Map<Class<? extends Rating>, Rating>>> combiners = map();

    public static MetaRatingMerger metaRatingMerger
            (Map<Class<? extends Rating>, Rating> ratings) {
        return new MetaRatingMergerI(ratings);
    }

    private MetaRatingMergerI(Map<Class<? extends Rating>, Rating> ratings) {
        requireNotNull(ratings);
        this.ratings = ratings;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <R extends Rating> R combine(Rating... additionalRatings) {
        // TODO Make it immutble.
        final var additionalRatingMap = typeMapping(simplifyMoreEfficiently(additionalRatings));
        return (R) MetaRatingI.metaRating(combiners.entrySet().stream()
                .filter(combiner -> combiner.getKey().test(ratings, additionalRatingMap))
                .findFirst()
                .orElseThrow()
                .getValue()
                .apply(ratings, additionalRatingMap));
    }

    @Override
    public <T extends Rating> void registerMerger
            (BiPredicate<Map<Class<? extends Rating>, Rating>, Map<Class<? extends Rating>, Rating>> condition
                    , BiFunction<Map<Class<? extends Rating>, Rating>, Map<Class<? extends Rating>, Rating>, Map<Class<? extends Rating>, Rating>> combiner) {
        if (combiners.containsKey(condition)) {
            throw new IllegalArgumentException();
        }
        combiners.put(condition, combiner);
    }

    /**
     * {@link #simplify(Rating...)} has bad performance and
     * the act in itself might not yield better performance because of
     * {@link #combiners}.
     * For the N Queen problem {@link #simplifyMoreEfficiently} yielded better
     * performance.
     *
     * @param ratings Ratings To Simplify
     * @return Simplified Ratings
     */
    private static List<Rating> simplifyMoreEfficiently(Rating... ratings) {
        return list(ratings);
    }

    /*
     * TODO REMOVE
     */
    @Deprecated
    private static List<Rating> simplify(Rating... ratings) {
        return list(ratings).stream().flatMap(rating -> {
            if (rating instanceof MetaRating) {
                return ((MetaRating) rating).content().values().stream();
            }
            return StreamUtils.streamOf(rating);
        }).collect(toList());
    }

    @Override
    public <R extends Rating> R _clone() {
        throw notImplementedYet();
    }

    @Override
    public boolean betterThan(Rating rating) {
        return smallerThan(rating);
    }

    @Override
    public Optional<Ordering> compare_partially_to(Rating arg) {
        throw notImplementedYet();
    }

    @Override
    public Tree toPerspective() {
        return perspective(MetaRatingMerger.class.getSimpleName());
    }
}
