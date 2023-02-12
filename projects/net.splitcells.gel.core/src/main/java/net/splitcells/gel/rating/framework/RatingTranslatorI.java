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

import static net.splitcells.dem.data.set.map.Maps.map;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import net.splitcells.dem.data.set.map.Map;

public class RatingTranslatorI implements RatingTranslator {
    private final Map<Class<? extends Rating>, Rating> ratings;

    public static RatingTranslator ratingTranslator(Map<Class<? extends Rating>, Rating> ratings) {
        return new RatingTranslatorI(ratings);
    }

    private RatingTranslatorI(Map<Class<? extends Rating>, Rating> ratings) {
        this.ratings = ratings;
    }

    private final Map<Class<? extends Rating>
            , Map<Predicate<Map<Class<? extends Rating>, Rating>>
            , Function<Map<Class<? extends Rating>, Rating>, Rating>>> translators = map();

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Rating> T translate(Class<T> target) {
        if (ratings.containsKey(target)) {
            return (T) ratings.get(target);
        }
        Function<Map<Class<? extends Rating>, Rating>, Rating> fittingTranslator;
        final var translatorCandidate
                = classesOf(target).stream()
                .filter(i -> translators.containsKey(i))
                .map(i -> translators.get(i).entrySet()).flatMap(i -> i.stream())
                .filter(i -> i.getKey().test(ratings))
                .map(i -> i.getValue())
                .findFirst();
        fittingTranslator = translatorCandidate.orElseThrow();
        return (T) fittingTranslator.apply(ratings);
    }

    @Override
    public void registerTranslator(Class<? extends Rating> targetType
            , Predicate<Map<Class<? extends Rating>, Rating>> condition
            , Function<Map<Class<? extends Rating>, Rating>, Rating> translator) {
        if (!translators.containsKey(targetType)) {
            translators.put(targetType, map());
        }
        translators.get(targetType).put(condition, translator);
    }

    public static List<Class<?>> classesOf(Class<?> clazz) {
        final List<Class<?>> rVal = new ArrayList<>();
        rVal.add(clazz);
        Class<?> superclass = clazz.getSuperclass();
        while (superclass != null) {
            rVal.add(superclass);
            clazz = superclass;
            superclass = clazz.getSuperclass();
        }
        return rVal;
    }
}
