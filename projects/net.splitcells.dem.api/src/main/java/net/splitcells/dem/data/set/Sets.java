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
package net.splitcells.dem.data.set;

import net.splitcells.dem.environment.resource.ResourceOptionImpl;
import net.splitcells.dem.lang.annotations.JavaLegacy;

import static net.splitcells.dem.Dem.configValue;
import static net.splitcells.dem.data.set.SetFactoryImplConfigured.setFiConfigured;
import static net.splitcells.dem.data.set.SetLegacyWrapper.setLegacyWrapper;
import static net.splitcells.dem.environment.config.StaticFlags.INLINE_STANDARD_FACTORIES;

public class Sets extends ResourceOptionImpl<SetF> {
    public Sets() {
        super(() -> setFiConfigured());
    }

    @JavaLegacy
    public static <T> java.util.stream.Collector<T, ?, Set<T>> toSetOfUniques() {
        return java.util.stream.Collector.of(
                Sets::<T>setOfUniques,
                Set::addAll,
                (a, b) -> {
                    a.addAll(b);
                    return a;
                }
        );
    }

    @JavaLegacy
    @SafeVarargs
    public static <T> Set<T> merge(java.util.Collection<T>... collections) {
        final var rVal = configValue(Sets.class).<T>set();
        for (java.util.Collection<T> collection : collections) {
            rVal.addAll(collection);
        }
        return rVal;
    }

    public static <T> Set<T> setOfUniques() {
        if (INLINE_STANDARD_FACTORIES) {
            return setLegacyWrapper(new java.util.LinkedHashSet<>());
        } else {
            return configValue(Sets.class).<T>set();
        }
    }

    @JavaLegacy
    @SafeVarargs
    public static <T> Set<T> setOfUniques(T... args) {
        if (INLINE_STANDARD_FACTORIES) {
            return setLegacyWrapper(new java.util.LinkedHashSet<T>()).with(args);
        } else {
            return setOfUniques(java.util.Arrays.asList(args));
        }
    }

    public static <T> Set<T> setOfUniques(java.util.Collection<T> arg) {
        if (INLINE_STANDARD_FACTORIES) {
            return setLegacyWrapper(new java.util.LinkedHashSet<T>()).with(arg);
        } else {
            final var rVal = configValue(Sets.class).<T>set();
            rVal.addAll(arg);
            return rVal;
        }
    }
}
