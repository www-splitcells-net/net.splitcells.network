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
import static net.splitcells.dem.lang.perspective.PerspectiveI.perspective;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.dem.lang.Xml.elementWithChildren;
import static net.splitcells.dem.data.order.Ordering.EQUAL;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.gel.rating.framework.MetaRatingMergerI.metaRatingMerger;
import static net.splitcells.gel.rating.framework.RatingTranslatorI.ratingTranslator;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

import net.splitcells.dem.lang.perspective.Perspective;
import net.splitcells.dem.utils.CommonFunctions;
import net.splitcells.gel.rating.type.Cost;
import net.splitcells.gel.rating.type.Optimality;
import net.splitcells.gel.rating.type.Profit;
import org.w3c.dom.Node;
import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.data.order.Ordering;
import net.splitcells.dem.data.set.map.Map;

public class MetaRatingI implements MetaRating {
    /**
     * TODO Use dedicated typed mapping interface, in order to move type unsafe
     * operations to a dedicated place.
     */
    private final Map<Class<? extends Rating>, Rating> ratingMap;
    private final RatingTranslator translator;
    private final MetaRatingMerger merger;

    public static MetaRating metaRating(Map<Class<? extends Rating>, Rating> rating) {
        return new MetaRatingI(rating);
    }

    public static MetaRating metaRating() {
        return new MetaRatingI();
    }

    public static MetaRating metaRating(Rating... ratings) {
        final Map<Class<? extends Rating>, Rating> ratingMap = map();
        list(ratings).forEach(rating -> ratingMap.put(rating.getClass(), rating));
        final MetaRatingI metaRating = new MetaRatingI(ratingMap);
        return metaRating;
    }

    private MetaRatingI() {
        this(map());
    }

    @SuppressWarnings("unlikely-arg-type")
    private MetaRatingI(Map<Class<? extends Rating>, Rating> ratingMap) {
        this.ratingMap = ratingMap;
        translator = ratingTranslator(ratingMap);
        merger = metaRatingMerger(ratingMap);
        /**
         * Combine multiple simple {@link Rating} with ar {@link MetaRating}.
         */
        registerMerger(
                (base, addition) -> base.isEmpty()
                        && addition.size() == 1
                        && addition.values().iterator().next() instanceof MetaRating
                        && ((MetaRating) addition.values().iterator().next()).content().size() == 1
                , (base, addition) -> {
                    MetaRating metaAddition
                            = (MetaRating) addition.values().iterator().next();
                    final Map<Class<? extends Rating>, Rating> metaRating = map();
                    metaRating.put(metaAddition.content().keySet().iterator().next()
                            , metaAddition.content().values().iterator().next());
                    return metaRating;
                }
        );
        /**
         * Combine 2 primitive {@link Rating}.
         */
        registerMerger(
                (base, addition) -> base.size() == 1
                        && addition.size() == 1
                        && !(addition.values().iterator().next() instanceof MetaRating)
                        && !(base.values().iterator().next() instanceof MetaRating)
                , (base, addition) -> {
                    final Map<Class<? extends Rating>, Rating> metaRating = map();
                    final Rating primitiveAddition = base.values().iterator().next()
                            .combine(addition.values().iterator().next());
                    metaRating.put(primitiveAddition.getClass()
                            , primitiveAddition);
                    return metaRating;
                }
        );
        /**
         * Combine primitive {@link Rating} with {@link MetaRating}.
         */
        registerMerger(
                (base, addition) -> base.size() == 1
                        && addition.size() == 1
                        && !(base.values().iterator().next() instanceof MetaRating)
                        && addition.values().iterator().next() instanceof MetaRating
                , (base, addition) -> {
                    final Map<Class<? extends Rating>, Rating> metaRating = map();
                    Rating primitiveAddition = base.values().iterator().next()
                            .combine(((MetaRating)
                                    addition.values().iterator().next()).content().values().iterator().next());
                    metaRating.put(primitiveAddition.getClass()
                            , primitiveAddition);
                    return metaRating;
                }
        );
        /**
         * Combine primitive {@link Rating} with neutral element.
         */
        registerMerger(
                (base, addition) -> base.isEmpty()
                        && addition.size() == 1
                        && !(addition.values().iterator().next() instanceof MetaRating)
                , (base, addition) -> {
                    final Map<Class<? extends Rating>, Rating> metaRating = map();
                    final var ratingClass = addition.keySet().iterator().next();
                    /**
                     * Without usage of {@link net.splitcells.dem.object.DeepCloneable#deepClone}
                     * negative side effects would be created.
                     */
                    metaRating.put(ratingClass, addition.get(ratingClass));
                    return metaRating;
                }
        );
    }

    @Override
    public <R extends Rating> R combine(Rating... additionalRatings) {
        return (R) merger.combine(additionalRatings);
    }

    @Override
    public <R extends Rating> R translate(Class<R> type) {
        if (ratingMap.size() == 1) {
            if (ratingMap.containsKey(Profit.class)) {
                return (R) Profit.profit(getContentValue(Profit.class).value());
            }
        }
        return (R) this;
    }

    @Override
    public <T extends Rating> void registerMerger
            (BiPredicate<Map<Class<? extends Rating>, Rating>, Map<Class<? extends Rating>, Rating>> condition
                    , BiFunction
                     <Map<Class<? extends Rating>, Rating>
                             , Map<Class<? extends Rating>, Rating>
                             , Map<Class<? extends Rating>, Rating>> combiner) {
        this.merger.registerMerger(condition, combiner);
    }

    @Override
    public void registerTranslator
            (Class<? extends Rating> targetType
                    , Predicate<Map<Class<? extends Rating>, Rating>> condition
                    , Function<Map<Class<? extends Rating>, Rating>, Rating> translator) {
        this.translator.registerTranslator(targetType, condition, translator);
    }

    @Override
    public boolean equals(Object arg) {
        if (arg instanceof Rating) {
            final var equality = compare_partially_to((Rating) arg);
            if (equality.isEmpty()) {
                return false;
            }
            return equality.get().equals(EQUAL);
        }
        throw notImplementedYet();
    }

    @Override
    public int hashCode() {
        return CommonFunctions.hashCode(ratingMap, translator, merger);
    }

    @Override
    public Optional<Ordering> compare_partially_to(Rating arg) {
        if (arg instanceof MetaRating) {
            final MetaRating other = (MetaRating) arg;
            /* TODO This block is a hack patch.
             * if (!translator.equals(other.translator()) || !merger.equals(other.merger())) {
             *    return Optional.empty();
             * }*/
            if (other.content().isEmpty() && ratingMap.isEmpty()) {
                return Optional.of(EQUAL);
            }
            if (other.content().size() == 1 && ratingMap.size() == 1) {
                return ratingMap.values().iterator().next()
                        .compare_partially_to(other.content().values().iterator().next());
            }
        }
        if (arg instanceof Profit) {
            if (content().containsKey(Profit.class)) {
                return this.getContentValue(Profit.class).compare_partially_to(arg);
            }
            throw notImplementedYet();
        }
        if (arg instanceof Cost) {
            if (content().containsKey(Cost.class)) {
                return this.getContentValue(Cost.class).compare_partially_to(arg);
            }
            if (content().isEmpty() && 0 == ((Cost) arg).value()) {
                return Optional.of(EQUAL);
            }
            if (content().containsKey(Optimality.class)) {
                return this.getContentValue(Optimality.class).compare_partially_to(arg);
            }
            throw notImplementedYet();
        }
        if (arg instanceof Optimality) {
            if (content().containsKey(Cost.class)) {
                return this.getContentValue(Cost.class).compare_partially_to(arg);
            }
        }
        throw notImplementedYet();
    }

    @Override
    public Map<Class<? extends Rating>, Rating> content() {
        return ratingMap;
    }

    @Override
    public <R extends Rating> R _clone() {
        final MetaRatingI clone = new MetaRatingI();
        clone.content().forEach((key, value) -> {
            clone.content().put(key, value._clone());
        });
        return (R) clone;
    }

    @Override
    public boolean betterThan(Rating rating) {
        return smallerThan(rating);
    }

    @Override
    public Perspective toPerspective() {
        if (1 == ratingMap.size()) {
            return ratingMap.values().iterator().next().toPerspective();
        }
        return perspective(MetaRating.class.getSimpleName());
    }

    @Override
    public String toString() {
        return toPerspective().toXmlString();
    }

    @Override
    public RatingTranslator translator() {
        return translator;
    }

    @Override
    public MetaRatingMerger merger() {
        return merger;
    }
}
